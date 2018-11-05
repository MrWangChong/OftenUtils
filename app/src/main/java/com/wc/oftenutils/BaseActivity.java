package com.wc.oftenutils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.wc.utils.DisplayUtils;

/**
 * 公用Activity，主要用于全屏澄清式状态栏使用
 * <p>
 * 使用etLayoutResID和getContentView返回界面布局
 * 并且需要使用相应的style，比如这里的ActivityTheme
 * 如果是白色界面或者浅色界面
 * 可以使用setStateBarColor来适配不能修改状态栏字体的手机，比如layout.activity_main中的status_view
 * <p>
 * Created by RushKing on 2018/11/5.
 */
public class BaseActivity extends AppCompatActivity {
    private DisplayUtils.NavigationBarContentObserver mObserver;
    private View mNavigationBar;
    private boolean mIsShowing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        DisplayUtils.init(this);
        super.onCreate(savedInstanceState);
        int layoutResID = getLayoutResID();
        View v = getContentView();
        if (layoutResID != 0 || v != null) {
            //将window扩展至全屏，并且不会覆盖状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //避免在状态栏的显示状态发生变化时重新布局
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            final int navigationBarHeight = DisplayUtils.getNavigationBarHeight();
            if (navigationBarHeight != 0) {
                LinearLayout contentView = new LinearLayout(this);
                contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                contentView.setOrientation(LinearLayout.VERTICAL);
                mNavigationBar = new View(this);
                mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mNavigationBar.setBackground(new ColorDrawable(Color.BLACK));
                } else {
                    mNavigationBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                }
                View view = layoutResID == 0 ? v : View.inflate(this, layoutResID, null);
                view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
                contentView.addView(view);
                contentView.addView(mNavigationBar);
                mObserver = new DisplayUtils.NavigationBarContentObserver(new Handler(Looper.getMainLooper()), this);
                mObserver.setOnNavigationBarChangedListener(new DisplayUtils.NavigationBarContentObserver.OnNavigationBarChangedListener() {
                    @Override
                    public void onNavigationBarChanged(boolean isShow) {
                        if (isShow) {
                            mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navigationBarHeight));
                        } else {
                            mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                        }
                    }
                });
                if (DisplayUtils.isNavigationBarShowing(this)) {
                    mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navigationBarHeight));
                }
//                if (mObserver.isNavigationBarShowing()) {
//                    mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navigationBarHeight));
//                }
                setContentView(contentView);
            } else {
                if (layoutResID == 0) {
                    setContentView(v);
                } else {
                    setContentView(layoutResID);
                }
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initData();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
        initData();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initView();
        initData();
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsShowing = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsShowing = false;
    }

    protected void initView() {

    }

    protected void initData() {
    }

    protected View getContentView() {
        return null;
    }

    protected int getLayoutResID() {
        return 0;
    }

    public void setStateBarColor() {
        View v_state_bar = findViewById(R.id.status_view);
        if (v_state_bar != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                v_state_bar.setVisibility(View.INVISIBLE);
                DisplayUtils.setStatusBarTextColor(this, true);
            } else {
                v_state_bar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mObserver != null) {
            mObserver.destroy();
        }
    }
}
