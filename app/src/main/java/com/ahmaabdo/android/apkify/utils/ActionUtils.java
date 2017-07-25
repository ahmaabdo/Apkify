package com.ahmaabdo.android.apkify.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;

import com.ahmaabdo.android.apkify.R;
import com.ahmaabdo.android.apkify.other.AppInfo;

import java.io.File;

/**
 * Created by Ahmad on Jul 22, 2017.
 */

public class ActionUtils {
    public static boolean open(Context context, AppInfo appInfo) {
        final Intent intent =
                context.getPackageManager().getLaunchIntentForPackage(appInfo.getAPK());
        if (intent != null) {
            context.startActivity(intent);
        } else {
            DialogUtils.showSnackBar((Activity) context,
                    context.getResources().getString(R.string.dialog_no_activity),
                    null,
                    null,
                    0);
        }
        return true;
    }

    public static boolean extract(Context context, AppInfo appInfo) {
        Activity activity = (Activity) context;
        Boolean status = AppUtils.extractFile(appInfo,
                Environment.getExternalStorageDirectory() + "/Apkify");
        if (!AppUtils.checkPermissions(activity) || !status) {
            DialogUtils.showTitleContent(context,
                    context.getResources().getString(R.string.dialog_extract_fail),
                    context.getResources().getString(R.string.dialog_extract_fail_description));
            return false;
        }
        DialogUtils.showSnackBar(activity, String.format(
                context.getResources().getString(R.string.dialog_extract_success_description),
                appInfo.getName(), AppUtils.getApkFilename(appInfo)), context.getResources().getString(R.string.button_undo),
                new File(AppUtils.getCustomAppFolder() + AppUtils.getApkFilename(appInfo)), 1)
                .show();
        return true;
    }

    public static boolean uninstall(Context context, AppInfo appInfo) {
        Activity activity = (Activity) context;
        Boolean status = RootUtils.uninstallWithRootPermission(appInfo.getAPK());
        if (!AppUtils.checkPermissions(activity) || !RootUtils.isRooted() || !status) {
            DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_error), null, null, 0);
            return false;
        }
        DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_reboot), context.getResources().getString(R.string.button_reboot), null, 2).show();
        return true;
    }

    public static boolean share(Context context, AppInfo appInfo) {
        AppUtils.extractFile(appInfo, context.getFilesDir().toString());
        Intent shareIntent =
                AppUtils.getShareIntent(new File(context.getFilesDir() +
                        AppUtils.getApkFilename(appInfo)));
        context.startActivity(
                Intent.createChooser(shareIntent, String.format(context.getResources().getString(R.string.send_to),
                        appInfo.getName())));
        return true;
    }

    public static boolean settings(Context context, AppInfo appInfo) {
        String packageName = appInfo.getAPK();
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);
        return true;
    }

    public static boolean hide(Context context, AppInfo appInfo) {
        Activity activity = (Activity) context;
        AppDbUtils appDbUtils = new AppDbUtils(context);
        boolean status = RootUtils.disableWithRootPermission(appInfo.getAPK(), appDbUtils.checkAppInfo(appInfo, 3));
        if (!AppUtils.checkPermissions(activity) || !RootUtils.isRooted() || !status) {
            DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_error), null, null, 0);
            return false;
        }
        if (!appDbUtils.checkAppInfo(appInfo, 3)) {
            appInfo.setHidden(true);
            appDbUtils.updateAppInfo(appInfo, 3);
        } else {
            appInfo.setHidden(false);
            appDbUtils.updateAppInfo(appInfo, 3);
        }
        DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_reboot), context.getResources().getString(R.string.button_reboot), null, 2).show();
        return true;
    }

    public static boolean disable(Context context, AppInfo appInfo) {
        Activity activity = (Activity) context;
        AppDbUtils appDbUtils = new AppDbUtils(context);
        Boolean status = RootUtils.disableWithRootPermission(appInfo.getAPK(), appDbUtils.checkAppInfo(appInfo, 4));
        if (!AppUtils.checkPermissions(activity) || !RootUtils.isRooted() || !status) {
            DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_error), null, null, 0);
            return false;
        }
        if (!appDbUtils.checkAppInfo(appInfo, 4)) {
            appInfo.setDisabled(true);
            appDbUtils.updateAppInfo(appInfo, 4);
        } else {
            appInfo.setDisabled(false);
            appDbUtils.updateAppInfo(appInfo, 4);
        }
        DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_reboot), context.getResources().getString(R.string.button_reboot), null, 2).show();
        return true;
    }

    public static boolean favorite(Context context, AppInfo appInfo) {
        AppDbUtils appDbUtils = new AppDbUtils(context);
        if (appDbUtils.checkAppInfo(appInfo, 2)) {
            appInfo.setFavorite(false);
            appDbUtils.updateAppInfo(appInfo, 2);
        } else {
            appInfo.setFavorite(true);
            appDbUtils.updateAppInfo(appInfo, 2);
        }
        return true;
    }
}
