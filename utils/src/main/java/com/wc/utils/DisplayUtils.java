package com.wc.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 屏幕信息工具类
 * Created by RushKing on 2018/7/16.
 */
public class DisplayUtils {
    private static boolean mHasInit = false;
    private static int sScreenHeight;
    private static int sScreenWidth;
    private static int sScreenTitleHeight;
    private static int sNavigationBarHeight;

    private static float sDensity;
    private static float sScaledDensity;

    private DisplayUtils() {
    }

    /**
     * 初始化当前屏幕的宽度和高度
     */
    public static void init(Activity context) {
        if (mHasInit) {
            return;
        }
        mHasInit = true;
        sScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        sScreenHeight = getHasVirtualKey(context);
        sScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        sDensity = context.getResources().getDisplayMetrics().density;
        sScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        initStatusHeight(context);
        initNavigationBarHeight(context);
    }

    public static void setStatusBarTextColor(Activity activity, boolean isBlack) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (isBlack) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏黑色字体
            } else {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//恢复状态栏白色字体
            }
        }
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(float dipValue) {
        final float scale = sDensity;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(float pxValue) {
        final float fontScale = sScaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int sp2px(float spValue) {
        final float fontScale = sScaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    public static int px2dip(float pxValue) {
        final float scale = sDensity;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 获取当前屏幕的高度
     */
    public static int getScreenHeight() {
        return sScreenHeight;
    }

    /**
     * 获取当前屏幕的宽度
     */
    public static int getScreenWidth() {
        return sScreenWidth;
    }

    /**
     * 获取当前屏幕的状态栏高度
     */
    public static int getStatusHeight() {
        return sScreenTitleHeight;
    }

    /**
     * 获取当前屏幕的虚拟按键高度
     *
     * @return sNavigationBarHeight 虚拟按键高度
     */
    public static int getNavigationBarHeight() {
        return sNavigationBarHeight;
    }

    public static boolean isSoftShowing(Window window) {
        if (window == null) {
            return false;
        }
        Rect rect = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        return sScreenHeight - rect.bottom - sNavigationBarHeight != 0;
    }

    /**
     * 检测指定activity是否为当前显示的activity
     *
     * @param context      当前Activity
     * @param activityName 指定Activity
     */
    public static boolean isActivityForGround(Context context, String activityName) {
        if (context == null || activityName == null) return false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
        if (runningTasks == null || runningTasks.size() == 0) return false;
        ActivityManager.RunningTaskInfo cinfo = runningTasks.get(0);
        ComponentName component = cinfo.topActivity;
        return component.getClassName().equals(activityName);

    }

    private static int getHasVirtualKey(Activity context) {
        int dpi = 0;
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        sScreenHeight = dpi;
        return dpi;
    }

    /**
     * 初始化当前屏幕的状态栏高度
     */
    private static void initStatusHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            java.lang.reflect.Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            sScreenTitleHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化当前屏幕的虚拟按键高度
     */
    private static void initNavigationBarHeight(Activity context) {
        if (hasNavigationBar(context)) {
            try {
                @SuppressLint("PrivateApi")
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                java.lang.reflect.Field field = c.getField("navigation_bar_height");
                sNavigationBarHeight = context.getResources().getDimensionPixelSize(Integer.parseInt(field.get(obj).toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            sNavigationBarHeight = 0;
        }
    }

    private static boolean hasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        try {
            Resources rs = context.getResources();
            int id = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            } else {
                return !ViewConfiguration.get(context).hasPermanentMenuKey() && KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            }
            @SuppressLint("PrivateApi")
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }

    public static boolean isNavigationBarShowing(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            //boolean result = realSize.y != size.y;
            int navigationBarIsMin = Settings.Global.getInt(context.getContentResolver(),
                    "navigationbar_is_min", 0);
            int miuiIsMin = Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0);
            return realSize.y != size.y && navigationBarIsMin != 1 && miuiIsMin != 1;
        } else {
            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !menu && !back;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static boolean hasSoftKeys(WindowManager windowManager) {
        Display d = windowManager.getDefaultDisplay();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    public static class NavigationBarContentObserver extends ContentObserver {
        private Context mContext;
        private OnNavigationBarChangedListener mListener;
        private boolean mIsRegister;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public NavigationBarContentObserver(Handler handler, Context context) {
            super(handler);
            mContext = context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor
                        ("navigationbar_is_min"), true, this);
                mIsRegister = true;
            }
        }

        public void setOnNavigationBarChangedListener(OnNavigationBarChangedListener l) {
            mListener = l;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public boolean isNavigationBarShowing() {
            int navigationBarIsMin = Settings.Global.getInt(mContext.getContentResolver(),
                    "navigationbar_is_min", 0);
            return navigationBarIsMin != 1;
        }

        public void destroy() {
            if (mIsRegister) {
                mContext.getContentResolver().unregisterContentObserver(this);
            }
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (mListener != null) {
                mListener.onNavigationBarChanged(isNavigationBarShowing());
            }
        }

        public interface OnNavigationBarChangedListener {
            void onNavigationBarChanged(boolean isShow);
        }
    }
}
