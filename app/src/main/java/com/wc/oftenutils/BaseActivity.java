package com.wc.oftenutils;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wc.oftenutils.widget.MainLayout;

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
public abstract class BaseActivity extends AppCompatActivity {
    //    private DisplayUtils.NavigationBarContentObserver mObserver;
//    private View mNavigationBar;
    //    private DisplayUtil.NavigationBarContentObserver mObserver;
//    private View mNavigationBar;
    private boolean mIsShowing = false;
    private MainLayout mContentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && mContentView != null) {
            mContentView.setCanRequestLayout(false);
            setContentView(mContentView);
            return;
        }
        super.onCreate(savedInstanceState);
        int layoutResID = getLayoutResID();
        View v = getContentView();
        if (layoutResID != 0 || v != null) {
            boolean isBlackStateTextColor = isBlackStateTextColor();
            if (!isBlackStateTextColor) {
                setWindowInfo(false);
            }
            //将window扩展至全屏，并且不会覆盖状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //避免在状态栏的显示状态发生变化时重新布局
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            if (layoutResID == 0) {
                mContentView = new MainLayout(this);
                mContentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mContentView.addView(v);
                setContentView(mContentView);
            } else {
                mContentView = new MainLayout(this);
                mContentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                View.inflate(this, layoutResID, mContentView);
                setContentView(mContentView);
            }
            if (isBlackStateTextColor) {
                setWindowInfo(true);
            }
//            final int navigationBarHeight = DisplayUtil.getNavigationBarHeight();
//            if (navigationBarHeight != 0) {
//                LinearLayout contentView = new LinearLayout(this);
//                contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                contentView.setOrientation(LinearLayout.VERTICAL);
//                mNavigationBar = new View(this);
//                mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
//                mNavigationBar.setBackground(new ColorDrawable(Color.BLACK));
//                View view = layoutResID == 0 ? v : View.inflate(this, layoutResID, null);
//                view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
//                contentView.addView(view);
//                contentView.addView(mNavigationBar);
//                mObserver = new DisplayUtil.NavigationBarContentObserver(new Handler(Looper.getMainLooper()), this);
//                mObserver.setOnNavigationBarChangedListener(isShow -> {
//                    if (isShow) {
//                        mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navigationBarHeight));
//                    } else {
//                        mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
//                    }
//                });
//                if (DisplayUtil.isNavigationBarShowing(this)) {
//                    mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navigationBarHeight));
//                }
////                if (mObserver.isNavigationBarShowing()) {
////                    mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navigationBarHeight));
////                }
//                setContentView(contentView);
//            } else {
//                if (layoutResID == 0) {
//                    setContentView(v);
//                } else {
//                    setContentView(layoutResID);
//                }
//            }
        }
    }

    private void setWindowInfo(boolean isBlackStateTextColor) {
        Window window = getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.TRANSPARENT);
                int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (isBlackStateTextColor) {
                    View v_state_bar = findViewById(R.id.status_view);
                    if (v_state_bar != null) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            v_state_bar.setVisibility(View.INVISIBLE);
                            visibility = visibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                        } else {
                            v_state_bar.setVisibility(View.VISIBLE);
                        }
                    }
                }
                window.getDecorView().setSystemUiVisibility(visibility);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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

    //是否设置黑色状态栏字体
    public boolean isBlackStateTextColor() {
        return false;
    }

    public View getMainLayout() {
        return mContentView;
    }
}