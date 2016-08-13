package com.scu.tausch.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.scu.tausch.Adapters.CustomListAdapter;
import com.scu.tausch.DB.DBAccessor;
import com.scu.tausch.DTO.OfferDTO;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.R;

import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Praneet on 1/31/16.
 */
public class OffersList extends Fragment implements DBListener{


    private List<ParseObject> itemObjects;
    private String[] arrayItemNames;
    private Bitmap[] arrayItemImages;
    private Double[] arrayItemCosts;
    private ListView listViewItems;
    private ProgressDialog progress;
    private boolean isFilterActive=false;
    private boolean isSortActive=false;
    private boolean isSearchActive=false;
    private TextView emptyListTextView;
    static List<ParseObject> retainItemObjects;
    static String currentCategoryId;
    private DBAccessor dbAccessor;
    private String selectedValue;
    private Button buttonSort, buttonFilter;
    private List<ParseObject> searchResults;
    static List<List<Bitmap>> listOfImageLists;


    public OffersList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    //    DBAccessor.searchCode = Constants.SEARCH_CODE_HOME_PAGE;  // defect fix - defect no. 28
        listOfImageLists = new ArrayList<>();

        progress = new ProgressDialog(getActivity());
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    public void setRetainItemObjects(List<ParseObject> retainItemObjects){
        this.retainItemObjects=retainItemObjects;
    }

    private void setArraysForNamesImagesCost(List<ParseObject> arrayOfItemObjects){

        List<String> arrayTitles = new ArrayList<>();
        List<Bitmap> arrayImages = new ArrayList<>();
        List<Double> arrayPrice = new ArrayList<>();

        if (arrayOfItemObjects!=null && arrayOfItemObjects.size()>0) {


            for (ParseObject itemObject : arrayOfItemObjects) {

                List<Bitmap> arrayItemFiveImages = new ArrayList<>();

                String itemTitle = (String) itemObject.get(Constants.DB_Offer_Title);
                arrayTitles.add(itemTitle);
                //  double doubleVal =
                Double itemPrice = ((Number) itemObject.get(Constants.DB_Price)).doubleValue();
                arrayPrice.add(itemPrice);

                try {
                    ParseFile bum = (ParseFile) itemObject.get(Constants.DB_Image_ONE);
                    byte[] file = bum.getData();
                    Bitmap image = BitmapFactory.decodeByteArray(file, 0, file.length);
                    arrayImages.add(image);
                    arrayItemFiveImages.add(image);

                    ParseFile bumTwo = (ParseFile) itemObject.get(Constants.DB_Image_TWO);
                    byte[] fileTwo = bumTwo.getData();
                    Bitmap imageTwo = BitmapFactory.decodeByteArray(fileTwo, 0, fileTwo.length);
                    arrayItemFiveImages.add(imageTwo);

                    ParseFile bumThree = (ParseFile) itemObject.get(Constants.DB_Image_THREE);
                    byte[] fileThree = bumThree.getData();
                    Bitmap imageThree = BitmapFactory.decodeByteArray(fileThree, 0, fileThree.length);
                    arrayItemFiveImages.add(imageThree);

                    ParseFile bumFour = (ParseFile) itemObject.get(Constants.DB_Image_FOUR);
                    byte[] fileFour = bumFour.getData();
                    Bitmap imageFour = BitmapFactory.decodeByteArray(fileFour, 0, fileFour.length);
                    arrayItemFiveImages.add(imageFour);

                    ParseFile bumFive = (ParseFile) itemObject.get(Constants.DB_Image_FIVE);
                    byte[] fileFive = bumFive.getData();
                    Bitmap imageFive = BitmapFactory.decodeByteArray(fileFive, 0, fileFive.length);
                    arrayItemFiveImages.add(imageFive);

                    if (listOfImageLists == null) {
                        listOfImageLists = new ArrayList<>();
                    }
                    listOfImageLists.add(arrayItemFiveImages);

                } catch (ParseException e) {

                }
            }
        }

        arrayItemNames = arrayTitles.toArray(new String[arrayOfItemObjects.size()]);
        arrayItemCosts = arrayPrice.toArray(new Double[arrayOfItemObjects.size()]);
        arrayItemImages = arrayImages.toArray(new Bitmap[arrayOfItemObjects.size()]);

    }


