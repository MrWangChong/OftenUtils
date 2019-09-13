package com.wc.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Toast提示工具类
 * Created by RushKing on 2018/3/22.
 */
public class ToastUtils {
    private final static int RELEASE_TIME = 5000;
    private static int sTime;
    private static ReleaseThread sThread;
    private static Toast sToast;
    private static Object iNotificationManagerObj;

    /**
     * 显示Toast
     *
     * @param context 上下文
     * @param text    显示内容
     */
    @SuppressLint("ShowToast")
    public static void showToast(Context context, CharSequence text) {
        if (context == null || TextUtils.isEmpty(text)) {
            return;
        }
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
        if (sToast == null) {
            if (context instanceof Application) {
                sToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            } else {
                sToast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
            }
        } else {
            if (sToast.getView() == null || "custom".equals(sToast.getView().getTag(R.id.toast_tag))) {
                if (context instanceof Application) {
                    sToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                } else {
                    sToast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
                }
            } else {
                sToast.setText(text);
                sToast.setDuration(Toast.LENGTH_SHORT);
            }
        }
        if (isNotificationEnabled(context)) {
            sToast.show();
        } else {
            showSystemToast(sToast);
        }
        startReleaseThread();
    }

    /**
     * 显示Toast
     *
     * @param context 上下文
     * @param resId   内容资源ID
     */
    @SuppressLint("ShowToast")
    public static void showToast(Context context, @StringRes int resId) {
        if (context == null) {
            return;
        }
        if (sToast == null) {
            if (context instanceof Application) {
                sToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
            } else {
                sToast = Toast.makeText(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
            }
        } else {
            if (sToast.getView() == null || "custom".equals(sToast.getView().getTag(R.id.toast_tag))) {
                if (context instanceof Application) {
                    sToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
                } else {
                    sToast = Toast.makeText(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
                }
            } else {
                sToast.setText(resId);
                sToast.setDuration(Toast.LENGTH_SHORT);
            }
        }
        if (isNotificationEnabled(context)) {
            sToast.show();
        } else {
            showSystemToast(sToast);
        }
        startReleaseThread();
    }

    //开个线程释放资源
    private static void startReleaseThread() {
        sTime = RELEASE_TIME;
        if (sThread == null) {
            sThread = new ReleaseThread();
            new Thread(sThread).start();
        }
    }

    //释放资源线程
    private static class ReleaseThread implements Runnable {

        @Override
        public void run() {
            while (sTime > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sTime -= 1000;
            }
            sThread = null;
            sToast = null;
        }
    }

    /**
     * 显示系统Toast
     */
    private static void showSystemToast(Toast toast) {
        try {
            Method getServiceMethod = Toast.class.getDeclaredMethod("getService");
            getServiceMethod.setAccessible(true);
            //hook INotificationManager
            if (iNotificationManagerObj == null) {
                iNotificationManagerObj = getServiceMethod.invoke(null);

                Class iNotificationManagerCls = Class.forName("android.app.INotificationManager");
                Object iNotificationManagerProxy = Proxy.newProxyInstance(toast.getClass().getClassLoader(), new Class[]{iNotificationManagerCls}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //强制使用系统Toast
                        if ("enqueueToast".equals(method.getName())
                                || "enqueueToastEx".equals(method.getName())) {  //华为p20 pro上为enqueueToastEx
                            args[0] = "android";
                        }
                        return method.invoke(iNotificationManagerObj, args);
                    }
                });
                Field sServiceFiled = Toast.class.getDeclaredField("sService");
                sServiceFiled.setAccessible(true);
                sServiceFiled.set(null, iNotificationManagerProxy);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息通知是否开启
     *
     * @return areNotificationsEnabled
     */
    private static boolean isNotificationEnabled(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        //boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
        return notificationManagerCompat.areNotificationsEnabled();
    }
}
