package com.scu.tausch.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.scu.tausch.Adapters.ChatListAdapter;
import com.scu.tausch.DB.DBAccessor;
import com.scu.tausch.DTO.Message;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.R;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatFragment extends Fragment implements MessagesListener {

    static final String TAG = ChatFragment.class.getSimpleName();
    static String dealObj = "";

    private ProgressDialog progress;
    private EditText etMessage;
    private Button btSend;
    private View rootView;
    static LinearLayout layout;
    private String receiverEmail;
    private String receiverObjectId;
    private String receiverName;
    private String senderName;
    public static HomePage context;

    ListView lvChat;
    ArrayList<Message> mMessages;
    private ScrollView scrollViewForMessages;
    ChatListAdapter mAdapter;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;


    public ChatFragment() {
        // Required empty public constructor

    }

    public void setArgumentsForMessageSending(String receiverEmail, String receiverObjectId, String receiverName) {
        this.receiverEmail = receiverEmail;
        this.receiverObjectId = receiverObjectId;
        this.receiverName = receiverName;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //   mHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);

        DBAccessor.searchCode = Constants.SEARCH_CODE_HOME_PAGE;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        layout = (LinearLayout) rootView.findViewById(R.id.layoutMessage);


        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                return false;
            }
        });

        loadAndDisplayOldMessages();


        ParseUser myCurrentUser = ParseUser.getCurrentUser();

        // User login
        if (myCurrentUser != null) { // start with existing user
            startWithCurrentUser();
        }

        return rootView;
    }

    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }


    // Setup message field and posting
    void setupMessagePosting() {
        // Find the text field and button


        etMessage = (EditText) rootView.findViewById(R.id.etMessage);
        scrollViewForMessages = (ScrollView) rootView.findViewById(R.id.scrollLayout);
        //  scrollViewForMessages.scrollTo(0,scrollViewForMessages.getBottom());


        //When the keypad opens, message send view would be pushed on top of keypad.
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        btSend = (Button) rootView.findViewById(R.id.btSend);
        //  lvChat = (ListView) rootView.findViewById(R.id.lvChat);
        mMessages = new ArrayList<>();
        // Automatically scroll to the bottom when a data set change notification is received and only if the last item is already visible on screen. Don't scroll to the bottom otherwise.
        //  lvChat.setTranscriptMode(1);
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        // mAdapter = new ChatListAdapter(getActivity(), userId, mMessages);
        //  lvChat.setAdapter(mAdapter);
        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.fetchInBackground(new GetCallback<ParseObject>() {
                                                  @Override
                                                  public void done(ParseObject object, ParseException e) {

                                                      if (etMessage.getText().toString().trim().length()==0){
                                                          return;
                                                      }

                                                      if (e == null) {
                                                          boolean isUserVerified = object.getBoolean("emailVerified");
                                                          Log.i("test", "The Value is :" + isUserVerified);

                                                          if (!isUserVerified) {
                                                              showDialogBoxForUnverfiedUser();
                                                              return;
                                                          } else {
                                                              //user is verified.
                                                              ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                                                              query.whereEqualTo("username", ParseUser.getCurrentUser().getEmail());
                                                              query.findInBackground(new FindCallback<ParseObject>()

                                                                                     {
                                                                                         public void done(List<ParseObject> objects, ParseException e) {
                                                                                             if (e == null) {
                                                                                                 //  row of Object Id "U8mCwTHOaC"

                                                                                                 for (final ParseObject dealsObject : objects) {
                                                                                                     // use dealsObject.get('columnName') to access the properties of the Deals object.
                                                                                                     dealObj = dealsObject.getObjectId();

                                                                                                     final String data = etMessage.getText().toString();

                                                                                                     if (etMessage.getText().toString().trim().length() == 0) {
                                                                                                         return;
                                                                                                     }

                                                                                                     ParseObject message = ParseObject.create("Message");
                                                                                                     //  message.put(Message.USER_ID_KEY, userId);

                                                                                                     //object id for Adhuri@scu.edu
                                                                                                     message.put(Message.USER_ID_KEY, userId);

                                                                                                     message.put(Message.BODY_KEY, data);

                                                                                                     message.put(Message.OTHER_PERSON_NAME, receiverName);

                                                                                                     String nam = (String) ParseUser.getCurrentUser().get("firstname");

                                                                                                     message.put(Message.SENDER_PERSON_NAME, nam);

                                                                                                     //currently its for pjain3@scu.edu, it should be receiver Obj
                                                                                                     message.put(Message.RECEIVER_ID_KEY, receiverObjectId);
                                                                                                     message.saveInBackground(new SaveCallback() {
                                                                                                         @Override
                                                                                                         public void done(com.parse.ParseException e) {
                                                                                                             Toast.makeText(getActivity(), "Message Sent",
                                                                                                                     Toast.LENGTH_SHORT).show();


                                                                                                             //    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                                                                             //   params.setMargins(0, 0, 0, 20);


                                                                                                             DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
                                                                                                             float dpWidth = displayMetrics.widthPixels;
                                                                                                             //  float dpHeight = displayMetrics.heightPixels;

                                                                                                             LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                                                                             TextView textView = (TextView) inflater.inflate(R.layout.textview_bubble, null);
                                                                                                             textView.setText(data);
                                                                                                             textView.setTextColor(Color.WHITE);
                                                                                                             textView.setWidth((int) dpWidth);
                                                                                                             //    textView.setHeight((int) dpHeight);
                                                                                                             textView.setBackgroundColor(Color.parseColor("#808080"));
                                                                                                             //   textView.setLayoutParams(params);
                                                                                                             LinearLayout layout = ChatFragment.layout;

                                                                                                             layout.addView(textView);



                                                                                                             TextView textViewTS = (TextView) inflater.inflate(R.layout.textview_bubble_timestamp, null);
                                                                                                             Date d  = new Date();
                                                                                                             textViewTS.setText(d.toString());
                                                                                                             textViewTS.setTextColor(Color.WHITE);
                                                                                                             textViewTS.setWidth((int) dpWidth);
                                                                                                             textViewTS.setBackgroundColor(Color.parseColor("#808080"));
                                                                                                             //textViewTS.setLayoutParams(params);
                                                                                                             layout.addView(textViewTS);

                                                                                                             JSONObject jsondata = null;



                                                                                                             SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                                                                                                             Constants.PUSH_RECEIVED = sharedpreferences.getBoolean("push_received", false);
                                                                                                             Constants.lastSenderEmail = sharedpreferences.getString("last_sender_email", "");
                                                                                                             Constants.lastReceiverEmail = sharedpreferences.getString("last_receiver_email", "");
                                                                                                             String currentReceiver="";


                                                                                                             if(Constants.PUSH_RECEIVED){

                                                                                                                 if(currentUser.getEmail().equals(Constants.lastReceiverEmail)){

                                                                                                                     currentReceiver = Constants.lastSenderEmail;

                                                                                                                     try {
                                                                                                                         jsondata = new JSONObject("{\"title\" : \"Tausch\"," +
                                                                                                                                 "\"receiver_email\" : \"" + Constants.lastSenderEmail + "\"," +
                                                                                                                                 "\"sender_email\" : \"" + currentUser.getEmail() + "\"," +
                                                                                                                                 "\"alert\" : \"" + data + "\"," +
                                                                                                                                 "\"receiver_object_id\" : \"" + receiverObjectId + "\"," +
                                                                                                                                 "\"sender_object_id\" : \"" + currentUser.getObjectId() + "\"," +
                                                                                                                                 "\"receiver_name\" :\"" + receiverName + "\"}");
                                                                                                                     } catch (JSONException he) {
                                                                                                                         he.printStackTrace();
                                                                                                                     }

                                                                                                                 }
                                                                                                                 else{
                                                                                                                     currentReceiver = Constants.lastReceiverEmail;

                                                                                                                     try {
                                                                                                                         jsondata = new JSONObject("{\"title\" : \"Tausch\"," +
                                                                                                                                 "\"receiver_email\" : \"" + Constants.lastReceiverEmail + "\"," +
                                                                                                                                 "\"sender_email\" : \"" + currentUser.getEmail() + "\"," +
                                                                                                                                 "\"alert\" : \"" + data + "\"," +
                                                                                                                                 "\"receiver_object_id\" : \"" + receiverObjectId + "\"," +
                                                                                                                                 "\"sender_object_id\" : \"" + currentUser.getObjectId() + "\"," +
                                                                                                                                 "\"receiver_name\" :\"" + receiverName + "\"}");
                                                                                                                     } catch (JSONException he) {
                                                                                                                         he.printStackTrace();
                                                                                                                     }

                                                                                                                 }


                                                                                                             }

                                                                                                            else {

                                                                                                                 try {
                                                                                                                     jsondata = new JSONObject("{\"title\" : \"Tausch\"," +
                                                                                                                             "\"receiver_email\" : \"" + receiverEmail + "\"," +
                                                                                                                             "\"sender_email\" : \"" + currentUser.getEmail() + "\"," +
                                                                                                                             "\"alert\" : \"" + data + "\"," +
                                                                                                                             "\"receiver_object_id\" : \"" + receiverObjectId + "\"," +
                                                                                                                             "\"sender_object_id\" : \"" + currentUser.getObjectId() + "\"," +
                                                                                                                             "\"receiver_name\" :\"" + receiverName + "\"}");
                                                                                                                 } catch (JSONException he) {
                                                                                                                     he.printStackTrace();
                                                                                                                 }
                                                                                                             }


                                                                                                             ParsePush parsePush = new ParsePush();
                                                                                                             ParseQuery pQuery = ParseInstallation.getQuery(); // <-- Installation query

                                                                                                          if (Constants.PUSH_RECEIVED){
                                                                                                              pQuery.whereEqualTo("username", currentReceiver); // <-- you'll probably want to target someone that's not the current user, so modify accordingly

                                                                                                          }
                                                                                                          else {

                                                                                                              pQuery.whereEqualTo("username", receiverEmail); // <-- you'll probably want to target someone that's not the current user, so modify accordingly
                                                                                                              //  parsePush.sendMessageInBackground(data, pQuery);
                                                                                                          }
                                                                                                             parsePush.setData(jsondata);
                                                                                                             parsePush.setQuery(pQuery);
                                                                                                             parsePush.sendInBackground();

                                                                                                             //  refreshMessages();
                                                                                                         }
                                                                                                     });
                                                                                                     etMessage.setText(null);

                                                                                                 }
                                                                                             } else {
                                                                                                 // error
                                                                                             }
                                                                                         }
                                                                                     }

                                                              );

                                                          }
                                                      }


                                                  }
                                              }

                );



            }
        });
    }

    public void loadAndDisplayOldMessages() {

        progress = new ProgressDialog(getActivity());
        progress.setMessage("Verifying...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        DBAccessor.getInstance().messagesBetweenSenderAndReceiver(receiverObjectId, context);


    }

    @Override
    public void callbackForAllMessages(List<ParseObject> messagesAll, String receiverId) {

        String data = null;
        String msgTimestamp = null;

        List<ParseObject> complete = new ArrayList<>();

        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        LinearLayout layout = ChatFragment.layout;


        int messageNumber = 0;

        //    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //   params.setMargins(0, 0, 0, 20);

        while (messageNumber < messagesAll.size()) {

            data = (String) messagesAll.get(messageNumber).get("body");
            msgTimestamp = String.valueOf(messagesAll.get(messageNumber).getCreatedAt());

            if ((messagesAll.get(messageNumber).get("userId")).equals(ParseUser.getCurrentUser().getObjectId())) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                TextView textView = (TextView) inflater.inflate(R.layout.textview_bubble, null);
                textView.setText(data);
                textView.setTextColor(Color.WHITE);
                textView.setWidth((int) dpWidth);
                textView.setBackgroundColor(Color.parseColor("#808080"));
                layout.addView(textView);

                TextView textViewTS = (TextView) inflater.inflate(R.layout.textview_bubble_timestamp, null);
                textViewTS.setText(msgTimestamp);
                textViewTS.setTextColor(Color.WHITE);
                textViewTS.setWidth((int) dpWidth);
                textViewTS.setBackgroundColor(Color.parseColor("#808080"));
                //     textViewTS.setLayoutParams(params);
                layout.addView(textViewTS);
            }
            if ((messagesAll.get(messageNumber).get("receiverId")).equals(ParseUser.getCurrentUser().getObjectId())) {

                LayoutInflater inflaterTwo = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                TextView textViewTwo = (TextView) inflaterTwo.inflate(R.layout.textview_bubble, null);
                textViewTwo.setText(data);
                textViewTwo.setTextColor(Color.WHITE);
                textViewTwo.setWidth((int) dpWidth);
                textViewTwo.setBackgroundColor(Color.parseColor("#4edacf"));
                layout.addView(textViewTwo);

                TextView textViewTwoTS = (TextView) inflaterTwo.inflate(R.layout.textview_bubble_timestamp, null);
                textViewTwoTS.setText(msgTimestamp);
                textViewTwoTS.setTextColor(Color.WHITE);
                textViewTwoTS.setWidth((int) dpWidth);
                textViewTwoTS.setBackgroundColor(Color.parseColor("#4edacf"));
                //    textViewTwoTS.setLayoutParams(params);
                layout.addView(textViewTwoTS);
            }
            messageNumber++;
        }
        progress.dismiss();

    }

    private void showDialogBoxForUnverfiedUser() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("You must verify your Email!");

        alertDialogBuilder.setPositiveButton("Resend Email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                DBAccessor.getInstance().updateEmailForVerificationAgain(context);

            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Constants.CURRENT_SCREEN = Constants.SCREEN_MY_MESSAGES_CHAT;
    }
}