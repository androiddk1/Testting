package com.yingpu.socket.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends SocketBaseActivity {

    private static final String TAG = "MainActivity";
    MessageBackReciver reciver;
    Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_start = (Button) findViewById(R.id.btn_start);

        reciver = new MessageBackReciver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(SocketService.HEART_BEAT_ACTION)) {
                    Log.i(TAG, "只是心跳！");
                } else {
                    String message = intent.getStringExtra("message");
                    Log.i(TAG, "message:" + message);
                }
            }
        };
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessageDelayed(1, 10 * 1000);
            }
        });


    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    try {
                        if (iBackService == null) {
                            Toast.makeText(MainActivity.this, "null", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        iBackService.sendMessage(Constans.SOCKET_CONNECT_KEY);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    break;
            }
        }
    };

}