    public void searchList(List<ParseObject> offers, String searchStr){

        if (progress != null) {
            progress.dismiss();
        }
        isSearchActive=true;

        itemObjects = null;
        itemObjects=offers;
        searchResults = new ArrayList<>();
       // List<OfferDTO> offers = new ArrayList<OfferDTO>();

        // commented as we do not want this function any more

        //offers = searchOffersWithASearchStr(searchedObjects, offers, searchStr);
        //setArraysForSearchResults(offers);


        // offers is a list of all parse objects that we fetched ( these are not searched offers)
        // searchResults is a list of all searched parse objects ( here we search all offers with the user's input string)
        searchResults =  searchOffersWithASearchStr(offers,searchResults, searchStr);
        setArraysForNamesImagesCost(searchResults);


        //REMOVE IF ISSUE IS THERE AS IT RESOLVES THE ISSUE TO FILTER CURRENT SCREEN ITEMS.
        itemObjects = searchResults;

//        if (progress!=null){
//            progress.dismiss();
//        }
//        if (HomePage.progress!=null && HomePage.progress.isShowing()){
//            HomePage.progress.dismiss();
//        }

    }

    public List<ParseObject>  searchOffersWithASearchStr(List<ParseObject> offersParseList,List<ParseObject> searchResults, String searchStr){

        // till here we fetched all the offers specific to a category and which are open offers...
       // progress.dismiss();
        if(offersParseList.size()>0) {
            for (ParseObject parseOffer : offersParseList) {
                String itemTitle = (String) parseOffer.get(Constants.DB_Offer_Title);
                if (itemTitle != null && !itemTitle.isEmpty()
                        && itemTitle.toLowerCase().contains(searchStr.toLowerCase())) {
                    searchResults.add(parseOffer);
                }
            }
        }
        return searchResults;
    }






    /*public List<OfferDTO>  searchOffersWithASearchStr(List<ParseObject> offersParseList, List<OfferDTO> offersList, String searchStr){

        // till here we fetched all the offers specific to a category and which are open offers...
        List<OfferDTO> searchedOffers = new ArrayList<OfferDTO>();
        //progress.dismiss();
        if(offersParseList.size()>0) {
            for (ParseObject parseOffer : offersParseList) {
                Bitmap image = null;
                try {
                    ParseFile bum = (ParseFile) parseOffer.get(Constants.DB_Image_ONE);
                    byte[] file = bum.getData();
                    image = BitmapFactory.decodeByteArray(file, 0, file.length);

                }
                catch (ParseException e){
                }
                OfferDTO offer = new OfferDTO(
                        parseOffer.getObjectId(),
                        parseOffer.getString("offeror"),
                        parseOffer.getString("category_id"),
                        parseOffer.getString("offer_title"),
                        parseOffer.getString("offer_description"),
                        parseOffer.getString("city"),
                        parseOffer.getString("zipcode"),
                        parseOffer.getString("condition"),
                        parseOffer.getString("rental_type"),
                        parseOffer.getDouble("price"),
                        image,
                        parseOffer.getString("offeror"),
                        parseOffer.getBoolean("status")
                );
                offersList.add(offer);
            }
        }

        // now perform the search on the offersList to search the string -
        // currently performed search on below columns: offer-title, offer_description, zipcode, price and condition
        // Can add others but they are all IDs stored like category_id,condition_id
        if(offersList!=null && !offersList.isEmpty()) {
            for (OfferDTO offer : offersList) {
                if (
                        offer.getOfferTitle().toLowerCase().contains(searchStr.toLowerCase()) ||
                                offer.getOfferDescription().toLowerCase().contains(searchStr.toLowerCase())
                                //offer.getPrice().toLowerCase().contains(searchStr.toLowerCase()) ||
                                //offer.getZip().toLowerCase().contains(searchStr.toLowerCase()) ||
                                //offer.getCondition().toLowerCase().contains(searchStr.toLowerCase()) ||
                                //offer.getCityId().toLowerCase().contains(searchStr.toLowerCase())
                        )
                    searchedOffers.add(offer);
            }
        }

        // searchedOffers will only contain those offers which has the string that is to be searched
        if(searchedOffers!=null && !searchedOffers.isEmpty()) {
            {
               // Toast.makeText(this,Toast.LENGTH_SHORT).show();
                int count = 0;
                for (OfferDTO o : searchedOffers) {
                    count++;
                }
            }
        }
        return searchedOffers;
    }*/



   /* private void setArraysForSearchResults(List<OfferDTO> offers){

        List<String> arrayTitles = new ArrayList<>();
        List<Bitmap> arrayImages = new ArrayList<>();
        List<Double> arrayPrice = new ArrayList<>();

        for(OfferDTO offer:offers){

            String itemTitle = offer.getOfferTitle();
            arrayTitles.add(itemTitle);
            double itemPrice = offer.getPrice();
            arrayPrice.add(itemPrice);
                //ParseFile bum = (ParseFile) itemObject.get(Constants.DB_Image_ONE);
                //byte[] file = bum.getData();
                Bitmap image = offer.getImage_one();
                arrayImages.add(image);
        }

        arrayItemNames = arrayTitles.toArray(new String[offers.size()]);
        arrayItemCosts = arrayPrice.toArray(new Double[offers.size()]);
        arrayItemImages = arrayImages.toArray(new Bitmap[offers.size()]);

    }*/

