package com.example.sockettest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private Intent mServiceIntent;
    private IBackService iBackService;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.img);
        initSocketConnect();

    }

    /**
     */
    private void initSocketConnect() {

        mServiceIntent = new Intent(this, SocketService.class);
        bindService(mServiceIntent, conn, Context.BIND_AUTO_CREATE);
        registerReceiver();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (iBackService != null) {
                    try {
                        iBackService.sendMessage(Constans.SOCKET_CONNECT_KEY);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 3 * 1000);
    }


    private void showImg(ImageView imageView, String data) {
        byte[] bytes = data.getBytes();
        byte[] decode = Base64.decode(bytes, Base64.DEFAULT);
        Bitmap decodeByteArray = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        imageView.setImageBitmap(decodeByteArray);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(mReceiver);
//        unbindService(conn);
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unbindService(conn);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.HEART_BEAT_ACTION);
        intentFilter.addAction(SocketService.MESSAGE_ACTION);
        registerReceiver(mReceiver, intentFilter);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String data = (String) msg.obj;
                    if (null == data || TextUtils.isEmpty(data))
                        return;
                    Log.i(TAG, "data:" + data);
                    String base64Img = Constans.parseImgJson(data);
                    Log.e(TAG, "base64Img:" + base64Img);
                    showImg(img, base64Img);
                    break;
                case 2:
                    break;
            }
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SocketService.MESSAGE_ACTION)) {
                String stringExtra = intent.getStringExtra("message");
                Message message = mHandler.obtainMessage();
                message.obj = stringExtra;
                message.what = 1;
                mHandler.sendMessage(message);
            } else if (action.equals(SocketService.HEART_BEAT_ACTION)) {
                Log.i(TAG, "1111");
            }
        }
    };


    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBackService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBackService = IBackService.Stub.asInterface(service);
        }
    };
}
