package com.scu.tausch.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.DialogInterface;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.scu.tausch.DB.DBAccessor;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

/**
 * Created by Praneet on 1/17/16.
 */
public class HomePage extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener,SearchListener{


    private static String TAG = HomePage.class.getSimpleName();
    private String Tag_Name;

    private Toolbar mToolbar,toolbarBottom;
    private boolean isSearcing;
    private FragmentDrawer drawerFragment;
    private Fragment fragment = null;


    private final int textTitleWidth = 240;
    private final int marginToReduceFromWidth = 40;

    //Names and images for categories.
    private CharSequence[] items = {Constants.HOMEPAGE_HOME, Constants.HOMEPAGE_MY_OFFERS, Constants.HOMEPAGE_MY_MESSAGES,Constants.HOMEPAGE_SETTINGS,Constants.HOMEPAGE_HELP,Constants.HOMEPAGE_ABOUT,Constants.HOMEPAGE_SIGNOUT};
    private final int HOME = 0;
    private final int MY_OFFERS = 1;
    private final int MY_MESSAGES = 2;
    private final int SETTINGS = 3;
    private final int HELP = 4;
    private final int ABOUT = 5;
    private final int SIGN_OUT = 6;
    private ProgressDialog progress;
    static ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

       Constants.homePage = this;
        DBAccessor.searchCode = Constants.SEARCH_CODE_HOME_PAGE;

        ImageAddFragment.context=this;
        MyOfferFragment.context=this;
        EditOfferFragment.context=this;
        EditImageFragment.context=this;
        ChatFragment.context = this;
        MyMessagesFragment.context = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        profilePicture = (ImageView) findViewById(R.id.profile_picture);

//        try {
//            ParseFile bum = (ParseFile) ParseUser.getCurrentUser().get("picture");
//            if(bum!=null) {
//                byte[] file = bum.getData();
//                Bitmap userImage = BitmapFactory.decodeByteArray(file, 0, file.length);
//                profilePicture.setImageBitmap(userImage);
//            }
//        }catch (ParseException pe){
//
//        }


        //set profile image for user.
        ParseFile imageFile = (ParseFile) ParseUser.getCurrentUser().get("picture");
        if (imageFile != null) {
            imageFile.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        // data has the bytes for the image
                        Bitmap userImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                        profilePicture.setImageBitmap(userImage);
                    } else {
                        // something went wrong
                    }
                }
            });
        }
        AddOfferFragment.context=this;

        // display the first navigation drawer view on app launch
        displayView(0);

        if (Constants.PUSH_RECEIVED){
            pushReceived();
            Constants.lastReceiverName=null;
            Constants.lastReceiverObjectId=null;
            Constants.lastReceiverEmail=null;
            Constants.PUSH_RECEIVED = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launch_screen, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (isSearcing == false) {
                    isSearcing = true;
                    showProgressForSearch();
                    DBAccessor.getInstance().getSearchResults(query, HomePage.this);
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });



        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;

