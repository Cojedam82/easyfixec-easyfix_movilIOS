package com.easyfixapp.easyfix.widget;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.github.florent37.materialleanback.MaterialLeanBack;

/**
 * Created by julio on 30/10/17.
 */

public class CustomViewHolder extends MaterialLeanBack.ViewHolder {

    public TextView textView;
    public ImageView imageView;

    public CustomViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.txt_title);
        imageView = (ImageView) itemView.findViewById(R.id.img_detail);
    }
}
