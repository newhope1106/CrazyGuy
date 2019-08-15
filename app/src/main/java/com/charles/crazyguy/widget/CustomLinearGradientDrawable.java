package com.charles.crazyguy.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * 半屏渐变背景
 * @author Liu Liaopu
 * date 2019/8/15
 */
public class CustomLinearGradientDrawable extends Drawable {
    private static final String TAG = "CustomLinearGradientDrawable";

    /**
     * 各渐变色比例
     * */
    private float[] mRatios;
    /**
     * 渐变色
     * */
    private int[] mColors;

    /**
     * alpha加成
     * */
    private int mAlpha = 0xff;

    /**
     * 画笔
     * */
    private Paint mPaint;

    private int mGradientOrientation = ORIENTATION_HORIZONTAL;

    /**
     * 渐变水平方向
     * */
    public static final int ORIENTATION_HORIZONTAL = 0;

    /**
     * 渐变垂直方向
     * */
    public static final int ORIENTATION_VERTICAL = 1;


    public CustomLinearGradientDrawable() {
        mRatios = new float[]{0f, 0.225f, 0.298f, 1.0f};
        mColors = new int[]{0x00000000, 0x99000000, 0xB3000000, 0xe6000000};

        mPaint = new Paint();
    }

    /**
     * 设置渐变颜色，参数长度要保持一致
     * @param radios 渐变色所占位置比例
     * @param colors 对应比例的颜色(32位 e.g.:0xccffffff)
     * */
    public void setGradientColors(float[] radios, int[] colors) {
        if(radios == null || colors == null || radios.length != colors.length) {
            throw new IllegalArgumentException("radios and colo");
        }

        mRatios = radios;
        mColors = colors;
    }

    /**
     * 设置渐变方向
     * @param orientation 水平{@link #ORIENTATION_HORIZONTAL}、垂直{@link #ORIENTATION_VERTICAL}
     * */
    public void setGradientOrientation(int orientation) {
        if(orientation == ORIENTATION_HORIZONTAL
                || orientation == ORIENTATION_VERTICAL) {
            mGradientOrientation = orientation;
        }
    }

    /**
     * 合并alpha通道加成
     * */
    private void mergeAlphaChannel() {
        if(mColors != null && mColors.length > 0) {
            if(mAlpha >= 0 && mAlpha < 0xff) {
                int size = mColors.length;
                double alphaRatio = (int)(mAlpha*0.1/0xff);
                for(int i=0; i<size; i++) {
                    int alpha =  mColors[i] >> 24;
                    int newAlpha = (int)(alpha * alphaRatio);
                    mColors[i] = (newAlpha << 24) + (mColors[i]<<8);
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        mergeAlphaChannel();
        if(mColors == null) {
            return;
        }

        /*只有一个颜色，就只设置一个颜色*/
        if(mColors.length == 1) {
            mPaint.setColor(mColors[0]);
            canvas.drawRect(getBounds(), mPaint);
        } else {
            Rect rect = getBounds();
            int width = rect.width();
            int height = rect.height();
            /*横向渐变*/
            if(mGradientOrientation == ORIENTATION_HORIZONTAL) {
                LinearGradient linearGradient = new LinearGradient(0, 0, width, 0, mColors, mRatios, Shader.TileMode.CLAMP);
                mPaint.setShader(linearGradient);
                canvas.drawRect(rect, mPaint);
            } else {
                LinearGradient linearGradient = new LinearGradient(0, 0, 0, height, mColors, mRatios, Shader.TileMode.CLAMP);
                mPaint.setShader(linearGradient);
                canvas.drawRect(rect, mPaint);
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
