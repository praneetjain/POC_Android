package com.scu.tausch.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.scu.tausch.DB.DBAccessor;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.R;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Praneet on 1/31/16.
 */
public class DetailedItemFragment extends Fragment{

    private ImageButton messageButton;
    private String title;
    private String description;
    private Bitmap image_one, image_two, image_three, image_four, image_five;
    private double item_price;
    private String offeror;
    private String condition;
    private String city;
    String receiverEmail;
    String receiverName;
    String senderName;
    List<Bitmap> itemFiveImages;
    private int imageNumberToDisplay = 0;
    private ImageView leftArrow;
    private ImageView rightArrow;
    private TextView textOfferor;
    private ImageView imageItemView;
    private ImageView dotOne, dotTwo, dotThree, dotFour, dotFive;
    private boolean isOfferByOfferor = false;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

private String receiverObjectId;

    public DetailedItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBAccessor.searchCode = Constants.SEARCH_CODE_HOME_PAGE;

    }

            public void setArguments(final ParseObject itemObject, Bitmap[] images,int positionInList,String[] titles, Double[] prices){

                title = titles[positionInList];
                description = (String)itemObject.get(Constants.DB_OFFER_DESCRIPTION);
                image_one = images[positionInList];
                item_price = prices[positionInList];
                offeror = (String)itemObject.get(Constants.DB_OFFEROR);
                condition = (String)itemObject.get(Constants.DB_CONDITION);
                city = (String)itemObject.get(Constants.DB_CITY);

                if (OffersList.listOfImageLists.size() != 0) {
                    itemFiveImages = OffersList.listOfImageLists.get(positionInList);
                } else {
                    if (itemFiveImages == null) {
                        itemFiveImages = new ArrayList<>();
                    }

                    try {
                        ParseFile bum = (ParseFile) itemObject.get(Constants.DB_Image_ONE);
                        byte[] file = bum.getData();
                        Bitmap image = BitmapFactory.decodeByteArray(file, 0, file.length);
                        itemFiveImages.add(image);

                        ParseFile bumTwo = (ParseFile) itemObject.get(Constants.DB_Image_TWO);
                        byte[] fileTwo = bumTwo.getData();
                        Bitmap imageTwo = BitmapFactory.decodeByteArray(fileTwo, 0, fileTwo.length);
                        itemFiveImages.add(imageTwo);

                        ParseFile bumThree = (ParseFile) itemObject.get(Constants.DB_Image_THREE);
                        byte[] fileThree = bumThree.getData();
                        Bitmap imageThree = BitmapFactory.decodeByteArray(fileThree, 0, fileThree.length);
                        itemFiveImages.add(imageThree);

                        ParseFile bumFour = (ParseFile) itemObject.get(Constants.DB_Image_FOUR);
                        byte[] fileFour = bumFour.getData();
                        Bitmap imageFour = BitmapFactory.decodeByteArray(fileFour, 0, fileFour.length);
                        itemFiveImages.add(imageFour);

                        ParseFile bumFive = (ParseFile) itemObject.get(Constants.DB_Image_FIVE);
                        byte[] fileFive = bumFive.getData();
                        Bitmap imageFive = BitmapFactory.decodeByteArray(fileFive, 0, fileFive.length);
                        itemFiveImages.add(imageFive);

//                        if (listOfImageLists == null) {
//                            listOfImageLists = new ArrayList<>();
//                        }
//                        listOfImageLists.add(arrayItemFiveImages);

                    } catch (ParseException e) {


                    }


                }

                ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
               query.whereEqualTo("objectId", itemObject.get("user_id"));
// Retrieve the object by id

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        receiverEmail = (String) (objects.get(0).get("email"));
                        receiverObjectId = (String)itemObject.get("user_id");
                        receiverName = (String)(objects.get(0).get("firstname"));
                        senderName = (String) ParseUser.getCurrentUser().get("firstname");


                        if (receiverEmail.equals(ParseUser.getCurrentUser().getEmail())) {
                            isOfferByOfferor = true;
                            messageButton.setVisibility(View.GONE);
                            textOfferor.setText("You");

                            return;
                        } else {
                            isOfferByOfferor = false;
                            messageButton.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detailed_item, container, false);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                return false;
            }
        });

        dotOne = (ImageView)rootView.findViewById(R.id.dot_one);
        dotTwo = (ImageView)rootView.findViewById(R.id.dot_two);
        dotThree = (ImageView)rootView.findViewById(R.id.dot_three);
        dotFour = (ImageView)rootView.findViewById(R.id.dot_four);
        dotFive = (ImageView)rootView.findViewById(R.id.dot_five);

        messageButton = (ImageButton)rootView.findViewById(R.id.icon_message_box);
        TextView textTitle = (TextView)rootView.findViewById(R.id.item_title);
        TextView textDescription=(TextView)rootView.findViewById(R.id.item_description);
        final ImageView imageItem = (ImageView) rootView.findViewById(R.id.item_image);
        imageItemView = imageItem;
        textOfferor = (TextView) rootView.findViewById(R.id.value_name);
        TextView textPrice = (TextView)rootView.findViewById(R.id.value_price);
        TextView textCondition = (TextView)rootView.findViewById(R.id.value_condition);
        TextView textCity = (TextView)rootView.findViewById(R.id.value_city);
        LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.layout_dots);
     //   leftArrow = (ImageView) rootView.findViewById(R.id.arrow_left);
     //   rightArrow = (ImageView) rootView.findViewById(R.id.arrow_right);

        textTitle.setText(title);
        textDescription.setText(description);
        imageItem.setImageBitmap(image_one);
        textOfferor.setText(offeror);
        textPrice.setText("$" + item_price);
        textCondition.setText(condition);
        textCity.setText(city);
       // leftArrow.setVisibility(View.INVISIBLE);


        gestureDetector = new GestureDetector(getActivity(), new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

//        imageItem.setOnClickListener(getActivity());
        imageItem.setOnTouchListener(gestureListener);

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                Constants.PUSH_RECEIVED = sharedpreferences.getBoolean("push_received",false);


                Constants.WAS_LAST_SCREEN_ITEM_DESCRIPTION = true;

                ChatFragment nextFrag= new ChatFragment();
                nextFrag.setArgumentsForMessageSending(receiverEmail,receiverObjectId, receiverName);


                DetailedItemFragment.this.getFragmentManager().beginTransaction()
                        .replace(R.id.myDetailedItemFragment, nextFrag,Constants.TAG_Chat_Fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    public void leftArrowClicked() {

        clearAllDots();
        if (imageNumberToDisplay > 0) {
            imageNumberToDisplay--;
            selectDot(imageNumberToDisplay);
            imageItemView.setImageBitmap(itemFiveImages.get(imageNumberToDisplay));
        }
        if (imageNumberToDisplay==0) {
            dotOne.setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

    public void rightArrowClicked() {

        clearAllDots();
        if (imageNumberToDisplay < 4) {
            imageNumberToDisplay++;
           selectDot(imageNumberToDisplay);
            imageItemView.setImageBitmap(itemFiveImages.get(imageNumberToDisplay));
        }
if (imageNumberToDisplay==4){
    dotFive.setBackgroundColor(Color.parseColor("#ffffff"));
}
    }

    public void selectDot(int imageNumberToDisplay){
        if (imageNumberToDisplay==0){
            dotOne.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else if (imageNumberToDisplay==1){
            dotTwo.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else if (imageNumberToDisplay==2){
            dotThree.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else if (imageNumberToDisplay==3){
            dotFour.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else if (imageNumberToDisplay==4){
            dotFive.setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

    public void clearAllDots(){

        dotOne.setBackgroundColor(Color.parseColor("#616161"));
        dotTwo.setBackgroundColor(Color.parseColor("#616161"));
        dotThree.setBackgroundColor(Color.parseColor("#616161"));
        dotFour.setBackgroundColor(Color.parseColor("#616161"));
        dotFive.setBackgroundColor(Color.parseColor("#616161"));

    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Constants.CURRENT_SCREEN = Constants.SCREEN_OFFER_DETAILS_CHAT_WINDOW;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                   // Toast.makeText(getActivity(), "Left Swipe", Toast.LENGTH_SHORT).show();
                    rightArrowClicked();
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                   // Toast.makeText(getActivity(), "Right Swipe", Toast.LENGTH_SHORT).show();
                    leftArrowClicked();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}