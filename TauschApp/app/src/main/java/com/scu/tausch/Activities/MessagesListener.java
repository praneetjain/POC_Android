package com.scu.tausch.Activities;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Praneet on 3/30/16.
 */
public interface MessagesListener {

    void callbackForAllMessages(List<ParseObject> messagesAll, String receiverId);


}
