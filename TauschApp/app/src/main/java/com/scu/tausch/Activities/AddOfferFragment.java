package com.scu.tausch.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.scu.tausch.DB.DBAccessor;
import com.scu.tausch.DTO.OfferDTO;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Praneet on 2/11/16.
 */
public class AddOfferFragment extends Fragment{

    public static HomePage context;
    private OfferDTO offerDTO;
    private ProgressDialog progress;
    private TextView textCityName;
    private boolean isPriceValid;
    private boolean isFormFilled;
    private String cityName = "";
    private boolean hasNectButtonTapped = false;
    EditText editTitle;
    EditText editDescription;
    EditText editPrice;
    EditText editZip;
    Spinner spinnerCategory;
    Spinner spinnerCondition;


    private static final String ACTION_FOR_INTENT_CALLBACK = "THIS_IS_A_UNIQUE_KEY_WE_USE_TO_COMMUNICATE";

    public AddOfferFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBAccessor.searchCode = Constants.SEARCH_CODE_HOME_PAGE;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_offer, container, false);



        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                return false;
            }
        });

        spinnerCategory = (Spinner)rootView.findViewById(R.id.spinner_category);
        spinnerCondition = (Spinner)rootView.findViewById(R.id.spinner_condition);
        editTitle = (EditText)rootView.findViewById(R.id.edit_titleListing);
        editDescription = (EditText)rootView.findViewById(R.id.edit_description);
        editPrice = (EditText)rootView.findViewById(R.id.edit_price);
        editZip = (EditText)rootView.findViewById(R.id.edit_zip);
        textCityName=(TextView)rootView.findViewById(R.id.text_city);


        textCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editZip.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editZip, InputMethodManager.SHOW_IMPLICIT);
                editZip.setInputType(InputType.TYPE_CLASS_NUMBER);            }
        });


        editZip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {

                    //fetching value from webservice to usps server.
                    checkIfZipCodeIsCorrect();
                    return;

                }


            }
        });


        //adding all categories in list.
        List<String> categories = new ArrayList<>();
        categories.add(Constants.Array_Category_Automobiles);
        categories.add(Constants.Array_Category_Books);
        categories.add(Constants.Array_Category_Laptops);
        categories.add(Constants.Array_Category_Furniture);
        categories.add(Constants.Array_Category_Rentals);

        //adding type whether used or new to list.
        List<String> conditions = new ArrayList<>();
        conditions.add(Constants.ITEM_TYPE_NEW);
        conditions.add(Constants.ITEM_TYPE_USED);



        //Creating and setting adapter to array of categories required in spinner.
        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, categories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerCategory.setAdapter(adapterCategories);
        spinnerCategory.setSelection(Constants.CURRENT_SELECTED_CATEGORY);

        //Creating and setting adapter to array of conditions required in spinner.
        ArrayAdapter<String> adapterConditions = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, conditions);
        adapterConditions.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerCondition.setAdapter(adapterConditions);

