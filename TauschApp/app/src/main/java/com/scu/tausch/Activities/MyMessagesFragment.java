package com.scu.tausch.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.scu.tausch.Adapters.MyMessagesAdapter;
import com.scu.tausch.DB.DBAccessor;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Praneet on 1/29/16.
 */
public class MyMessagesFragment extends Fragment implements MessageThreadListener,RefreshInterface{

    private MyMessagesAdapter myMessagesAdapter;
    private ListView listViewItems;
    private TextView emptyListTextView;
    private List<String> itemObjects;
    private  String[] arrayUniqueNames;
    private  String[] arrayUniqueIds;
    private  String[] arrayItemCosts;
    private ProgressDialog progress;

    private String receiverEmail;
    private String receiverObjectId;
    private String receiverName;
    private ProgressDialog progressDialog;

    public static HomePage context;

    public MyMessagesFragment() {
        // Required empty public constructor
    }

    public MyMessagesAdapter getAdapter() {
        return myMessagesAdapter;
    }
    public void setAdapter(MyMessagesAdapter customListAdapter) {
        this.myMessagesAdapter = customListAdapter;
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Constants.PUSH_RECEIVED = false;

        DBAccessor.searchCode = Constants.SEARCH_CODE_HOME_PAGE;

        progress = new ProgressDialog(getActivity());
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
       DBAccessor.getInstance().getAllMessageThreadValuesfinal(context);











        //DO NOT DELETE FOLLOWING CODE
        //ChatFragment nextFrag= new ChatFragment();

        // Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
        // Commit the transaction

//        this.getFragmentManager().beginTransaction()
//                .replace(R.id.myMessagesWindow, nextFrag)
//                .addToBackStack(null)
//                .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mymessages, container, false);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                return false;
            }
        });

        listViewItems=(ListView)rootView.findViewById(R.id.list_items_in_category);
        emptyListTextView=(TextView)rootView.findViewById(android.R.id.empty);


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void callbackForAllMessagesThreads(List<String> uniqueIds, List<String> peopleNames) {

        progress.dismiss();
        itemObjects=peopleNames;

        arrayUniqueNames = peopleNames.toArray(new String[itemObjects.size()]);
        arrayUniqueIds = uniqueIds.toArray(new String[itemObjects.size()]);

        fetchedDataFromServer();
    }


    public void fetchedDataFromServer(){

        final MyMessagesAdapter myMessagesAdapter = new MyMessagesAdapter(getActivity(),arrayUniqueNames);
        setAdapter(myMessagesAdapter);
        listViewItems.setAdapter(myMessagesAdapter);

        if (itemObjects.size() == 0) {
            emptyListTextView.setText("No items found.");
            listViewItems.setEmptyView(emptyListTextView);
        }


        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                Constants.PUSH_RECEIVED = sharedpreferences.getBoolean("push_received",false);



                final ChatFragment nextFrag = new ChatFragment();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                query.whereEqualTo("objectId", arrayUniqueIds[position]);
                // Retrieve the object by id

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        receiverEmail = (String) (objects.get(0).get("email"));
                        receiverObjectId = arrayUniqueIds[position];
                        receiverName = (String) (objects.get(0).get("firstname"));

                        nextFrag.setArgumentsForMessageSending(receiverEmail, receiverObjectId, receiverName);

                        MyMessagesFragment.this.getFragmentManager().beginTransaction()
                                .replace(R.id.myMessagesWindow, nextFrag, Constants.TAG_Chat_Fragment)
                                .addToBackStack(null)
                                .commit();

                    }
                });



            }
        });

        // remove items
        // Create the listener for long item clicks
        listViewItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long rowid) {

                SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                Constants.PUSH_RECEIVED = sharedpreferences.getBoolean("push_received",false);

                // Store selected item in global variable
                final String selectedItem = parent.getItemAtPosition(position).toString();

                final String objectToBeDeleted = arrayUniqueIds[position];
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to remove " + selectedItem + "?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getAdapter() != null) {

                            progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setMessage("Deleting...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();

                            DBAccessor.getInstance().deleteMessage(objectToBeDeleted, context);
                            // TODO refresh the list or change arrays to lists and uncomment the next two lines
                            //getAdapter().remove(selectedItem);
                            //getAdapter().notifyDataSetChanged();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                // Create and show the dialog
                builder.show();
                // Signal OK to avoid further processing of the long click
                return true;
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Constants.CURRENT_SCREEN = Constants.SCREEN_MY_MESSAGES_LIST;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void refreshAfterStatusChangeForDelete() {

        //adding time lag so that item gets refreshed on device after data has been deleted and status
        //has changed from true to false.
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over

                DBAccessor.getInstance().getAllMessageThreadValuesfinal(context);
                myMessagesAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }
        }, Constants.DELETE_ITEM_TIME_TO_REFRESH);




    }


}
