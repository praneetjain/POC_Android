package com.scu.tausch.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scu.tausch.R;

/**
 * Created by Praneet on 2/14/16.
 */
public class CustomListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] itemName;
    private final Double[] itemPrice;
    private final Bitmap[] imageId;

    public CustomListAdapter(Activity context, String[] itemName, Double[] itemPrice, Bitmap[] imageId) {
        super(context, R.layout.category_items_list_row, itemName);

        this.context=context;
        this.itemName=itemName;
        this.imageId=imageId;
        this.itemPrice=itemPrice;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=((Activity)context).getLayoutInflater();
        View rowView=inflater.inflate(R.layout.category_items_list_row, null,true);

        TextView textTitle = (TextView) rowView.findViewById(R.id.item_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.item_image);
        TextView textPrice = (TextView) rowView.findViewById(R.id.item_cost);

        textTitle.setText(itemName[position]);
        imageView.setImageBitmap(imageId[position]);
        textPrice.setText("$"+itemPrice[position]);
        return rowView;

    }
}
