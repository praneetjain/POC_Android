package com.scu.tausch.Activities;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Praneet on 2/13/16.
 */
public interface DBListener {

    void callback(List<ParseObject> objects);
}
