package com.yahoo.gridimagesearch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import com.etsy.android.grid.util.DynamicHeightTextView;
import com.squareup.picasso.Picasso;
import com.yahoo.gridimagesearch.SearchImage;
import android.view.ViewGroup.LayoutParams;

/**
 * Created by sraovasu on 2/1/15.
 */

public class ImageSearchAdapter extends ArrayAdapter<SearchImage> {

    public ImageSearchAdapter(Context context, ArrayList<SearchImage> images) {
        super(context, R.layout.item_image, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchImage image = getItem(position);

        // Note: I could not figure out how to resize the views while being
        // reused. So, I am inflating it for each cell which is definitely something
        // I would rework to ensure better performance
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image, parent, false);
        ImageView ivSearchImageView = (ImageView) convertView.findViewById(R.id.ivSearchImageView);;

        LayoutParams params = ivSearchImageView.getLayoutParams();
        int newHeight = params.width * image.height/image.width;
        params.height = newHeight;
        ivSearchImageView.setLayoutParams(params);

        Picasso.with(getContext())
                .load(image.url)
                .fit()
                .placeholder(R.drawable.placeholder)
                .into(ivSearchImageView);

        return convertView;
    }
}
