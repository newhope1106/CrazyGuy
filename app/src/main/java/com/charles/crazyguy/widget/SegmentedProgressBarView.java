package com.charles.crazyguy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author 刘辽谱
 * date 2019/8/5
 * 分段进度条
 */
public class SegmentedProgressBarView extends View {
    public SegmentedProgressBarView(Context context) {
        this(context, null);
    }

    public SegmentedProgressBarView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SegmentedProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
