package com.moviebook.yztlive;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.RelativeLayout;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
import com.living.sdk.LivingSdk;


/**
 * Created by yingpu on 2017/11/14.
 */

public class AliYunActivity extends Activity {
    SurfaceView mSurfaceView;
    //    String playUrl = "rtmp://live.hkstv.hk.lxdns.com/live/hks ";
    String playUrl = "rtmp://172.16.100.3/mypull/liveStream";
    LivingSdk livingSdk;
    private RelativeLayout re_vg;
    int width, height;
    AliVcMediaPlayer mPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aliyun);

        AliVcMediaPlayer.init(getApplicationContext(), "");

        //获取视频显示View
        mSurfaceView = findViewById(R.id.surfaceView);
        //创建player对象
        mPlayer = new AliVcMediaPlayer(this, mSurfaceView);
        mPlayer.setMaxBufferDuration(8000);
        // 设置图像适配屏幕，适配最长边
        mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        // 设置图像适配屏幕，适配最短边，超出部分裁剪
        mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
                holder.setKeepScreenOn(true);

                // Important: surfaceView changed from background to front, we need reset surface to mediaplayer.
                // 对于从后台切换到前台,需要重设surface;部分手机锁屏也会做前后台切换的处理
                if (mPlayer != null) {
                    mPlayer.setVideoSurface(mSurfaceView.getHolder().getSurface());
                }

            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                if (mPlayer != null)
                    mPlayer.setSurfaceChanged();
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        mPlayer.prepareAndPlay(playUrl);

        re_vg = findViewById(R.id.re_vg_ali);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        livingSdk = LivingSdk.getInstance();
        livingSdk.initLayout(this, re_vg, width, height);
        livingSdk.initService(this,"mqtt");

    }

    private void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    private void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    private void resume() {
        if (mPlayer != null) {
            mPlayer.play();
        }
    }

    private void destroy() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.destroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //when view goto background,will pausethe player, so we save the player's status here,
        // and when activity resumed, we resume the player's status.
        savePlayerState();
    }

    private void savePlayerState() {
        if (mPlayer.isPlaying()) {
            //we pause the player for not playing on the background
            // 不可见，暂停播放器
            pause();
        }
    }

    @Override
    protected void onDestroy() {
        stop();
        destroy();
        livingSdk.release(this);
        super.onDestroy();

    }
}
