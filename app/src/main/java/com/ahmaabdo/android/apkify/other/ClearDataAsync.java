package com.ahmaabdo.android.apkify.other;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ahmaabdo.android.apkify.R;
import com.ahmaabdo.android.apkify.utils.AppUtils;
import com.ahmaabdo.android.apkify.utils.DialogUtils;
import com.ahmaabdo.android.apkify.utils.RootUtils;

/**
 * Created by Ahmad on Jul 22, 2017.
 */

public class ClearDataAsync extends AsyncTask<Void, String, Boolean> {
    private Context context;
    private Activity activity;
    private MaterialDialog dialog;
    private AppInfo appInfo;

    public ClearDataAsync(Context context, MaterialDialog dialog, AppInfo appInfo) {
        this.context = context;
        this.activity = (Activity) context;
        this.dialog = dialog;
        this.appInfo = appInfo;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean status = false;
        if (AppUtils.checkPermissions(activity) && RootUtils.isRooted()) {
            status = RootUtils.clearDataWithRootPermission(appInfo.getAPK());
        }
        return status;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        dialog.dismiss();
        if (status && RootUtils.isRooted()) {
            DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_clear_data_success_description, appInfo.getName()), null, null, 0).show();
        } else if (!RootUtils.isRooted()) {
            DialogUtils.showTitleContent(context, context.getResources().getString(R.string.dialog_root_required), context.getResources().getString(R.string.dialog_root_required_description));
        } else {
            DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_error), null, null, 0);
        }
    }
}