package com.ahmaabdo.android.apkify;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ahmaabdo.android.apkify.other.App;
import com.ahmaabdo.android.apkify.other.AppInfo;
import com.ahmaabdo.android.apkify.other.ClearDataAsync;
import com.ahmaabdo.android.apkify.other.RemoveCacheAsync;
import com.ahmaabdo.android.apkify.utils.ActionUtils;
import com.ahmaabdo.android.apkify.utils.AppDbUtils;
import com.ahmaabdo.android.apkify.utils.AppPreferences;
import com.ahmaabdo.android.apkify.utils.DialogUtils;
import com.ahmaabdo.android.apkify.utils.InterfaceUtils;
import com.ahmaabdo.android.apkify.views.ButtonSwitchView;
import com.ahmaabdo.android.apkify.views.ButtonView;
import com.ahmaabdo.android.apkify.views.InformationView;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Ahmad on Jul 22, 2017.
 */

public class AppActivity extends ThemeActivity {
    private AppPreferences appPreferences;
    private AppDbUtils appDbUtils;
    private Context context;
    private MenuItem favorite;
    private AppInfo appInfo;
    private int UNINSTALL_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appPreferences = App.getAppPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        context = this;

        appDbUtils = new AppDbUtils(context);

        getInitialConfiguration();
        setInitialConfiguration();
        setScreenElements();
    }


    private void setScreenElements() {
        TextView header = (TextView) findViewById(R.id.header);
        ImageView icon = (ImageView) findViewById(R.id.app_icon);
        TextView name = (TextView) findViewById(R.id.app_name);

        header.setBackgroundColor(appPreferences.getPrimaryColor());
        icon.setImageDrawable(appInfo.getIcon());
        name.setText(appInfo.getName());

        ImageView open = (ImageView) findViewById(R.id.open);
        ImageView extract = (ImageView) findViewById(R.id.extract);
        ImageView uninstall = (ImageView) findViewById(R.id.uninstall);
        ImageView share = (ImageView) findViewById(R.id.share);
        ImageView settings = (ImageView) findViewById(R.id.settings);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.open(context, appInfo);
            }
        });
        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.extract(context, appInfo);
            }
        });
        uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appInfo.getSystem()) {
                    ActionUtils.uninstall(context, appInfo);
                } else {
                    Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                    intent.setData(Uri.parse("package:" + appInfo.getAPK()));
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.share(context, appInfo);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.settings(context, appInfo);
            }
        });

        LinearLayout informations = (LinearLayout) findViewById(R.id.information);
        InformationView iPackage = new InformationView(context, getString(R.string.package_layout), appInfo.getAPK(), getResources().getColor(R.color.grey));
        InformationView iVersion = new InformationView(context, getString(R.string.version_layout), appInfo.getVersion(), getResources().getColor(R.color.grey_dark));
        InformationView iAppSize = new InformationView(context, getString(R.string.size_layout), getString(R.string.development_layout), getResources().getColor(R.color.grey));
        InformationView iCachesize = new InformationView(context, getString(R.string.cache_size_layout), getString(R.string.development_layout), getResources().getColor(R.color.grey_dark));
        InformationView iDataFolder = new InformationView(context, getString(R.string.data_layout), appInfo.getData(), getResources().getColor(R.color.grey));
        InformationView iSourceFolder = new InformationView(context, getString(R.string.source_layout), appInfo.getSource(), getResources().getColor(R.color.grey_dark));
        informations.addView(iPackage);
        informations.addView(iVersion);
        informations.addView(iAppSize);
        informations.addView(iCachesize);
        informations.addView(iDataFolder);
        informations.addView(iSourceFolder);

        PackageManager packageManager = getPackageManager();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
        try {
            InformationView iInstall = new InformationView(context, getString(R.string.install_layout), formatter.format(packageManager.getPackageInfo(appInfo.getAPK(), 0).firstInstallTime), getResources().getColor(R.color.grey));
            InformationView iUpdate = new InformationView(context, getString(R.string.update_layout), formatter.format(packageManager.getPackageInfo(appInfo.getAPK(), 0).lastUpdateTime), getResources().getColor(R.color.grey_dark));
            informations.addView(iInstall);
            informations.addView(iUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayout buttons = (LinearLayout) findViewById(R.id.buttons);

        Switch oneSwitch = new Switch(context);
        if (appInfo.getHidden()) {
            oneSwitch.setChecked(true);
        }
        oneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ActionUtils.hide(context, appInfo);
            }
        });
        Switch twoSwitch = new Switch(context);
        if (appInfo.getDisabled()) {
            twoSwitch.setChecked(true);
        }
        twoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ActionUtils.disable(context, appInfo);
            }
        });
        Switch redSwitch = new Switch(context);
        if (appInfo.getSystem()) {
            redSwitch.setChecked(true);
        }
        redSwitch.setClickable(false);
        redSwitch.setAlpha(0.5f);
        redSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });
        ButtonSwitchView hide = new ButtonSwitchView(context, getResources().getString(R.string.action_hide), null, oneSwitch);
        ButtonSwitchView disable = new ButtonSwitchView(context, getResources().getString(R.string.action_disable), null, twoSwitch);
        ButtonSwitchView system = new ButtonSwitchView(context, getResources().getString(R.string.action_system), null, redSwitch);
        buttons.addView(hide);
        buttons.addView(disable);
        buttons.addView(system);

        ButtonView removeCache = new ButtonView(context, getString(R.string.action_remove_cache), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                        , getResources().getString(R.string.dialog_cache_progress)
                        , getResources().getString(R.string.dialog_cache_progress_description));
                new RemoveCacheAsync(context, dialog, appInfo).execute();
            }
        });
        ButtonView clearData = new ButtonView(context, getString(R.string.action_clear_data), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                        , getResources().getString(R.string.dialog_clear_data_progress)
                        , getResources().getString(R.string.dialog_clear_data_progress_description));
                new ClearDataAsync(context, dialog, appInfo).execute();
            }
        });

        buttons.addView(removeCache);
        buttons.addView(clearData);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i("App", appInfo.getAPK() + "OK");
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("App", appInfo.getAPK() + "CANCEL");
            }
        }
    }


    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(InterfaceUtils.darker(appPreferences.getPrimaryColor(), 0.8));
            toolbar.setBackgroundColor(appPreferences.getPrimaryColor());
            if (appPreferences.getNavigationColor()) {
                getWindow().setNavigationBarColor(appPreferences.getPrimaryColor());
            }
        }
    }

    private void getInitialConfiguration() {
        String appName = getIntent().getStringExtra("app_name");
        String appApk = getIntent().getStringExtra("app_apk");
        String appVersion = getIntent().getStringExtra("app_version");
        String appSource = getIntent().getStringExtra("app_source");
        String appData = getIntent().getStringExtra("app_data");
        Boolean appIsSystem = getIntent().getExtras().getBoolean("app_isSystem");
        Boolean appIsFavorite = getIntent().getExtras().getBoolean("app_isFavorite");
        Boolean appIsHidden = getIntent().getExtras().getBoolean("app_isHidden");
        Boolean appIsDisabled = getIntent().getExtras().getBoolean("app_isDisabled");

        Bitmap bitmap = getIntent().getParcelableExtra("app_icon");
        Drawable appIcon = new BitmapDrawable(getResources(), bitmap);
        appInfo = new AppInfo(appName, appApk, appVersion, appSource, appData, appIsSystem, appIsFavorite, appIsHidden, appIsDisabled, appIcon);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        favorite = menu.findItem(R.id.action_favorite);
        InterfaceUtils.updateAppFavoriteIcon(context, favorite, appDbUtils.checkAppInfo(appInfo, 2));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                ActionUtils.favorite(context, appInfo);
                InterfaceUtils.updateAppFavoriteIcon(context, favorite, appDbUtils.checkAppInfo(appInfo, 2));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
