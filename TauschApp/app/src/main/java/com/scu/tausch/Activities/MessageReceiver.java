package com.scu.tausch.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParsePushBroadcastReceiver;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Praneet on 3/20/16.
 */
public class MessageReceiver extends ParsePushBroadcastReceiver {

    String message;
    static HomePage context;
    String receiverEmail,receiverObjectId,receiverName, senderObjectId,senderEmail;

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        Bundle extras = intent.getExtras();
        try {
            String jsonData = extras.getString("com.parse.Data");
            JSONObject jsonObject;
            jsonObject = new JSONObject(jsonData);
             message = jsonObject.getString("alert");
            receiverEmail = jsonObject.getString("receiver_email");
            senderEmail = jsonObject.getString("sender_email");
            receiverName = jsonObject.getString("receiver_name");
            receiverObjectId = jsonObject.getString("receiver_object_id");
            senderObjectId = jsonObject.getString("sender_object_id");

            Constants.lastReceiverName = receiverName;
            Constants.lastReceiverObjectId = senderObjectId;
            Constants.lastSenderEmail = senderEmail;
            Constants.lastReceiverEmail = receiverEmail;

            SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("last_sender_email", senderEmail);
            editor.putString("last_receiver_email", receiverEmail);
            editor.putBoolean("push_received", true);
            editor.commit();


            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels;


            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView textView = (TextView)inflater.inflate(R.layout.textview_bubble, null);
            textView.setWidth((int)dpWidth);
            textView.setBackgroundColor(Color.parseColor("#4edacf"));
            textView.setText(message);

            LinearLayout layout = ChatFragment.layout;
            if (layout != null) {
                layout.addView(textView);
            }

            // Code to add the time when message is received. I have commented the code - defect 34 from excel sheet
            TextView textViewTS = (TextView) inflater.inflate(R.layout.textview_bubble_timestamp, null);
            Date d  = new Date();
            textViewTS.setText(d.toString());
            textViewTS.setTextColor(Color.WHITE);
            textViewTS.setWidth((int) dpWidth);
            textViewTS.setBackgroundColor(Color.parseColor("#4edacf"));
            if (layout != null) {
                layout.addView(textViewTS);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);

//        Constants.homePage.pushReceived();
        Constants.PUSH_RECEIVED = true;
    }

    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {

        Constants.PUSH_RECEIVED = true;

        return super.getActivity(context, intent);
    }
}
