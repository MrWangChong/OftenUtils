package com.wc.oftenutils.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class MainLayout extends FrameLayout {
    private boolean isCanRequestLayout;

    public MainLayout(@NonNull Context context) {
        super(context);
    }

    public MainLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MainLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        isCanRequestLayout = true;
    }

    public void setCanRequestLayout(boolean b) {
        isCanRequestLayout = b;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }
}
