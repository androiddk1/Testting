package com.living.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.io.InputStream;

/**
 * 右上角有删除按钮的容器控件
 */
public class AdImage extends RelativeLayout {

    private ImageView imageViewRes;//内容
    private ImageView imageViewDel;//删除按钮
    private AdImageClickListener listener;//元素点击监听回调
    private Context context;

    public AdImage(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    @SuppressLint("ResourceType")
    private void initView(Context context) {
        imageViewRes = new ImageView(context);
        imageViewRes.setId(111);
        imageViewDel = new ImageView(context);
        //设置删除按钮资源

        //设置字体
        //mTextView = new TextView(context);
        //mTextView.setText("广告");
        //mTextView.setTextSize(10);
        //mTextView.setTextColor(Color.WHITE);
    }

    /**
     * @param width
     * @param height
     * @param delVisible 是否添加删除按钮
     * @return
     */
    public int setImSize(int width, int height, boolean delVisible) {

        int lp_w = (int) (width * 0.11);
        int paddingX = 0;
        if (delVisible) {
            paddingX = lp_w / 2;
        }
        int vg_width = width + paddingX;
        int vg_height = height + paddingX;
        LayoutParams params = new LayoutParams(vg_width, vg_height);//最外层布局宽高
        RelativeLayout layout = new RelativeLayout(context);
        addView(layout, params);

        LayoutParams lp = new LayoutParams(width, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.addView(imageViewRes, lp);

        if (delVisible) {
            LayoutParams lps = new LayoutParams(lp_w, lp_w);
            lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layout.addView(imageViewDel, lps);
        }
        return paddingX;
    }


    /**
     * 图片资源点击和删除按钮点击回调接口
     */
    public interface AdImageClickListener {
        void AdImageResClick();

        void AdImageDelClick();
    }

    public void setAdImageClickListener(AdImageClickListener listener) {
        this.listener = listener;

    }

    public ImageView getResImage() {
        return imageViewRes;
    }

    public ImageView getDeleteImage() {
        return imageViewDel;
    }


    /**
     * 设置内容
     *
     * @param url
     */
    public void setImageResContent(String url) {
        if (url == null || TextUtils.isEmpty(url))
            return;
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                setImageDelContent();
                return false;
            }
        }).into(imageViewRes);
        imageViewRes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.AdImageResClick();
                }
            }
        });

    }

    /**
     * 设置删除按钮
     */
    private void setImageDelContent() {
        Bitmap bit_del = getImageFromAssetsFile("deleted.png");
        if (imageViewDel != null && bit_del != null) {
            imageViewDel.setImageBitmap(bit_del);
            imageViewDel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.AdImageDelClick();
                    }
                }
            });
        }
    }

    /**
     * 从资产目录获取bitmap
     *
     * @param fileName
     * @return
     */
    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        try {
            InputStream is = context.getResources().getAssets().open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }

    public void release() {
        imageViewRes = null;
        imageViewDel = null;
        System.gc();
    }

}
