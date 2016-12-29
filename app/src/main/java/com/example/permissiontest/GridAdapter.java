package com.example.permissiontest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Ralph_Chao on 2016/12/22.
 */

public class GridAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<String> imagePath;

    public GridAdapter (Context context, ArrayList<String> path) {
        this.inflater = LayoutInflater.from(context);
        this.imagePath = path;
    }

    public class ViewHolder {
        SimpleDraweeView image;
        public ViewHolder(SimpleDraweeView imageView) {
            this.image = imageView;
        }
    }

    @Override
    public int getCount() {
        return this.imagePath.size();
    }

    @Override
    public String getItem(int position) {
        return this.imagePath.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.imagePath.indexOf(imagePath.get(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.grid_item, null);
            holder = new ViewHolder((SimpleDraweeView) convertView.findViewById(R.id.grid_image));
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();

        final String path = getItem(position);

        Uri uri = Uri.parse("file://"+path);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                                            .setResizeOptions(new ResizeOptions(120, 120))
                                            .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                            .setOldController(holder.image.getController())
                                            .setImageRequest(request)
                                            .setAutoPlayAnimations(true)
                                            .build();
        holder.image.setController(controller);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShowPhoto.class);
                intent.putExtra("PHOTOPATH", path);
                v.getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
