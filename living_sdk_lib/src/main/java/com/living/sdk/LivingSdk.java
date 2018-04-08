package com.living.sdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


public class LivingSdk {

    private int width;
    private int height;

    private Activity context;
    private FrameLayout fr;//广告父布局
    private ViewGroup mVg;//APP父布局
    private int x;
    private int y;
    private float w_ratio;
    private float h_ratio;

    private Intent mServiceIntent;

    public static AdListener adlistener = null;//广告开始结束监听

    private AdOnClickListener adClickListener;//广告点击监听

    private LivingSdk() {
    }

    private static LivingSdk instance = new LivingSdk();

    /**
     * 获取SDK实例
     *
     * @return
     */
    public static LivingSdk getInstance() {
        return instance;
    }

    /**
     * 初始化布局信息
     *
     * @param activity    上下文
     * @param vg          父布局
     * @param view_Width  播放器宽
     * @param view_Height 播放器高度
     */
    public void initLayout(Activity activity, ViewGroup vg, int view_Width, int view_Height) {

        context = activity;
        mVg = vg;
        //屏幕的宽高
        width = view_Width;
        height = view_Height;
        //比例系数
        w_ratio = (float) width / 960;
        h_ratio = (float) height / 540;

    }

    /**
     * 屏幕切换
     *
     * @param vg
     * @param view_Width
     * @param view_Height
     */
    public void switchScreen(ViewGroup vg, int view_Width, int view_Height) {
        if (context == null || vg == null)
            return;
        mVg.removeView(fr);
        mVg = vg;
        fr = new FrameLayout(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(view_Width, view_Height);
        vg.addView(fr, lp);
        //屏幕的宽高
        width = view_Width;
        height = view_Height;
        //比例系数
        w_ratio = (float) width / 960;
        h_ratio = (float) height / 540;
        switchAd();
    }

    /**
     * 切换屏幕重新计算广告相关参数
     */
    private void switchAd() {
        if (fr != null) {
            fr.removeAllViews();
            int x = 0;
            int y = 0;
            int img_width = 0;
            int img_height = 0;
            String url = "";

            x = (int) (width * (Float.parseFloat(String.valueOf(x))) / 100);
            y = (int) (height * (Float.parseFloat(String.valueOf(y))) / 100);

            int image_w = (int) (img_width * w_ratio);
            int image_h = (int) (img_height * h_ratio);
            //

            if (x >= width - image_w) {
                x = width - image_w;
            }
            if (y >= height - image_h) {
                y = height - image_h;
            }
            if (adlistener != null) {
                adlistener.AdStart();
            }

            final AdImage adImage = new AdImage(context);
            adImage.setImageResContent(url);
            int paddingX = adImage.setImSize(image_w, image_h, false);

            mVg.removeView(fr);
            fr = new FrameLayout(context);
            FrameLayout.LayoutParams fr_lp = new FrameLayout.LayoutParams(width, height);
            mVg.addView(fr, fr_lp);

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(x, y - paddingX, 0, 0);
            fr.addView(adImage, lp);

            adImage.setAdImageClickListener(new AdImage.AdImageClickListener() {
                @Override
                public void AdImageResClick() {
                    if (adClickListener != null) {

                        adClickListener.adClick("www.baidu.com");
                    }
                }

                //关闭按钮的点击事件
                @Override
                public void AdImageDelClick() {
                    fr.removeView(adImage);
                    if (adImage != null) {
                        adImage.release();
                    }
                }
            });
        }
    }

    /**
     * 初始化直播流服务
     *
     * @param context
     */
    public void initService(Context context, String type) {
        if (!"socket".equals(type) && !"mqtt".equals(type))
            return;
        if ("socket".equals(type))
            mServiceIntent = new Intent(context, SocketService.class);
        else
            mServiceIntent = new Intent(context, MQTTService.class);

        context.startService(mServiceIntent);
        // 注册广播服务
        registerReceiver(context);
    }


    /**
     * 设置广告的回调监听
     *
     * @param adClickListener
     */
    public void setAdOnClickListener(AdOnClickListener adClickListener) {
        this.adClickListener = adClickListener;
    }

    /**
     * 注册接收消息的自定义广播
     */
    private void registerReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.HEART_BEAT_ACTION);
        intentFilter.addAction(Constants.MESSAGE_ACTION);
        context.registerReceiver(mReceiver, intentFilter);
    }

    private AdImage adImage;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (context == null || mVg == null)
                        return;
                    String json = (String) msg.obj;

                    int time = 0;
                    int x = 0;
                    int y = 0;
                    String id = "";

                    String cmd = "show";

                    try {

                        JSONObject jsonObject = new JSONObject(json);
                        time = Integer.parseInt(jsonObject.getString("time"));
                        x = (int) (width * (Float.parseFloat(jsonObject.getString("x"))));
                        y = (int) (height * (Float.parseFloat(jsonObject.getString("y"))));
                        id = jsonObject.getString("id");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(cmd) || TextUtils.isEmpty(id))
                        return;

