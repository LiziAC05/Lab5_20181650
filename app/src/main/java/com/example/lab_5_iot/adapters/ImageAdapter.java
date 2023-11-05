package com.example.lab_5_iot.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.lab_5_iot.R;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    public ImageAdapter (Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View contextView, ViewGroup viewGroup) {
        ImageView imageView;
        if(contextView == null){
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(350,350));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) contextView;
            imageView.setImageResource(R.drawable.question);
        }
        return imageView;
    }
}
