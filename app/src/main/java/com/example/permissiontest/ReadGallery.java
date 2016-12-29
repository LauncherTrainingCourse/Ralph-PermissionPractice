package com.example.permissiontest;

import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;


/**
 * Created by Ralph_Chao on 2016/12/22.
 */

public class ReadGallery extends AppCompatActivity {

    ArrayList<String> imagePath = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_gallery);

        File externalStorage = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PermissionTest");

        if(!externalStorage.exists() || externalStorage.listFiles().length < 1) {
            Toast.makeText(getApplicationContext(),"Album Empty", Toast.LENGTH_SHORT).show();
        }
        else {
            File [] photoList = externalStorage.listFiles();
            Arrays.sort(photoList);
            for (File f : photoList) {
                if (f.isFile()) {
                    String imageName = f.getName();
                    imagePath.add(externalStorage.getAbsolutePath() + "/" + imageName);
                }
            }
            Log.d("num", Integer.toString(externalStorage.listFiles().length));

            GridAdapter gridAdapter = new GridAdapter(this, imagePath);
            GridView gridView = (GridView) findViewById(R.id.gridview);
            gridView.setAdapter(gridAdapter);
        }
    }
}
