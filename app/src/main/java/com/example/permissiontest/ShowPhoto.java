package com.example.permissiontest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import static java.lang.Math.min;

/**
 * Created by Ralph_Chao on 2016/12/27.
 */

public class ShowPhoto extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_photo);
        ImageView imageView = (ImageView)findViewById(R.id.photo_image);

        Intent intent = getIntent();

        String path = intent.getStringExtra("PHOTOPATH");

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);

        /*int width = bmOptions.outWidth;
        int height = bmOptions.outHeight;
        int layout_width = imageView.getWidth();
        int layout_height = imageView.getHeight();
        int scale = min(width/layout_width, height/layout_height);*/

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = 2;
        Bitmap bm = BitmapFactory.decodeFile(path, bmOptions);
        imageView.setImageBitmap(bm);

    }

}
