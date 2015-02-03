package com.yahoo.gridimagesearch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by sraovasu on 2/2/15.
 */
public class FullScreenImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        String imageUrl = getIntent().getStringExtra("imageUrl");
        ImageView ivFullscreenImage = (ImageView) findViewById(R.id.ivFullscreenImage);

        Picasso.with(this).load(imageUrl).fit().into(ivFullscreenImage);

        final FullScreenImageActivity instance = this;

        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.finish();
            }
        });

        ImageButton btnShare = (ImageButton) findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.shareImage();
            }
        });
    }

    private void shareImage() {
        ImageView ivFullscreenImage = (ImageView) findViewById(R.id.ivFullscreenImage);
        Drawable itemDrawable = ivFullscreenImage.getDrawable();
        Bitmap bitmapImage = ((BitmapDrawable)itemDrawable).getBitmap();

        String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                bitmapImage, "Image Description", null);

        Uri uri = Uri.parse(path);

        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.setType("image/png");
        startActivity(Intent.createChooser(emailIntent, "Share image"));
    }
}
