package com.scu.tausch.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.scu.tausch.Adapters.CustomListAdapter;
import com.scu.tausch.DB.DBAccessor;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Praneet on 1/29/16.
 */
public class MyOfferFragment extends Fragment implements DBListener,RefreshInterface{

    private List<ParseObject> itemObjects;
    public static HomePage context;
    private String[] arrayItemNames;
    private Bitmap[] arrayItemImages;
    private Double[] arrayItemCosts;
    private ListView listViewItems;
    private ProgressDialog progress;
    private boolean isFilterActive=false;
    private boolean isSearchActive=false;
    private TextView emptyListTextView;
    private List<ParseObject> retainItemObjects;
    private CustomListAdapter customListAdapter;
    private ProgressDialog progressDialog;
    public MyOfferFragment() {
        // Required empty public constructor
    }

    public CustomListAdapter getAdapter() {
        return customListAdapter;
    }
    public void setAdapter(CustomListAdapter customListAdapter) {
        this.customListAdapter = customListAdapter;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBAccessor.searchCode = Constants.SEARCH_CODE_HOME_PAGE;

        progress = new ProgressDialog(getActivity());
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myoffer, container, false);

        emptyListTextView = (TextView) rootView.findViewById(android.R.id.empty);


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


        DBAccessor.getInstance().getItemsPostedByUser(context);

        // Inflate the layout for this fragment
        return rootView;
    }

    private void setArraysForNamesImagesCost(List<ParseObject> arrayOfItemObjects){

        List<String> arrayTitles = new ArrayList<>();
        List<Bitmap> arrayImages = new ArrayList<>();
        List<Double> arrayPrice = new ArrayList<>();

        for(ParseObject itemObject:arrayOfItemObjects){

            String itemTitle = (String)itemObject.get(Constants.DB_Offer_Title);
            arrayTitles.add(itemTitle);
            double itemPrice = ((Number) itemObject.get(Constants.DB_Price)).doubleValue();
            arrayPrice.add(itemPrice);

            try {
                ParseFile bum = (ParseFile) itemObject.get(Constants.DB_Image_ONE);
                byte[] file = bum.getData();
                Bitmap image = BitmapFactory.decodeByteArray(file, 0, file.length);
                arrayImages.add(image);

            }
            catch (ParseException e){

            }

        }

        arrayItemNames = arrayTitles.toArray(new String[itemObjects.size()]);
        arrayItemCosts = arrayPrice.toArray(new Double[itemObjects.size()]);
        arrayItemImages = arrayImages.toArray(new Bitmap[itemObjects.size()]);

    }

    public void fetchedDataFromServer(){

        final CustomListAdapter customListAdapter = new CustomListAdapter(getActivity(),arrayItemNames,arrayItemCosts,arrayItemImages);
        setAdapter(customListAdapter);
        listViewItems.setAdapter(customListAdapter);

        if (itemObjects.size() == 0) {
            emptyListTextView.setText("No items found.");
            listViewItems.setEmptyView(emptyListTextView);
        }


        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EditOfferFragment nextFrag = new EditOfferFragment();

                nextFrag.setArgumentsForUpdate(itemObjects.get(position));

                MyOfferFragment.this.getFragmentManager().beginTransaction()
                        .replace(R.id.myOfferItemFragment, nextFrag, Constants.TAG_Edit_Offer_Fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // remove items
        // Create the listener for long item clicks
        listViewItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long rowid) {

                // Store selected item in global variable
                final String selectedItem = parent.getItemAtPosition(position).toString();
                final String objectToBeDeleted = itemObjects.get(position).getObjectId();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to remove " + selectedItem + "?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(getAdapter() != null) {

                            progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setMessage("Deleting...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();

                            DBAccessor.getInstance().deleteOffer(objectToBeDeleted,context);
                          // TODO refresh the list or change arrays to lists and uncomment the next two lines
                            //getAdapter().remove(selectedItem);

                            getAdapter().notifyDataSetChanged();
                            DBAccessor.getInstance().getItemsPostedByUser(context);
                            getAdapter().notifyDataSetChanged();
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
    public void callback(List<ParseObject> objects) {

        itemObjects=objects;

        if (itemObjects == null) {
            progress.dismiss();
            return;
        }

        setArraysForNamesImagesCost(objects);

        fetchedDataFromServer();

        progress.dismiss();

    }

    @Override
    public void refreshAfterStatusChangeForDelete() {

        //adding time lag so that item gets refreshed on device after data has been deleted and status
        //has changed from true to false.
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over

                DBAccessor.getInstance().getItemsPostedByUser(context);
                customListAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }
        }, Constants.DELETE_ITEM_TIME_TO_REFRESH);




    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Constants.CURRENT_SCREEN = Constants.SCREEN_MY_OFFERS_LIST;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