 public void filterList(List<ParseObject> filteredObjects){
     isFilterActive=true;
     itemObjects = null;
     itemObjects=filteredObjects;
     setArraysForNamesImagesCost(filteredObjects);
 }

    public void sortList(List<ParseObject> sortedObjects){
        isSortActive=true;
        itemObjects = null;
        itemObjects=sortedObjects;
        setArraysForNamesImagesCost(sortedObjects);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_offers_list, container, false);

        buttonFilter = (Button) rootView.findViewById(R.id.button_filter);
        buttonSort = (Button) rootView.findViewById(R.id.button_sort);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                return false;
            }
        });


        listViewItems=(ListView)rootView.findViewById(R.id.list_items_in_category);
        Toolbar toolbarBottom = (Toolbar)rootView.findViewById(R.id.toolbarBottom);
        emptyListTextView=(TextView)rootView.findViewById(android.R.id.empty);


        toolbarBottom.setBackgroundColor(Color.TRANSPARENT);

        /*Spinner spinnerSort = new Spinner(getActivity());
        //adding sort criteria
        List<String> sortCriteria = new ArrayList<>();
        sortCriteria.add(Constants.SORT_PRICE_LOW_TO_HIGH);
        sortCriteria.add(Constants.SORT_PRICE_HIGH_TO_LOW);
        sortCriteria.add(Constants.SORT_DATE_OLD_TO_NEW);
        sortCriteria.add(Constants.SORT_DATE_NEW_TO_OLD);
        //Creating and setting adapter to array of sort criteria required in spinner.
        ArrayAdapter<String> adapterSortCriteria = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, sortCriteria);
        adapterSortCriteria.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerSort.setAdapter(adapterSortCriteria);

        int spinnerSort_X = 20;
        spinnerSort.setX(spinnerSort_X);
        spinnerSort.setBackgroundColor(Color.TRANSPARENT);*/
//
//        Button buttonFilter = new Button(getActivity());
//        float buttonFilter_X = 20;
//        buttonFilter.setX(buttonFilter_X);
//        buttonFilter.setWidth(100);
//
//        buttonFilter.setText("Filter");
//        buttonFilter.setTextColor(Color.WHITE);
//        buttonFilter.setBackgroundColor(Color.TRANSPARENT);
//        buttonFilter.setBackgroundColor(Color.TRANSPARENT);
//        toolbarBottom.addView(buttonFilter);
        //toolbarBottom.addView(spinnerSort);

//        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
//        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

