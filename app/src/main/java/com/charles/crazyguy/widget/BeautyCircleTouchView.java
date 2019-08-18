package com.charles.crazyguy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 触碰的圆圈
 * */
public class BeautyCircleTouchView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private SurfaceHolder mSurfaceHolder;
    private volatile boolean mStop = false;

    private volatile int touchX, touchY;

    private Handler mDrawHandler;

    private Paint mPaint;

    public BeautyCircleTouchView(Context context) {
        this(context, null);
    }

    public BeautyCircleTouchView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BeautyCircleTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(32);
        mPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.ITALIC));
    }

    @Override
    public void run() {
        if(mDrawHandler != null) {
            mDrawHandler.removeCallbacksAndMessages(null);
            startDrawCircle(0);
        }else{
            Looper.prepare();
            mDrawHandler = new Handler();
            startDrawCircle(0);
            Looper.loop();
        }
    }

    private void startDrawCircle(final int count) {
        Canvas canvas = mSurfaceHolder.lockCanvas();

        switch (count % 3) {
            case 0:{
                mPaint.setColor(0xffff0000);
                break;
            }
            case 1:{
                mPaint.setColor(0xff00ff00);
                break;
            }
            case 2:{
                mPaint.setColor(0xff0000ff);
                break;
            }
        }

        canvas.drawARGB(0xff, 0xff, 0xff, 0xff);
        /** 画一个心 **/
        int i, j;
        double x, y, r;
        for (i = 0; i <= 90; i++) {
            for (j = 0; j <= 90; j++) {
                r = Math.PI / 45 * i * (1 - Math.sin(Math.PI / 45 * j))
                        * 20;
                x = r * Math.cos(Math.PI / 45 * j)
                        * Math.sin(Math.PI / 45 * i) + 320 / 2;
                y = -r * Math.sin(Math.PI / 45 * j) + 400 / 4;
                canvas.drawPoint((float) x + touchX - 160, (float) y + touchY - 200, mPaint);
            }
        }
        /** 绘制文字 **/
        canvas.drawText("Loving You", 75 +touchX - 160 , 400 + touchY - 200, mPaint);

        mSurfaceHolder.unlockCanvasAndPost(canvas);

        mDrawHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!mStop && count < 20) {
                    startDrawCircle(count+1);
                }
            }
        }, 200);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawARGB(0xff, 0xff, 0xff, 0xff);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN : {
                mStop = true;
                break;
            }
            case MotionEvent.ACTION_UP : {
                touchX = (int)event.getX();
                touchY = (int)event.getY();
                mStop = false;
                startDraw();
                break;
            }
        }

        return true;
    }

    private void startDraw() {
        if(mDrawHandler == null) {
            executorService.execute(this);
        } else {
            mDrawHandler.post(this);
        }
    }

    public void stopDraw() {
        executorService.shutdown();
        mDrawHandler.removeCallbacksAndMessages(null);
    }
}
