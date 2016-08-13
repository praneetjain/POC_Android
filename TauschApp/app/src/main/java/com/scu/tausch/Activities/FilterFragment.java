package com.scu.tausch.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseObject;
import com.scu.tausch.DB.DBAccessor;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Praneet on 2/11/16.
 */
public class FilterFragment extends Fragment {

    private List<ParseObject> itemObjects;
    private List<ParseObject> retainedList;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog postedAfterDatePickerDialog;

    public FilterFragment() {
        // Required empty public constructor
    }

    public void setRetainArray(List<ParseObject> fullList){

        retainedList = fullList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBAccessor.searchCode = Constants.SEARCH_CODE_HOME_PAGE;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    public void fetchedItemObjects(List<ParseObject> itemObjects){
        this.itemObjects=itemObjects;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                return false;
            }
        });

        final Spinner spinnerCondition = (Spinner)rootView.findViewById(R.id.spinner_condition);
        final EditText editCity = (EditText)rootView.findViewById(R.id.edit_city_of_item);
        final EditText editMin = (EditText)rootView.findViewById(R.id.edit_min_price);
        final EditText editMax = (EditText)rootView.findViewById(R.id.edit_max_price);
        //final EditText editPostedAfterDate = (EditText) rootView.findViewById(R.id.edit_posted_after_date);
        Button filterButton = (Button)rootView.findViewById(R.id.button_filter);
        Button cancelButton = (Button)rootView.findViewById(R.id.button_cancel);
        /*editPostedAfterDate.setInputType(InputType.TYPE_NULL);
        editPostedAfterDate.requestFocus();

        editPostedAfterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postedAfterDatePickerDialog.show();
            }
        });*/

       /* dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        Calendar newCalendar = Calendar.getInstance();

        postedAfterDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editPostedAfterDate.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));*/




        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String condition = spinnerCondition.getSelectedItem().toString().trim();
                String city = editCity.getText().toString().trim();
                String strMin = editMin.getText().toString().trim();
                String strMax = editMax.getText().toString().trim();
                double min=0;
                double max=0;
                if (strMin.length()>0) {
                    min = Double.parseDouble(strMin);
                }
               if (strMax.length()>0) {
                   max = Double.parseDouble(strMax);
               }

                List<ParseObject> filteredObjects = new ArrayList<>();

                if (itemObjects!=null) {
                    for (ParseObject object : itemObjects) {

                        String pricee = "";

                        if ((((Number) object.get(Constants.DB_Price)).toString()).length() == 0) {
                            pricee = "0";
                        } else {
                            pricee = ((Number) object.get(Constants.DB_Price)).toString();
                        }

                        String lCondition = (String) object.get(Constants.DB_CONDITION);
                        String lCity = (String) object.get(Constants.DB_CITY);
                        double price = Double.parseDouble(pricee);

                        if (lCity.equalsIgnoreCase(city) || city.length() == 0) {
                            if (price >= min || price == 0) {
                                if (price <= max || price == 0 || max == 0) {
                                    if (lCondition.equalsIgnoreCase(condition)) {
                                            filteredObjects.add(object);
                                    } else if (condition.equalsIgnoreCase(Constants.ITEM_TYPE_All)) {
                                            filteredObjects.add(object);
                                    }
                                }
                            }
                        }
                    }
                }
                OffersList fragment = new OffersList();
                fragment.filterList(filteredObjects);
                fragment.setRetainItemObjects(itemObjects);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment,"tagOfferList");
                fragmentTransaction.commit();





            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HomeFragment nextFrag = new HomeFragment();

                FilterFragment.this.getFragmentManager().beginTransaction()
                        .replace(R.id.container_body, nextFrag)
                        .commit();


            }
        });


        List<String> categories = new ArrayList<>();
        categories.add(Constants.Array_Category_Automobiles);
        categories.add(Constants.Array_Category_Books);
        categories.add(Constants.Array_Category_Laptops);
        categories.add(Constants.Array_Category_Furniture);
        categories.add(Constants.Array_Category_Rentals);

        List<String> conditions = new ArrayList<>();
        conditions.add(Constants.ITEM_TYPE_All);
        conditions.add(Constants.ITEM_TYPE_NEW);
        conditions.add(Constants.ITEM_TYPE_USED);

        ArrayAdapter<String> adapterConditions = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, conditions);
        adapterConditions.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerCondition.setAdapter(adapterConditions);


        // Inflate the layout for this fragment
        return rootView;
    }

    private void showDialogToEnterDescription(){

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Description is required!");

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Constants.CURRENT_SCREEN = Constants.SCREEN_FILTER_PAGE;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