//        Button buttonSort = new Button((getActivity()));
//        buttonSort.setWidth(100);
//        float buttonSort_X = (dpWidth-100)-20;
//
//        buttonSort.setX(buttonSort_X);
//        buttonSort.setText("Sort");
//        buttonSort.setTextColor(Color.WHITE);
//        buttonSort.setBackgroundColor(Color.TRANSPARENT);
//        toolbarBottom.addView(buttonSort);
//



        buttonFilter.setOnClickListener(new View.OnClickListener() {

            FilterFragment fragment = null;
            String title;

            @Override
            public void onClick(View v) {

                title = getString(R.string.app_name);

                fragment = new FilterFragment();
                if (itemObjects == null) {
                    progress.dismiss();
                    return;
                }

                //COMMENT THIS IF CONDITION IF ISSUE IS THERE.
                if (itemObjects.size() == 0 || itemObjects == null) {
                    itemObjects = retainItemObjects;
                }
                fragment.fetchedItemObjects(itemObjects);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();
            }
        });


        buttonSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] items = {Constants.SORT_PRICE_LOW_TO_HIGH, Constants.SORT_PRICE_HIGH_TO_LOW, Constants.SORT_DATE_OLD_TO_NEW, Constants.SORT_DATE_NEW_TO_OLD};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select sort type");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        //mDoneButton.setText(items[item]);
                        selectedValue = items[item];
                        performSortAsOptionSelected();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });



        //spinnerSort.getSelectedItem().toString()

        if (isFilterActive) {
            fetchedFilterData();
            isFilterActive=false;
            progress.dismiss();
        }
        if (isSortActive) {
            fetchedDataFromServer();
            isSortActive=false;
            progress.dismiss();
        }
        if (isSearchActive){
            fetchedSearchListDataFromServer();
            isSearchActive=false;
            progress.dismiss();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    //Method is called when data has been fetched from server.
    public void fetchedDataFromServer(){

        CustomListAdapter customListAdapter = new CustomListAdapter(getActivity(),arrayItemNames,arrayItemCosts,arrayItemImages);
        listViewItems.setAdapter(customListAdapter);

        if (itemObjects.size() == 0) {
            emptyListTextView.setText("No items found.");
            listViewItems.setEmptyView(emptyListTextView);
        }

        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                DetailedItemFragment nextFrag = new DetailedItemFragment();

                if (arrayItemImages.length > 0 && arrayItemNames.length > 0 && arrayItemCosts.length > 0) {
                    nextFrag.setArguments(itemObjects.get(position), arrayItemImages, position, arrayItemNames, arrayItemCosts);
                }


                OffersList.this.getFragmentManager().beginTransaction()
                        .replace(R.id.myItemsInCategoryWindow, nextFrag, Constants.TAG_Item_Details_Fragment)
                        .addToBackStack(null)
                        .commit();


            }
        });

    }

    //Method is called when data has been fetched from server.
    public void fetchedFilterData(){

        CustomListAdapter customListAdapter = new CustomListAdapter(getActivity(),arrayItemNames,arrayItemCosts,arrayItemImages);
        listViewItems.setAdapter(customListAdapter);

        if (itemObjects.size() == 0) {
            emptyListTextView.setText("No items found.");
            listViewItems.setEmptyView(emptyListTextView);
        }

        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                DetailedItemFragment nextFrag = new DetailedItemFragment();

                if (arrayItemImages.length > 0 && arrayItemNames.length > 0 && arrayItemCosts.length > 0) {
                    nextFrag.setArguments(itemObjects.get(position), arrayItemImages, position, arrayItemNames, arrayItemCosts);
                }


                OffersList.this.getFragmentManager().beginTransaction()
                        .replace(R.id.myItemsInCategoryWindow, nextFrag, Constants.TAG_Item_Details_Fragment)
                        .addToBackStack(null)
                        .commit();


            }
        });

    }

    //Method is called when data has been fetched from server.
    public void fetchedSearchListDataFromServer(){

        CustomListAdapter customListAdapter = new CustomListAdapter(getActivity(),arrayItemNames,arrayItemCosts,arrayItemImages);
        listViewItems.setAdapter(customListAdapter);

        if (itemObjects.size() == 0) {
            emptyListTextView.setText("No items found.");
            listViewItems.setEmptyView(emptyListTextView);
        }

        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                DetailedItemFragment nextFrag = new DetailedItemFragment();

                if (arrayItemImages.length > 0 && arrayItemNames.length > 0 && arrayItemCosts.length > 0) {
                    nextFrag.setArguments(searchResults.get(position), arrayItemImages, position, arrayItemNames, arrayItemCosts);
                }


                OffersList.this.getFragmentManager().beginTransaction()
                        .replace(R.id.myItemsInCategoryWindow, nextFrag, Constants.TAG_Item_Details_Fragment)
                        .addToBackStack(null)
                        .commit();


            }
        });

    }

    public void performSortAsOptionSelected(){
        List<ParseObject> sortedObjects = itemObjects;

        if(selectedValue.equals(Constants.SORT_PRICE_LOW_TO_HIGH)) {
            Collections.sort(sortedObjects, new Comparator<ParseObject>() {
                @Override
                public int compare(ParseObject o1, ParseObject o2) {
                    return Double.compare(Double.parseDouble(o1.get(Constants.DB_Price).toString()),
                            Double.parseDouble(o2.get(Constants.DB_Price).toString()));
                }
            });
        } else if(selectedValue.equals(Constants.SORT_PRICE_HIGH_TO_LOW)) {
            Collections.sort(sortedObjects, new Comparator<ParseObject>() {
                @Override
                public int compare(ParseObject o1, ParseObject o2) {
                    return Double.compare(Double.parseDouble(o2.get(Constants.DB_Price).toString()),
                            Double.parseDouble(o1.get(Constants.DB_Price).toString()));
                }
            });
        } else if(selectedValue.equals(Constants.SORT_DATE_OLD_TO_NEW)) {
            Collections.sort(sortedObjects, new Comparator<ParseObject>() {
                @Override
                public int compare(ParseObject o1, ParseObject o2) {
                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                }
            });
        } else if(selectedValue.equals(Constants.SORT_DATE_NEW_TO_OLD)) {
            Collections.sort(sortedObjects, new Comparator<ParseObject>() {
                @Override
                public int compare(ParseObject o1, ParseObject o2) {
                    return o1.getCreatedAt().compareTo(o2.getCreatedAt());
                }
            });
        }
        OffersList fragment = new OffersList();
        fragment.sortList(sortedObjects);
        fragment.setRetainItemObjects(itemObjects);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment, "tagOfferList");
        fragmentTransaction.commit();
    }

    @Override
    public void callback(List<ParseObject> objects) {

        setRetainItemObjects(objects);
        itemObjects=objects;

        setArraysForNamesImagesCost(objects);

        fetchedDataFromServer();
        progress.dismiss();

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Constants.CURRENT_SCREEN = Constants.SCREEN_OFFERS_LIST;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
