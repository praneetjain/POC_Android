package com.scu.tausch.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.scu.tausch.R;

/**
 * Created by Praneet on 3/30/16.
 */
public class MyMessagesAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] itemName;

    public MyMessagesAdapter(Activity context, String[] itemName) {
        super(context, R.layout.category_mymessages_list_item_row, itemName);

        this.context=context;
        this.itemName=itemName;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=((Activity)context).getLayoutInflater();
        View rowView=inflater.inflate(R.layout.category_mymessages_list_item_row, null,true);

        TextView textTitle = (TextView) rowView.findViewById(R.id.item_name);

        textTitle.setText(itemName[position]);
        return rowView;

    }
}