//                    if ("update".equals(cmd)) {
//                        //请求图片Bean信息
//                        return;
//                    } else
                    if ("show".equals(cmd)) {

                        int img_width = 240;
                        int img_height = 160;

//                        x = (int) (width * (Float.parseFloat(String.valueOf(x))) / 100);
//                        y = (int) (height * (Float.parseFloat(String.valueOf(y))) / 100);
                        /*
                        * 移动端适配代码
					    * */
                        int image_w = (int) (img_width * w_ratio);
                        int image_h = (int) (img_height * h_ratio);
                        //

                        if (x >= width - image_w) {
                            x = width - image_w;
                        }
                        if (y >= height - image_h) {
                            y = height - image_h;
                        }
                        if (adlistener != null) {
                            adlistener.AdStart();
                        }
//                        int index = new Random().nextInt(3);//[0,3)
                        adImage = new AdImage(context);
                        adImage.setImageResContent("http://172.16.100.3/screen/" + id + ".png");
                        int paddingX = adImage.setImSize(image_w, image_h, false);

                        if (fr != null) {
                            mVg.removeView(fr);
                        }
                        fr = new FrameLayout(context);
                        FrameLayout.LayoutParams fr_lp = new FrameLayout.LayoutParams(width, height);
                        mVg.addView(fr, fr_lp);

                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(x, y - paddingX, 0, 0);
                        fr.addView(adImage, lp);
                        //todo 开启定时控制，关闭广告图片
                        mHandler.sendEmptyMessageDelayed(3, time * 1000);

                        adImage.setAdImageClickListener(new AdImage.AdImageClickListener() {
                            @Override
                            public void AdImageResClick() {
                                if (adClickListener != null) {

//                                    adClickListener.adClick("www.baidu.com");
                                }
                            }

                            //关闭按钮的点击事件
                            @Override
                            public void AdImageDelClick() {

                                fr.removeView(adImage);
                                if (adImage != null) {
                                    adImage.release();
                                }
                                adImage = null;
                                fr = null;
                            }
                        });
                    }
                    break;
                case 2:
                    L.e(Constants.TAG, "纯属心跳反馈");
                    break;
                case 3:
                    if (adImage != null && fr != null) {

                        fr.removeView(adImage);
                        if (adImage != null) {
                            adImage.release();
                        }
                        adImage = null;
                    }
                    if (adlistener != null) {
                        adlistener.AdStop();
                    }
                    fr = null;
                    break;
            }
        }
    };

    /**
     * 心跳和内容的广播回调监听
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.MESSAGE_ACTION)) {
                String dataStr = intent.getStringExtra("message");
                Message message = mHandler.obtainMessage();
                message.obj = dataStr;
                message.what = 1;
                mHandler.sendMessage(message);
            } else if (action.equals(Constants.HEART_BEAT_ACTION)) {// 心跳广播
                mHandler.sendEmptyMessage(2);
            }
        }
    };

    /**
     * 释放资源
     *
     * @param context
     */
    public void release(Context context) {
        // 注销广播
        context.unregisterReceiver(mReceiver);
        //停止服务
        if (mServiceIntent != null) {
            context.stopService(mServiceIntent);
        }
    }

}
