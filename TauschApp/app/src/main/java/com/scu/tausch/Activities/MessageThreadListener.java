package com.scu.tausch.Activities;

import java.util.List;

/**
 * Created by Praneet on 3/30/16.
 */
public interface MessageThreadListener {

    void callbackForAllMessagesThreads(List<String> uniqueIds, List<String> peopleNames);

}