/*
* Setting data to dto object on click of next button to retain value.
*
* */
        Button buttonNext = (Button)rootView.findViewById(R.id.button_next);

        buttonNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //    title = getString(R.string.app_name);

                hasNectButtonTapped = true;

              boolean isComplete =  isFormComplete(editTitle.getText().toString().trim(), editDescription.getText().toString().trim(), editPrice.getText().toString(),editZip.getText().toString().trim(),"");

                if (isComplete){
                    isFormFilled=true;
                    checkIfZipCodeIsCorrect();
                }
                else{
                    isFormFilled=false;
                   // showDialogBox();
                    return;
                }

            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }


    public void showDialogBox(){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Incomplete!");
        alertDialog.setMessage("Please fill all the fields.");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();

    }

    public void showDialogBoxForIncorrectZip(){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Incorrect Zip Code!");
        alertDialog.setMessage("Please provide valid zip code.");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();

    }

    //Get the id of category used in database.
    public String getCategoryId(String categoryName){

        if (categoryName.equals(Constants.Array_Category_Automobiles)){
            return Constants.CATEGORY_AUTOMOBILES_OBJECT_ID;
        }
        if (categoryName.equals(Constants.Array_Category_Books)){
            return Constants.CATEGORY_BOOKS_OBJECT_ID;
        }
        if (categoryName.equals(Constants.Array_Category_Laptops)){
            return Constants.CATEGORY_LAPTOPS_OBJECT_ID;
        }
        if (categoryName.equals(Constants.Array_Category_Furniture)){
            return Constants.CATEGORY_FURNITURE_OBJECT_ID;
        }
        if (categoryName.equals(Constants.Array_Category_Rentals)){
            return Constants.CATEGORY_RENTALS_OBJECT_ID;
        }
        return null;
    }


    //Verification if all the required fields in the form are provided by user.
    public boolean isFormComplete(String title, String description, String price, String zip, String city){

        boolean isComplete = true;

        if (title.length()==0){
            isComplete=false;
            showDialogBox();
        }
        else if (description.length()==0){
            isComplete=false;
            showDialogBox();
        }
        else if(checkIfValueIsIntegerType(""+price)==false){
         isComplete=false;
            showDialogBox();
        }
        else if (("" + price).length() == 0 || isPriceValid == false) {
            isComplete=false;
            showDialogBox();
        }
        else if(zip.length()==0 ){
            isComplete=false;
            showDialogBox();
        }


        return isComplete;
    }


    public boolean callback(String cityName) {


        offerDTO = new OfferDTO();

        offerDTO.setOfferTitle(editTitle.getText().toString().trim());
        offerDTO.setOfferDescription(editDescription.getText().toString().trim());
        if (editPrice.getText().toString().trim().length() > 0) {

            if (checkIfValueIsIntegerType(editPrice.getText().toString().trim())) {

                offerDTO.setPrice(Double.parseDouble(editPrice.getText().toString().trim()));
                isPriceValid = true;

            } else {
                isPriceValid = false;
            }
        }
        offerDTO.setZip(editZip.getText().toString().trim());
        offerDTO.setCategoryId(getCategoryId(spinnerCategory.getSelectedItem().toString()));
        offerDTO.setCondition(spinnerCondition.getSelectedItem().toString());
        offerDTO.setOfferorName((String) ParseUser.getCurrentUser().get("firstname"));

        if (progress!=null) {
            progress.dismiss();
        }

        if (editZip.getText().toString().trim()==null || offerDTO.getZip().length()==0){

            return false;
        }
        if (cityName.length()>0 && offerDTO!=null) {
            offerDTO.setCityId(cityName);
            textCityName.setText(offerDTO.getCityId());

        }
        else{

            return false;
        }

        return true;

    }

    private void goToSecondScreen(){

        ImageAddFragment nextFrag= new ImageAddFragment();

        nextFrag.currentOfferDetails(offerDTO);

        //Below, addToBackStack(null) means that if we transition to next fragment, this
        //fragment will not be destroyed and would appear again if user taps back button.
        //addToBackStack(null) works for current fragment, like
        //AddOfferFragment in this case.

        AddOfferFragment.this.getFragmentManager().beginTransaction()
                .replace(R.id.container_body, nextFrag, Constants.TAG_Image_Add)
                .addToBackStack(null)
                .commit();

    }


    private void checkIfZipCodeIsCorrect(){



        String urll = "http://ziptasticapi.com/"+editZip.getText().toString().trim();


        class RestTask extends AsyncTask<HttpUriRequest, Void, String> {
            private static final String TAG = "AARestTask";
            public static final String HTTP_RESPONSE = "httpResponse";

            private Context mContext;
            private HttpClient mClient;
            private String mAction;

            public RestTask(Context context, String action) {
                mContext = context;
                mAction = action;
                mClient = new DefaultHttpClient();
            }


            @Override
            protected String doInBackground(HttpUriRequest... params) {
                try {
                    HttpUriRequest request = params[0];
                    HttpResponse serverResponse = mClient.execute(request);
                    BasicResponseHandler handler = new BasicResponseHandler();
                    return handler.handleResponse(serverResponse);
                } catch (Exception e) {
                    // TODO handle this properly
                    e.printStackTrace();
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                Log.i(TAG, "RESULT = " + result);

                try {
                    JSONObject reader = new JSONObject(result);

                    String status = "OK";

                    if (reader.has("city")) {

                        String stringCityName = reader.optString("city");
                        Log.d("CITY NAME: ",stringCityName);

                        cityName = stringCityName;
                        textCityName.setText(stringCityName);

                        if (hasNectButtonTapped && isFormFilled) {
                            callback(cityName);
                            goToSecondScreen();
                            return;
                        }

                    }
                    else
                    {
                        status = "ERROR";
                        editZip.setText(null);
                        textCityName.setText(null);
                        hasNectButtonTapped = false;
                        showDialogBoxForIncorrectZip();
                        return;
                    }


                }
                catch (JSONException ex){
                    ex.printStackTrace();

                    return;
                }

            }

        }


        try {
            HttpGet httpGet = new HttpGet(new URI(urll));
            RestTask task = new RestTask(getActivity(), ACTION_FOR_INTENT_CALLBACK);
            task.execute(httpGet);
            //   progress = ProgressDialog.show(getActivity(), "Getting Data ...", "Waiting For Results...", true);
        } catch (Exception e) {
            showDialogBoxForIncorrectZip();
            //  Log.e(TAG, e.getMessage());
        }

    }


    public boolean checkIfValueIsIntegerType(String value) {

        try {
            double tempValue = Double.parseDouble(value);
            isPriceValid=true;
            return true;
        } catch (NumberFormatException nfe) {
            isPriceValid=false;
            return false;
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Constants.CURRENT_SCREEN = Constants.SCREEN_OFFER_POST_1;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}


/**
 * Android RestTask (REST) from the Android Recipes book.
 */


