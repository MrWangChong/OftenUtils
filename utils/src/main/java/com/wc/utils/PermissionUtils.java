package com.wc.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 权限跳转和管理工具类
 * Created by RushKing on 2017/11/2.
 */

public class PermissionUtils {

    public static boolean isEnableNotification(Context context, String pkg, int uid) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return isEnableV26(context, pkg, uid);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String CHECK_OP_NO_THROW = "checkOpNoThrow";
                String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
                Class appOpsClass = null;
                AppOpsManager mAppOps = null;
                mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE,
                        Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

                int value = (Integer) opPostNotificationValue.get(Integer.class);
                return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) ==
                        AppOpsManager.MODE_ALLOWED);
            }
        } catch (Exception e) {
            return true;
        }
        return true;
    }

    public static boolean isEnableV26(Context context, String pkg, int uid) {
        try {
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            Method sServiceField = notificationManager.getClass().getDeclaredMethod("getService");
            sServiceField.setAccessible(true);
            Object sService = sServiceField.invoke(notificationManager);

            Method method = sService.getClass().getDeclaredMethod("areNotificationsEnabledForPackage"
                    , String.class, Integer.TYPE);
            method.setAccessible(true);
            return (boolean) method.invoke(sService, pkg, uid);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 检测是否拥有自启权限
     */
    public static boolean checkSelfPrimingPermission(Context context) {
        String mobileType = getMobileType().toLowerCase();
        Log.d("PermissionUtils", "checkSelfPrimingPermission 当前手机型号为：" + mobileType);
        switch (mobileType) {
            case "vivo"://vivo手机
                return checkVivo(context);
            default:
                int i = checkStartupAndWidget(context, context.getPackageName());
                return i == 1 || i == 3;
        }
    }

    public static void jumpNoticePermissionSetting(Activity context) {
        Intent intent = new Intent();
        try {
            String packageName = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            @SuppressLint("WrongConstant")
            ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES);
            Log.d("FriendsAddListActivity", "uid : " + ai.uid);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                //8.0及以后版本使用这两个extra.  >=API 26
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, ai.uid);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", packageName);
                intent.putExtra("app_uid", ai.uid);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + packageName));
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageManager()));
            context.startActivity(intent);
        }
    }

    /**
     * 跳转到手机授权界面
     */
    public static void jumpPermissionSetting(Activity context) {
        Intent intent = new Intent();
        try {
            ComponentName componentName = null;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String mobileType = getMobileType().toLowerCase();
            Log.d("PermissionUtils", "jumpPermissionSetting 当前手机型号为：" + mobileType);
            switch (mobileType) {
                case "xiaomi"://小米手机
                    componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
                    break;
                case "letv"://乐视手机
                    intent.setAction("com.letv.android.permissionautoboot");
                    break;
                case "samsung"://三星手机
                    componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.ram.AutoRunActivity");
                    break;
                case "huawei"://华为手机
                    componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
                    //componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
                    break;
                case "vivo"://vivo手机
                    //componentName = ComponentName.unflattenFromString("com.iqoo.secure/.safeguard.PurviewTabActivity");
                    componentName = ComponentName.unflattenFromString("com.iqoo.secure/.safeguard.SoftPermissionDetailActivity");
                    intent.putExtra("packagename", context.getPackageName());
                    break;
                case "meizu"://魅族手机
                    componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.PermissionMainActivity");
                    break;
                case "oppo"://oppo手机
                    componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
                    break;
                case "ulong"://360手机
                    componentName = new ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun.AutoRunListActivity");
                    break;
                default:
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            }
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {//抛出异常就直接打开设置页面
            intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }

    //获取手机型号
    private static String getMobileType() {
        return Build.MANUFACTURER;
    }

    /**
     * 检查某个包 是否有自启动或者桌面小插件
     *
     * @return 0 木有 1有自启动 2有小插件 3都有
     */
    private static int checkStartupAndWidget(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean haveStartup;
        boolean haveWidget = false;
        try {
            PackageInfo pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_RECEIVERS | PackageManager.GET_META_DATA);// 通过包名，返回包信息
            haveStartup = pm.checkPermission("android.permission.RECEIVE_BOOT_COMPLETED", packageName) == PackageManager.PERMISSION_GRANTED;
            ActivityInfo[] receivers = pkgInfo.receivers;
            if (receivers != null) {
                for (ActivityInfo activityInfo : receivers) {
                    Log.d("PermissionUtils", "rece：" + activityInfo.name);
                    Bundle b = activityInfo.metaData;
                    if (b != null) {
                        if (b.containsKey("android.appwidget.provider")) {
                            haveWidget = true;
                            break;
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
        if (haveStartup && haveWidget)
            return 3;
        if (haveStartup)
            return 1;
        if (haveWidget)
            return 2;
        return 0;
    }

    private static final Uri VIVO_URI = Uri.parse("content://com.iqoo.secure.provider.secureprovider/forbidbgstartappslist");

    //检测vivo手机
    private static boolean checkVivo(Context context) {
        ContentResolver localContentResolver = context.getContentResolver();
        Cursor cursor = localContentResolver.query(VIVO_URI, null, null, null, null);
        if (cursor != null) {
            Log.d("PermissionUtils", "count:" + cursor.getCount());
            while (cursor.moveToNext()) {
                String pkgname = cursor.getString(cursor.getColumnIndex("pkgname"));
                Log.d("PermissionUtils", "pkgname:" + pkgname);
                if (context.getPackageName().equals(pkgname)) {
                    return true;
                }
            }
            cursor.close();
        }
        return false;
    }

    /**
     * 检查是否拥有悬浮到第三方应用之上的权限
     *
     * @param context 上下文
     * @return true:可以悬浮|false:不能悬浮
     */
    public static Boolean checkCanDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(context)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请悬浮到第三方应用之上的权限
     *
     * @param context 上下文
     */
    public static void requestCanDrawOverlays(Context context) {
        if (!checkCanDrawOverlays(context)) {
            requestFloatLayerUpperApi23(context);
        }

    }

    /**
     * 可出现在顶部的应用程序
     * API 23以上调用
     *
     * @param context 上下文
     */
    public static void requestFloatLayerUpperApi23(Context context) {
        Intent intent;
        try {
            intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                context.startActivity(intent);
            } catch (Exception e1) {//抛出异常就打开系统设置页面
                e1.printStackTrace();
                intent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(intent);
            }
        }
    }

}
