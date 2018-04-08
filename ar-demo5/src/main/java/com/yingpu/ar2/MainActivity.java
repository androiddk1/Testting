package com.yingpu.ar2;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * AR demo 跳转外链启动页
 */
public class MainActivity extends UnityPlayerActivity {

    private RelativeLayout relativeLayout;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = findViewById(R.id.layout_main);

        relativeLayout.addView(mUnityPlayerView);

        mProgressBar = findViewById(R.id.loadingBar);

        mHandler.sendEmptyMessageDelayed(1, 4000);

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressBar.setVisibility(View.GONE);
        }
    };

    public void clickModel(String params) {

        Intent viewIntent = new Intent("android.intent.action.VIEW",
                Uri.parse("https://item.jd.com/1281659.html"));
        startActivity(viewIntent);
//        Toast.makeText(this, params + "", Toast.LENGTH_SHORT).show();
//        UnityPlayer.UnitySendMessage("ImageTarget", "fromAndroid", "这是来自Android的字符串");
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序!", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
