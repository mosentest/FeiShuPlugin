package cn.zipper.feishu.plugin.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C), 2018-2020
 * Author: ziqimo
 * Date: 2020/9/10 5:08 PM
 * Description: https://blog.csdn.net/u013512708/article/details/102919707
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class AccessibilityUtils {

    /**
     * 辅助权限判断是否开启
     *
     * @param ct
     * @param serviceClass
     * @return
     */
    public static boolean hasServicePermission(Context ct, Class serviceClass) {
        int ok = 0;
        try {
            ok = Settings.Secure.getInt(ct.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Exception e) {
            if (ZLog.debug) {
                e.printStackTrace();
            }
        }
        TextUtils.SimpleStringSplitter ms = new TextUtils.SimpleStringSplitter(':');
        if (ok == 1) {
            String settingValue = Settings.Secure.getString(ct.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                ms.setString(settingValue);
                while (ms.hasNext()) {
                    String accessibilityService = ms.next();
                    if (accessibilityService.contains(serviceClass.getSimpleName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 引导用户到系统设置界面开启权限
     *
     * @param ct
     */
    public static void jumpSystemSetting(Context ct) {
        // jump to setting permission
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ct.startActivity(intent);
    }

    public static void openServicePermissionCompat(final Context ct, final Class service) {
        if (hasServicePermission(ct, service)) {
            return;
        }
        preOpenService(ct, service);
    }

    public static void preOpenService(Context ct, Class service) {
        try {
            openServicePermission(ct, service);
        } catch (Exception e) {
            if (ZLog.debug) {
                e.printStackTrace();
            }
            if (!hasServicePermission(ct, service)) {
                jumpSystemSetting(ct);
            }
        }
    }

    public static void openServicePermission(Context ct, Class serviceClass) {
        Set<ComponentName> enabledServices = getEnabledServicesFromSettings(ct, serviceClass);
        if (null == enabledServices) {
            return;
        }
        ComponentName toggledService = ComponentName.unflattenFromString(ct.getPackageName() + "/" + serviceClass.getName());
        final boolean accessibilityEnabled = true;
        enabledServices.add(toggledService);
        // Update the enabled services setting.
        StringBuilder enabledServicesBuilder = new StringBuilder();
        for (ComponentName enabledService : enabledServices) {
            enabledServicesBuilder.append(enabledService.flattenToString());
            enabledServicesBuilder.append(":");
        }
        final int enabledServicesBuilderLength = enabledServicesBuilder.length();
        if (enabledServicesBuilderLength > 0) {
            enabledServicesBuilder.deleteCharAt(enabledServicesBuilderLength - 1);
        }
        Settings.Secure.putString(ct.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, enabledServicesBuilder.toString());
        // Update accessibility enabled.
        Settings.Secure.putInt(ct.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, accessibilityEnabled ? 1 : 0);
    }

    public static Set<ComponentName> getEnabledServicesFromSettings(Context context, Class serviceClass) {
        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null) {
            enabledServicesSetting = "";
        }
        Set<ComponentName> enabledServices = new HashSet<ComponentName>();
        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);
        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);
            if (enabledService != null) {
                if (enabledService.flattenToString().contains(serviceClass.getSimpleName())) {
                    return null;
                }
                enabledServices.add(enabledService);
            }
        }
        return enabledServices;
    }
}
