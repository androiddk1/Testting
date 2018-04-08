package com.moviebook.ar_dynamic12;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AR demo 跳转外链启动页
 */
public class MainActivity extends UnityPlayerActivity {

    private RelativeLayout relativeLayout;
    private ProgressBar mProgressBar;
    private Map<String, String> mapUrl = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = findViewById(R.id.layout_main);
        XmlParseUtil util = new XmlParseUtil(this);
        List<ImageTarget> list = util.parse(Environment.getExternalStorageDirectory() + "/ar/urls.xml");
        for (int i = 0; i < list.size(); i++) {
            ImageTarget target = list.get(i);
            mapUrl.put(target.getName(), target.getUrl());
        }
        relativeLayout.addView(mUnityPlayer);
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
        Log.e("ar-demo", params + "-----");
        Intent viewIntent = new Intent("android.intent.action.VIEW",
                Uri.parse(mapUrl.get(params)));
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
