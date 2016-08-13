package com.scu.tausch.Activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.ParseFile;
import com.scu.tausch.DB.DBAccessor;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.R;

import java.util.ArrayList;

import android.widget.AdapterView.OnItemClickListener;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Praneet on 1/29/16.
 */
public class HomeFragment extends Fragment {

    public static HomePage context;

    private String[] arrayCategoryNames = new String[]{"AUTOMOBILES", "BOOKS", "LAPTOPS","FURNITURE","RENTALS"};
    private int[] arrayCategoryImages = new int[]{R.mipmap.car_cat_latest, R.mipmap.img_books_new1, R.mipmap.img_laptop_new1, R.mipmap.img_furniture_new1, R.mipmap.img_rental_new1};
    private ListView listViewCategories;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBAccessor.searchCode = Constants.SEARCH_CODE_HOME_PAGE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_menu, container, false);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                return false;
            }
        });

        List<HashMap<String, String>> categoriesNamesImages = new ArrayList<>();

        for (int i = 0; i < arrayCategoryNames.length; i++) {
            HashMap<String, String> hmPairs = new HashMap<>();
            hmPairs.put("category_name", arrayCategoryNames[i]);
            hmPairs.put("category_image", Integer.toString(arrayCategoryImages[i]));
            categoriesNamesImages.add(hmPairs);
        }

        //Keys used in HashMap.
        String[] from = {"category_image", "category_name"};

        //Ids used in HashMap.
        int[] to = {R.id.category_image, R.id.category_name};


        // Instantiating an adapter to store each items
        // R.layout.home_category_list defines the layout of each item
        SimpleAdapter categoryListAdapter = new SimpleAdapter(getActivity().getBaseContext(), categoriesNamesImages, R.layout.home_category_list, from, to);

        listViewCategories = (ListView) rootView.findViewById(R.id.list_categories);
        listViewCategories.setAdapter(categoryListAdapter);


        listViewCategories.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {


                if (position == Constants.CATEGORY_AUTOMOBILES) {

                    Constants.CURRENT_SELECTED_CATEGORY = Constants.CATEGORY_AUTOMOBILES;
                    DBAccessor.getInstance().getItemsForCategory(Constants.CATEGORY_AUTOMOBILES_OBJECT_ID,context);

                     DBAccessor.searchCode = Constants.SEARCH_CODE_AUTOMOBILES;
                    OffersList.currentCategoryId = Constants.CATEGORY_AUTOMOBILES_OBJECT_ID;


                } else if (position == Constants.CATEGORY_BOOKS) {

                    Constants.CURRENT_SELECTED_CATEGORY = Constants.CATEGORY_BOOKS;
                    DBAccessor.getInstance().getItemsForCategory(Constants.CATEGORY_BOOKS_OBJECT_ID,context);

                    DBAccessor.searchCode = Constants.SEARCH_CODE_BOOKS;
                    OffersList.currentCategoryId = Constants.CATEGORY_BOOKS_OBJECT_ID;


                } else if (position == Constants.CATEGORY_LAPTOPS) {

                    Constants.CURRENT_SELECTED_CATEGORY = Constants.CATEGORY_LAPTOPS;

                    DBAccessor.getInstance().getItemsForCategory(Constants.CATEGORY_LAPTOPS_OBJECT_ID,context);

                    DBAccessor.searchCode = Constants.SEARCH_CODE_LAPTOPS;
                    OffersList.currentCategoryId = Constants.CATEGORY_LAPTOPS_OBJECT_ID;



                } else if (position == Constants.CATEGORY_FURNITURE) {

                    Constants.CURRENT_SELECTED_CATEGORY = Constants.CATEGORY_FURNITURE;

                    DBAccessor.getInstance().getItemsForCategory(Constants.CATEGORY_FURNITURE_OBJECT_ID,context);

                    DBAccessor.searchCode = Constants.SEARCH_CODE_FURNITURE;
                    OffersList.currentCategoryId = Constants.CATEGORY_FURNITURE_OBJECT_ID;


                } else if (position == Constants.CATEGORY_RENTALS) {

                    Constants.CURRENT_SELECTED_CATEGORY = Constants.CATEGORY_RENTALS;

                    DBAccessor.getInstance().getItemsForCategory(Constants.CATEGORY_RENTALS_OBJECT_ID,context);

                    DBAccessor.searchCode = Constants.SEARCH_CODE_RENTALS;
                    OffersList.currentCategoryId = Constants.CATEGORY_RENTALS_OBJECT_ID;



                }


                OffersList nextFrag = new OffersList();

                // Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
                // Commit the transaction

                HomeFragment.this.getFragmentManager().beginTransaction()
                        .replace(R.id.myFragmentMenuWindow, nextFrag,"tagOfferList")
                        //.addToBackStack(null)
                        .commit();


            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Constants.CURRENT_SCREEN = Constants.SCREEN_HOME_PAGE;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
