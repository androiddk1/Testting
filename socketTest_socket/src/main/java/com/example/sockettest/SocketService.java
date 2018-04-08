package com.example.sockettest;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;


public class SocketService extends Service {

    private static final String TAG = "Socket";
    /**
     */
    private static final long HEART_BEAT_RATE = 20 * 1000;
    /**
     */
    private static final String HOST = "218.241.154.176";
    /**
     */
    public static final int PORT = 8282;
    /**
     */
    public static final String MESSAGE_ACTION = "ORG.YINGPU.MESSAGE_ACTION";
    /**
     */
    public static final String HEART_BEAT_ACTION = "ORG.YINGPU.HEART_BEAT_ACTION";
    /**
     */
    private long sendTime = 0L;

    private WeakReference<Socket> mSocket;

    /**
     */
    private ReadThread mReadThread;
    /**
     */
    private boolean connect = false;
    /**
     */
    ExecutorService threadPool = Executors.newFixedThreadPool(1);
    /**
     */
    private String msg;

//    private IBackService.Stub iBackService = new IBackService.Stub() {
//        @Override
//        public boolean sendMessage(String message) throws RemoteException {
//            return sendMsg(message);
//        }
//    };

    @Override
    public IBinder onBind(Intent arg0) {
//        return (IBinder) iBackService;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new InitSocketThread().start();
    }

    private Handler mHandler = new Handler();

    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
//                boolean isSuccess = sendMsg(Constans.SOCKET_CONNECT_KEY);//
                if (null == mSocket || null == mSocket.get()) {
                    connect = false;
                    return;
                }
                Socket soc = mSocket.get();
                try {
                    if (soc != null && !soc.isClosed() && !soc.isOutputShutdown()) {
                        OutputStream os = soc.getOutputStream();
                        String message = msg + "\r\n";
                        os.write(message.getBytes());
                        os.flush();
                        sendTime = System.currentTimeMillis();//
                        Log.i(TAG, "sendTime" + sendTime);
                        connect = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!connect) {
                    mHandler.removeCallbacks(heartBeatRunnable);
                    mHandler.removeCallbacks(connectRunnable);
                    mReadThread.release();
                    releaseLastSocket(mSocket);
                    new InitSocketThread().start();
                }
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };


    public void sendMsg(String msg) {
        this.msg = msg;
        threadPool.execute(connectRunnable);

    }

    private Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {
            if (null == mSocket || null == mSocket.get()) {
                connect = false;
                return;
            }
            Socket soc = mSocket.get();
            try {
                if (soc != null && !soc.isClosed() && !soc.isOutputShutdown()) {
                    OutputStream os = soc.getOutputStream();
                    String message = msg + "\r\n";
                    os.write(message.getBytes());
                    os.flush();
                    sendTime = System.currentTimeMillis();//
                    Log.i(TAG, "sendTime" + sendTime);
                    connect = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * @throws UnknownHostException
     * @throws IOException
     */
    private void initSocket() throws IOException {
        Log.i(TAG, "service initSocket!");
        Socket socket = new Socket(HOST, PORT);
        mSocket = new WeakReference<Socket>(socket);
        mReadThread = new ReadThread(socket);
        mReadThread.start();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//
    }

    /**
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

    private StringBuffer stringBuffer = new StringBuffer();

    /**
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
            if (null != socket) {
                try {
                    InputStream is = socket.getInputStream();
                    DataInputStream input = new DataInputStream(is);
                    byte[] buffer = new byte[1024 * 100];
                    int length = 0;
                    while (!socket.isClosed() && !socket.isInputShutdown()
                            && isStart && ((length = is.read(buffer)) != -1)) {
                        if (length > 0) {
                            String message = new String(buffer, 0, length, "utf-8");
                            Log.i(TAG, "message:" + message);
                            Log.i(TAG, "message.length():" + message.length());
                            stringBuffer.append(message);
                            if (null == message || TextUtils.isEmpty(message)) {
                                Intent intent = new Intent(HEART_BEAT_ACTION);
                                sendBroadcast(intent);
                            } else {
                                if (!TextUtils.isEmpty(stringBuffer.toString()) && stringBuffer.toString().endsWith("}^n")) {
                                    Intent intent = new Intent(MESSAGE_ACTION);
                                    String data = stringBuffer.toString();
                                    Log.i(TAG, "data:" + data);
                                    Log.i(TAG, "data.length():" + data.length());
                                    intent.putExtra("message", data.substring(data.indexOf("{"), data.length() - 2));
                                    sendBroadcast(intent);
                                    stringBuffer.delete(0, stringBuffer.length());//
                                }
                            }
                        }

                    }
                    input.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(heartBeatRunnable);
        mHandler.removeCallbacks(connectRunnable);
    }
}
