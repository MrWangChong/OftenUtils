package com.wc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 电话号码工具类
 * Created by RushKing on 2017/11/2.
 */

public class PhoneUtils {
    /**
     * 判断手机格式是否正确，并且是移动号码
     *
     * @param number 号码
     *               移动：134，135，136，137，138，139，147，150，151，152，157，158，159，178，182，183，184，187，188
     *               联通：130，131，132，145，155，156，176，185，186；
     *               电信：133，153，177，180，181，189；
     */
    public static boolean isMobileNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        String telRegex = "[1]([3][456789]|(47)|[5][012789]|(78)|[8][23478])\\d{8}";
        return number.matches(telRegex);
    }

    /**
     * 手机号验证
     *
     * @return 验证通过返回true
     */
    public static boolean isMobilePhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 座机号验证
     *
     * @return 验证通过返回true
     */
    public static boolean isTelephone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        if (phone.length() > 9) {
            Pattern p = Pattern.compile("^[0][1-9]{2,3}[-, ,0-9][0-9]{5,10}$");  // 验证带区号的
            Matcher m = p.matcher(phone);
            return m.matches();
        } else {
            Pattern p = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
            Matcher m = p.matcher(phone);
            return m.matches();
        }
    }

    /**
     * 格式化号码
     **/
    public static String formatNumber(String number) {
        if (!TextUtils.isEmpty(number)) {
            try {
                //去除号码中非数字的字符
                number = number.replaceAll("\\D", "");
                if (number.startsWith("86")) {
                    number = number.substring(2);
                }
                if (number.startsWith("086")) {
                    number = number.substring(3);
                }
                if (number.startsWith("17951")) {
                    number = number.substring(5);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            number = "";
        }
        return number;
    }

    /**
     * 获取app版本名称
     *
     * @param context 上下文
     * @return 版本名称
     */
    public static String getAppVersion(Context context) {
        PackageInfo info = null;
        try {
            PackageManager manager = context.getApplicationContext().getPackageManager();
            info = manager.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null != info ? info.versionName : "";
    }


    /**
     * 调用拨号界面，不需要权限
     */
    public static void call(Activity context, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    /**
     * 检测Volte是否开启
     *
     * @param context 上下文
     * @return 状态，0 未开启，1 已开启
     */
    public static int checkVoLteState(Context context) {
        if (isVoLteAvailable(context)) {
            return getVoLteState(context);
        }
        return 0;
    }

    //获取VoLte是否可用
    private static boolean isVoLteAvailable(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        boolean isVolteAvailable = false;
        if (telephonyManager != null) {
            try {
                Class<? extends TelephonyManager> telephonyManagerClass = telephonyManager.getClass();
                Method method = telephonyManagerClass.getDeclaredMethod("isVolteAvailable");
                method.setAccessible(true);
                isVolteAvailable = (boolean) method.invoke(telephonyManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isVolteAvailable;
    }

    /**
     * Whether the Volte is enabled
     * Type: int (0 for false, 1 for true)
     */
    private static int getVoLteState(Context context) {
        int voLteState = 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Class<?> globalClass = Settings.Global.class;
                Field field = globalClass.getDeclaredField("ENHANCED_4G_MODE_ENABLED");
                field.setAccessible(true);
                String volteStr = (String) field.get(globalClass);
                voLteState = Settings.Global.getInt(context.getContentResolver(), volteStr);
                //int VoLteState = Settings.Global.getInt(getContentResolver(), "volte_vt_enabled");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return voLteState;
    }
}
