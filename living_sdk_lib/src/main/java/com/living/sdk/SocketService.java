package com.living.sdk;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SocketService extends Service {

    /**
     * 心跳检测间隔时间
     */
    private static final long HEART_BEAT_RATE = 30 * 1000;

    /**
     * 发送时间
     */
    private long sendTime = 0L;

    private WeakReference<Socket> mSocket;

    /**
     * 接收服务消息线程
     */
    private ReadThread mReadThread;
    /**
     * 心跳包监测的线程池队列
     */
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new InitSocketThread().start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    threadPool.execute(checkConnectRunnable);//心跳监测线程
                    break;
            }
        }
    };

    /**
     * 心跳线程
     */
    private Runnable checkConnectRunnable = new Runnable() {

        private boolean isOk = false;

        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                if (null == mSocket || null == mSocket.get()) {
                    isOk = false;
                    return;
                }
                Socket soc = mSocket.get();
                try {
                    if (soc != null && !soc.isClosed() && !soc.isOutputShutdown()) {
                        OutputStream os = soc.getOutputStream();
                        String message = Constants.SOCKET_CONNECT_KEY + "^n";
                        os.write(message.getBytes());
                        os.flush();
                        sendTime = System.currentTimeMillis();// 每次发送成功数据，就改一下最后成功发送的时间，节省心跳间隔时间
                        L.e(Constants.TAG, "heartBeat sendTime ：" + sendTime);
                        isOk = true;
                    }
                } catch (IOException e) {
                    isOk = false;
                    e.printStackTrace();
                }
                /**
                 * 如果不OK，清理资源，重启socket服务
                 */
                if (!isOk) {
                    mReadThread.release();
                    releaseLastSocket(mSocket);
                    new InitSocketThread().start();
                    mHandler.removeCallbacks(this);
                    mHandler.removeMessages(1);
                }
            }
            mHandler.sendEmptyMessageDelayed(1, HEART_BEAT_RATE);
//            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 初始化socket
     *
     * @throws UnknownHostException
     * @throws IOException
     */
    private void initSocket() throws IOException {
        Socket socket = new Socket(Constants.HOST, Constants.PORT);
        L.e(Constants.TAG, "socket connected!!");
        mSocket = new WeakReference<Socket>(socket);
        mReadThread = new ReadThread(socket);
        mReadThread.start();//读取服务器推送消息线程
        threadPool.execute(checkConnectRunnable);//心跳监测线程
    }

    /**
     * 释放socket
     *
     * @param mSocket
     */
    private void releaseLastSocket(WeakReference<Socket> mSocket) {
        try {
            if (null != mSocket) {
                Socket sk = mSocket.get();
                if (!sk.isClosed()) {
                    sk.close();
                }
                sk = null;
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化socket线程
     */
    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                initSocket();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 服务器消息处理线程
     */
    public class ReadThread extends Thread {

        private WeakReference<Socket> mWeakSocket;
        private boolean isStart = true;

        public ReadThread(Socket socket) {
            mWeakSocket = new WeakReference<Socket>(socket);
        }

        public void release() {
            isStart = false;
            releaseLastSocket(mWeakSocket);
        }

        @SuppressLint("NewApi")
        @Override
        public void run() {
            super.run();
            Socket socket = mWeakSocket.get();
            InputStream is = null;
            DataInputStream input = null;
            if (null != socket) {
                try {
                    socket.setKeepAlive(true);
                    is = socket.getInputStream();
                    input = new DataInputStream(is);
                    byte[] buffer = new byte[1024 * 100];
                    int length = 0;
                    while (!socket.isClosed() && !socket.isInputShutdown()
                            && isStart && ((length = is.read(buffer)) != -1)) {
                        if (length > 0) {
                            String message = new String(buffer, 0, length, "utf-8");
                            L.e(Constants.TAG, "return message:" + message);

                            if (TextUtils.isEmpty(message))
                                continue;

                            if ((Constants.SOCKET_CONNECT_KEY + "^n").equals(message)) {

                                // 心跳回复
                                Intent intent = new Intent(Constants.HEART_BEAT_ACTION);
                                sendBroadcast(intent);
                            } else if (message.contains("time")) {

                                // 正常消息回复
                                Intent intent = new Intent(Constants.MESSAGE_ACTION);
//                                intent.putExtra("message", message);
                                intent.putExtra("message", message.substring(0, message.length() - 2));
                                sendBroadcast(intent);
                            }
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (input != null) {

                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (is != null) {

                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (checkConnectRunnable != null && mHandler != null) {
            mHandler.removeCallbacks(checkConnectRunnable);
        }
        if (mReadThread != null) {
            mReadThread.release();
        }
        stopSelf();
    }
}
