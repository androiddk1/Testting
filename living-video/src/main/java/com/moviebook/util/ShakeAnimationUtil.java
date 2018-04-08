package com.moviebook.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.LinearInterpolator;


/**
 * 抖动动画工具类
 */
public class ShakeAnimationUtil {

    private static AnimatorSet set = new AnimatorSet();

    /**
     * 开启晃动
     *
     * @param view
     */
    public static void startShake(View view) {

        ObjectAnimator shakeAnimation = shakeAnimator(view, 1.0f, 1400);
        ObjectAnimator silenceAnimation = silenceAnimator(view, 1.0f, 500);
        shakeAnimation.setInterpolator(new LinearInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            private boolean cancel;

            @Override
            public void onAnimationStart(Animator animation) {
                cancel = false;

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!cancel) {
                    set.start();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                cancel = true;

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.play(shakeAnimation).before(silenceAnimation);
        set.start();

    }

    /**
     * 停止晃动
     */
    public static void stopShake() {
        set.cancel();
        set.removeAllListeners();
    }

    /**
     * 晃动属性动画
     *
     * @param view
     * @param shakeFactor
     * @return
     */
    private static ObjectAnimator shakeAnimator(View view, float shakeFactor, long millions) {

        PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, -4f * shakeFactor),
                Keyframe.ofFloat(.2f, -4f * shakeFactor),
                Keyframe.ofFloat(.3f, 4f * shakeFactor),
                Keyframe.ofFloat(.4f, -4f * shakeFactor),
                Keyframe.ofFloat(.5f, 4f * shakeFactor),
                Keyframe.ofFloat(.6f, -4f * shakeFactor),
                Keyframe.ofFloat(.7f, 4f * shakeFactor),
                Keyframe.ofFloat(.8f, -4f * shakeFactor),
                Keyframe.ofFloat(.9f, 4f * shakeFactor),
                Keyframe.ofFloat(1f, 0)

        );
        return ObjectAnimator.ofPropertyValuesHolder(view, pvhRotate).
                setDuration(millions);
    }

    /**
     * 静止属性动画
     *
     * @param view
     * @param shakeFactor
     * @return
     */
    private static ObjectAnimator silenceAnimator(View view, float shakeFactor, long millions) {

        PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, 0f * shakeFactor),
                Keyframe.ofFloat(.2f, 0f * shakeFactor),
                Keyframe.ofFloat(.3f, 0f * shakeFactor),
                Keyframe.ofFloat(.4f, 0f * shakeFactor),
                Keyframe.ofFloat(.5f, 0f * shakeFactor),
                Keyframe.ofFloat(.6f, 0f * shakeFactor),
                Keyframe.ofFloat(.7f, 0f * shakeFactor),
                Keyframe.ofFloat(.8f, 0f * shakeFactor),
                Keyframe.ofFloat(.9f, 0f * shakeFactor),
                Keyframe.ofFloat(1f, 0f)

        );
        return ObjectAnimator.ofPropertyValuesHolder(view, pvhRotate).
                setDuration(millions);
    }
}
