package com.scu.tausch.Misc;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.scu.tausch.DTO.Message;

/**
 * Created by Praneet on 1/16/16.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models here
        ParseObject.registerSubclass(Message.class);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        if (ParseUser.getCurrentUser() != null) {
            installation.put("username", ParseUser.getCurrentUser().getEmail());
        }
        installation.saveInBackground();
        ParsePush.subscribeInBackground("Tausch");



//        Parse.enableLocalDatastore(this);
//        Parse.initialize(this);
//        ParseInstallation.getCurrentInstallation().saveInBackground();
//        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
//        installation.put("username", ParseUser.getCurrentUser().getEmail());
//        installation.saveInBackground();
//        ParsePush.subscribeInBackground("Tausch");


        //We need current user many times, so need to make sure its not null.
        if (ParseUser.getCurrentUser()==null) {
            //ParseUser.enableAutomaticUser();
         //   ParseUser.getCurrentUser().saveInBackground();
        }
    }

}