//  int buttonFilter_X = 20;
//      //  int buttonSort_X = (width - (buttonSort.getWidth()+40+20));
//
//        buttonFilter.setX(buttonFilter_X);
//       // buttonSort.setX(buttonSort_X);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {


            return true;
        }
        if (id == R.id.action_add) {

            Fragment fragment = null;
            String title;

            fragment = new AddOfferFragment();
//            title = getString(R.string.title_filter);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment,Constants.TAG_Add_Offer_Fragment);
            fragmentTransaction.commit();

            // set the toolbar title
//            getSupportActionBar().setTitle(title);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        view.setClickable(true);
        displayView(position);
    }

    private void displayView(int position) {
        String title = getString(R.string.app_name);
        switch (position) {
            case HOME:
                fragment = new HomeFragment();
                DBAccessor.searchCode = Constants.SEARCH_CODE_HOME_PAGE;
                Tag_Name = Constants.TAG_Home_Fragment;
                break;
            case MY_OFFERS:
                fragment = new MyOfferFragment();
                Tag_Name=Constants.TAG_My_Offer_Fragment;
                break;
            case MY_MESSAGES:
                fragment = new MyMessagesFragment();
                Tag_Name=Constants.Tag_My_Messages_Fragment;
                Constants.WAS_LAST_SCREEN_ITEM_DESCRIPTION = false;
                break;
            case SETTINGS:
                fragment = new SettingsFragment();
                Tag_Name=null;
                break;
            case HELP:
                fragment = new HelpFragment();
                Tag_Name=null;
                break;
            case ABOUT:
                fragment = new AboutFragment();
                Tag_Name=null;
                break;
            case SIGN_OUT:
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Do you really want to Sign out?");

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();


                        ParseUser.getCurrentUser().logOut();
                        Intent intent = new Intent(HomePage.this,Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel",null);

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            default:
                break;
        }

        if (fragment != null) {

            //Passing context of this class to fragment.
            HomeFragment.context = this;

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment,Tag_Name);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void searchResults(List<ParseObject> objects, String searchStr) {

        progress.dismiss();
        isSearcing = false;

        OffersList fragment = new OffersList();
        fragment.searchList(objects, searchStr);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment,Constants.TAG_Offer_List);
        fragmentTransaction.commit();

    }

    public void showProgressForSearch(){

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            //Move to previous activity


            if (Constants.CURRENT_SCREEN == Constants.SCREEN_MY_MESSAGES_CHAT) {

                if (Constants.WAS_LAST_SCREEN_ITEM_DESCRIPTION == false) {
                    Fragment fragment = null;
                    fragment = new MyMessagesFragment();

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment, Constants.Tag_My_Messages_Fragment);
                    fragmentTransaction.commit();
                } else {

                    Fragment fragment = null;
                    fragment = new HomeFragment();

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                    fragmentTransaction.commit();
                }


            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_MY_MESSAGES_LIST) {
                Fragment fragment = null;
                fragment = new HomeFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                fragmentTransaction.commit();

            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_OFFER_POST_1) {

                Fragment fragment = null;
                fragment = new HomeFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                fragmentTransaction.commit();

            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_MY_OFFERS_LIST) {


                Fragment fragment = null;
                fragment = new HomeFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                fragmentTransaction.commit();
            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_OFFERS_LIST) {
                Fragment fragment = null;
                fragment = new HomeFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                fragmentTransaction.commit();
            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_ABOUT) {


                Fragment fragment = null;
                fragment = new HomeFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                fragmentTransaction.commit();
            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_HELP) {


                Fragment fragment = null;
                fragment = new HomeFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                fragmentTransaction.commit();
            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_SETTINGS) {


                Fragment fragment = null;
                fragment = new HomeFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                fragmentTransaction.commit();
            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_FILTER_PAGE) {

                Fragment fragment = null;
                fragment = new HomeFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                fragmentTransaction.commit();
            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_EDIT_OFFERS_1) {

                Fragment fragment = null;
                fragment = new HomeFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                fragmentTransaction.commit();
            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_EDIT_OFFERS_2) {

                Fragment fragment = null;
                fragment = new HomeFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                fragmentTransaction.commit();
            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_OFFER_POST_2) {

                Fragment fragment = null;
                fragment = new HomeFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                fragmentTransaction.commit();
            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_OFFER_DETAILS_CHAT_WINDOW) {


                Fragment fragment = null;
                fragment = new HomeFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Home_Fragment);
                fragmentTransaction.commit();
            } else if (Constants.CURRENT_SCREEN == Constants.SCREEN_HOME_PAGE) {
                finish();
            }

            return true;
        }

        // If it wasn't the Back key, bubble up to the default
        // system behavior
        return super.onKeyDown(keyCode, event);
    }

    public void pushReceived(){

        Constants.WAS_LAST_SCREEN_ITEM_DESCRIPTION = true;

        ChatFragment fragment = null;
        fragment = new ChatFragment();

        fragment.setArgumentsForMessageSending(Constants.lastReceiverEmail,Constants.lastReceiverObjectId, Constants.lastReceiverName);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment, Constants.TAG_Chat_Fragment);
        fragmentTransaction.commit();

    }

}
