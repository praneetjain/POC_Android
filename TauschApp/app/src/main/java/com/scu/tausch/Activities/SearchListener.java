package com.scu.tausch.Activities;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Praneet on 2/21/16.
 */
public interface SearchListener {

    void searchResults(List<ParseObject> objects, String itemsToSearch);
}
