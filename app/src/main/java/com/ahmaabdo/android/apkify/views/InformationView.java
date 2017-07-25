package com.ahmaabdo.android.apkify.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahmaabdo.android.apkify.R;

/**
 * Created by Ahmad on Jul 22, 2017.
 */

public class InformationView extends RelativeLayout {

    public InformationView(Context context,
                           String title, String summary, int color) {
        super(context);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.view_information, null);
        addView(view);

        setBackgroundColor(color);

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView summaryView = (TextView) view.findViewById(R.id.summary);

        titleView.setText(title);
        summaryView.setText(summary);
    }
}