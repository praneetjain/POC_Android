package com.scu.tausch.DTO;

import com.parse.ParseObject;
import com.parse.ParseClassName;

/**
 * Created by Praneet on 1/31/16.
 */
@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_ID_KEY = "userId";
    public static final String OTHER_PERSON_NAME = "other_person";
    public static final String SENDER_PERSON_NAME = "sender_person";
    public static final String BODY_KEY = "body";
    public static final String RECEIVER_ID_KEY = "receiverId";

    public String getOtherPersonName(){
        return getString(OTHER_PERSON_NAME);
    }

    public String getSenderPersonName() {
        return getString(SENDER_PERSON_NAME);
    }

    public void setOtherPersonName(String otherPersonName){
        put(OTHER_PERSON_NAME,otherPersonName);
    }

    public void setSenderPersonName(String senderPersonName) {
        put(SENDER_PERSON_NAME, senderPersonName);
    }

    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public void setReceiverId(String receiverId){
        put(RECEIVER_ID_KEY,receiverId);
    }

    public String getReceiverId(){
        return getString(RECEIVER_ID_KEY);
    }

}
