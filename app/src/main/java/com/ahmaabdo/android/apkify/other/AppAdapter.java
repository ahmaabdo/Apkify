package com.ahmaabdo.android.apkify.other;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmaabdo.android.apkify.AppActivity;
import com.ahmaabdo.android.apkify.MainActivity;
import com.ahmaabdo.android.apkify.R;
import com.ahmaabdo.android.apkify.utils.ActionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Ahmad on Jul 22, 2017.
 */

public class AppAdapter
        extends RecyclerView.Adapter<AppAdapter.AppViewHolder> implements Filterable {

    private List<AppInfo> appList;
    private List<AppInfo> appListSearch;
    private Context context;

    public AppAdapter(List<AppInfo> appList, Context context) {
        this.appList = appList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        AppInfo appInfo = appList.get(position);
        holder.vName.setText(appInfo.getName());
        holder.vApk.setText(appInfo.getAPK());
        holder.vIcon.setImageDrawable(appInfo.getIcon());
        setButtonEvents(holder, appInfo);
    }

    private void setButtonEvents(AppViewHolder appViewHolder, final AppInfo appInfo) {
        Button btnOne = appViewHolder.vOne;
        Button btnTwo = appViewHolder.vTwo;
        Button btnThree = appViewHolder.vThree;
        final ImageView appIcon = appViewHolder.vIcon;
        final CardView cardView = appViewHolder.vCard;

        btnOne.setVisibility(View.INVISIBLE);
        btnTwo.setVisibility(View.INVISIBLE);
        btnThree.setVisibility(View.INVISIBLE);

        if (App.getAppPreferences().getTheme().equals("1")) {
            btnOne.setBackgroundColor(context.getResources().getColor(R.color.white));
            btnTwo.setBackgroundColor(context.getResources().getColor(R.color.white));
            btnThree.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            btnOne.setBackgroundColor(context.getResources().getColor(R.color.grey_dark));
            btnTwo.setBackgroundColor(context.getResources().getColor(R.color.grey_dark));
            btnThree.setBackgroundColor(context.getResources().getColor(R.color.grey_dark));
        }

        Set<String> actions = App.getAppPreferences().getAction();
        int counter = 0;
        for (String action : actions) {
            //TODO: set Switch instead of If
            if (counter == 0) {
                btnOne.setVisibility(View.VISIBLE);
                setButton(Integer.parseInt(action), btnOne, appInfo);
            } else if (counter == 1) {
                btnTwo.setVisibility(View.VISIBLE);
                setButton(Integer.parseInt(action), btnTwo, appInfo);
            } else if (counter == 2) {
                btnThree.setVisibility(View.VISIBLE);
                setButton(Integer.parseInt(action), btnThree, appInfo);
            } else {
                break;
            }
            counter++;
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) context;
                Intent intent = new Intent(context, AppActivity.class);
                intent.putExtra("app_name", appInfo.getName());
                intent.putExtra("app_apk", appInfo.getAPK());
                intent.putExtra("app_version", appInfo.getVersion());
                intent.putExtra("app_source", appInfo.getSource());
                intent.putExtra("app_data", appInfo.getData());
                intent.putExtra("app_isSystem", appInfo.getSystem());
                intent.putExtra("app_isFavorite", appInfo.getFavorite());
                intent.putExtra("app_isHidden", appInfo.getHidden());
                intent.putExtra("app_isDisabled", appInfo.getDisabled());

                Bitmap bitmap = ((BitmapDrawable) appInfo.getIcon()).getBitmap();
                intent.putExtra("app_icon", bitmap);

                //the icon will smoothly transition to its new location if version is above lollipop
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String transitionName = context.getResources().getString(R.string.transition);
                    ActivityOptions transitionActivityOptions =
                            ActivityOptions.makeSceneTransitionAnimation(
                                    activity, appIcon, transitionName);
                    context.startActivity(intent, transitionActivityOptions.toBundle());
                } else {
                    context.startActivity(intent);
                }
            }
        });

    }

    public void setButton(int action, Button button, final AppInfo appInfo) {
        switch (action) {
            case 0:
                button.setText(context.getString(R.string.action_open));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActionUtils.open(context, appInfo);
                    }
                });
                break;
            case 1:
                button.setText(context.getString(R.string.action_extract));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActionUtils.extract(context, appInfo);
                    }
                });
                break;
            case 2:
                button.setText(context.getString(R.string.action_share));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActionUtils.share(context, appInfo);
                    }
                });
                break;
        }
    }

    //Filter the search and notify change data if search result = name of an application
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<AppInfo> results = new ArrayList<>();
                if (appListSearch == null)
                    appListSearch = appList;

                if (constraint != null) {
                    if (appListSearch != null && appListSearch.size() > 0) {
                        for (final AppInfo appInfo : appListSearch) {
                            if (appInfo.getName().toLowerCase().contains(constraint.toString())) {
                                results.add(appInfo);
                            }
                        }
                    }
                    oReturn.values = results;
                    oReturn.count = results.size();
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    MainActivity.setResultsMessage(false);
                } else {
                    MainActivity.setResultsMessage(true);
                }
                appList = (ArrayList<AppInfo>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View appAdapterView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_app, parent, false);
        return new AppViewHolder(appAdapterView);
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        private TextView vName;
        private TextView vApk;
        private ImageView vIcon;
        private CardView vCard;

        private Button vOne;
        private Button vTwo;
        private Button vThree;

        public AppViewHolder(View view) {
            super(view);
            vName = (TextView) view.findViewById(R.id.txtName);
            vApk = (TextView) view.findViewById(R.id.txtApk);
            vIcon = (ImageView) view.findViewById(R.id.imgIcon);
            vCard = (CardView) view.findViewById(R.id.app_card);

            vOne = (Button) view.findViewById(R.id.btnOne);
            vTwo = (Button) view.findViewById(R.id.btnTwo);
            vThree = (Button) view.findViewById(R.id.btnThree);
        }
    }
}
