package com.scu.tausch.DB;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.scu.tausch.Activities.AddOfferFragment;
import com.scu.tausch.Activities.ChatFragment;
import com.scu.tausch.Activities.DBListener;
import com.scu.tausch.Activities.EditOfferFragment;
import com.scu.tausch.Activities.HomePage;
import com.scu.tausch.Activities.LaunchScreen;
import com.scu.tausch.Activities.MessageThreadListener;
import com.scu.tausch.Activities.MessagesListener;
import com.scu.tausch.Activities.MyMessagesFragment;
import com.scu.tausch.Activities.MyOfferFragment;
import com.scu.tausch.Activities.OffersList;
import com.scu.tausch.Activities.RefreshInterface;
import com.scu.tausch.Activities.SearchListener;
import com.scu.tausch.DTO.LoginDTO;
import com.scu.tausch.DTO.OfferDTO;
import com.scu.tausch.DTO.RegistrationDTO;
import com.scu.tausch.Activities.Login;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.Activities.Registration;
import com.parse.SignUpCallback;
import com.parse.LogInCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Praneet on 1/16/16.
 */
public class DBAccessor {

    private static DBAccessor instance = null;
    private DBListener dbListener;
    private SearchListener searchListener;
    private RefreshInterface refreshInterface;
    private MessagesListener messagesListener;
    private MessageThreadListener messageThreadListener;
    public static int searchCode = Constants.SEARCH_CODE_HOME_PAGE;

    protected DBAccessor() {
        // Exists only to defeat instantiation.
    }

    //singleton class. only one object should be created
    public static DBAccessor getInstance() {
        if(instance == null) {
            instance = new DBAccessor();
        }
        return instance;
    }

    private void setMessageThreadListener(MessageThreadListener messageThreadListener){
        this.messageThreadListener = messageThreadListener;
    }

    //setting the reference for callback.
    private void setDBListener(DBListener dbListener){
        this.dbListener=dbListener;
    }

    private void setMessagesListener(MessagesListener messagesListener){
        this.messagesListener = messagesListener;
    }

    private void setRefreshInterface(RefreshInterface refreshInterface){
        this.refreshInterface = refreshInterface;
    }

    //setting the reference for callback.
    private void setSearchListener(SearchListener searchListener){
        this.searchListener=searchListener;
    }

    //method to see if username is already available during registration
    public void checkIfUsernameExists(final RegistrationDTO regDTO, final Registration callbackReg){

        //Initially logout the current user.
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.logOut();

        ParseUser user = new ParseUser();
        user.setUsername(regDTO.getEmail().toLowerCase());
        user.setPassword(regDTO.getPassword());
        user.setEmail(regDTO.getEmail().toLowerCase());

        // other fields can be set just like with ParseObject
        user.put(Constants.NUMBER, regDTO.getNumber());
        user.put(Constants.FIRST_NAME,regDTO.getFirstName());
        user.put(Constants.LAST_NAME,regDTO.getLastName());

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put(Constants.DB_USERNAME, regDTO.getEmail().toLowerCase());
                    installation.saveInBackground();

                    // Sign up successful.
                    callbackReg.showSignupSuccessfulMessage();
                } else {
                    // Sign up failed.
                    callbackReg.showErrorMessageToUser(""+e);
                }
            }
        });

    }


    public void checkUsernamePasswordValidity(LoginDTO loginDTO, final Login callbackLogin){

        ParseUser.logInInBackground(loginDTO.getEmail().toLowerCase().trim(), loginDTO.getPassword().trim(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("username", ParseUser.getCurrentUser().getEmail());
                    installation.saveInBackground();
                    ParsePush.subscribeInBackground("Tausch");
                    callbackLogin.loginSuccessful();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    callbackLogin.showErrorMessageToUser("" + e);
                }
            }
        });
    }


    public void resetUserPassword(String usernameForPasswordReset, final Login callbackLogin){

        ParseUser.requestPasswordResetInBackground(usernameForPasswordReset,
                new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // An email was successfully sent with reset instructions.
                            callbackLogin.showMessageToCheckEmail();
                        } else {
                            // Something went wrong.
                            callbackLogin.showErrorInResetPassword("" + e);
                        }
                    }
                });
    }


    //Fetch all the items for a particular category from database.
    public void getItemsForCategory(String categoryID, final HomePage homePage){

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.DB_OFFERS);
        query.whereEqualTo(Constants.DB_CATEGORY_ID, categoryID);
        query.whereEqualTo(Constants.DB_STATUS, "true");

        final List<ParseObject> objectOtherThanUser = new ArrayList<>();

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                int counter = 0;
                while (counter < objects.size()){

                    //if (!((objects.get(counter).get("user_id")).equals(ParseUser.getCurrentUser().getObjectId()))){
                        objectOtherThanUser.add(objects.get(counter));
                  //  }

                    counter++;
                }


                //Getting the fragment already created using tag.
                OffersList offerListFragment = (OffersList) homePage.getSupportFragmentManager().findFragmentByTag(Constants.TAG_Offer_List);
                setDBListener(offerListFragment);

                if (dbListener != null) {
                    dbListener.callback(objectOtherThanUser);
                }

            }
        });

    }

    //Fetch all the items posted by user
    public void getItemsPostedByUser(final HomePage homePage){

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.DB_OFFERS);
        query.whereEqualTo(Constants.DB_USER_ID, ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo(Constants.DB_STATUS, "true");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                //Getting the fragment already created using tag.
                MyOfferFragment myOfferFragment = (MyOfferFragment) homePage.getSupportFragmentManager().findFragmentByTag(Constants.TAG_My_Offer_Fragment);
                setDBListener(myOfferFragment);

                if (dbListener != null) {
                    dbListener.callback(objects);
                }

            }
        });

    }

//    //get the name of city from its zipcode.
//    public void getCityForZip(String zip,final HomePage homePage){
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.DB_ZIP_CODE_DATABASE);
//        query.whereEqualTo(Constants.DB_ZIP, zip);
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//
//                //Getting the fragment already created using tag.
//                AddOfferFragment addOfferFragment = (AddOfferFragment) homePage.getSupportFragmentManager().findFragmentByTag(Constants.TAG_Add_Offer_Fragment);
//                setDBListener(addOfferFragment);
//
//                if (dbListener != null) {
//                    dbListener.callback(objects);
//                }
//
//            }
//        });
//
//
//    }

//    //get the name of city from its zipcode.
//    public void getUpdatedCityForZip(String zip,final HomePage homePage){
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.DB_ZIP_CODE_DATABASE);
//        query.whereEqualTo(Constants.DB_ZIP, zip);
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//
//                //Getting the fragment already created using tag.
//                EditOfferFragment editOfferFragment = (EditOfferFragment) homePage.getSupportFragmentManager().findFragmentByTag(Constants.TAG_Edit_Offer_Fragment);
//                setDBListener(editOfferFragment);
//
//                if (dbListener != null) {
//                    dbListener.callback(objects);
//                }
//
//            }
//        });
//
//
//    }

//Check search results for either category or general search.
    public void getSearchResults(String searchStr, final HomePage homePage){
        final String searchStr1 = searchStr;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.DB_OFFERS);
        query.whereEqualTo("status", "true");

       // TODO: Need to add status ="Open" (true) in queries, we need to search only open offers and not closed/deleted

        switch (searchCode){

            case Constants.SEARCH_CODE_AUTOMOBILES:{
                query.whereEqualTo(Constants.DB_CATEGORY_ID,Constants.CATEGORY_AUTOMOBILES_OBJECT_ID);

                break;
            }
            case Constants.SEARCH_CODE_FURNITURE:{

                query.whereEqualTo(Constants.DB_CATEGORY_ID,Constants.CATEGORY_FURNITURE_OBJECT_ID);

                break;
            }
            case Constants.SEARCH_CODE_LAPTOPS:{

                query.whereEqualTo(Constants.DB_CATEGORY_ID,Constants.CATEGORY_LAPTOPS_OBJECT_ID);

                break;
            }
            case Constants.SEARCH_CODE_RENTALS:{

                query.whereEqualTo(Constants.DB_CATEGORY_ID,Constants.CATEGORY_RENTALS_OBJECT_ID);

                break;
            }
            case Constants.SEARCH_CODE_BOOKS:{

                query.whereEqualTo(Constants.DB_CATEGORY_ID,Constants.CATEGORY_BOOKS_OBJECT_ID);

                break;
            }
        }

//If none of the above category is selected, get results of generalized search.
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                setSearchListener(homePage);

                if (searchListener != null) {
                    searchListener.searchResults(objects, searchStr1);
                }

            }
        });

    }

    //Resend verification mail to user if she/he hasn't verfied email address.
public void updateEmailForVerificationAgain(final HomePage homePage){

    ParseUser parseUser = ParseUser.getCurrentUser();
    String email = parseUser.getEmail().toLowerCase();
    parseUser.setEmail("");

    parseUser.saveInBackground();

    parseUser.setEmail(email.toLowerCase());
    parseUser.saveInBackground();
}

    public void deleteOffer(String objectToBeDeleted, final HomePage homePage) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Offers");

// Retrieve the object by id
        query.getInBackground(objectToBeDeleted, new GetCallback<ParseObject>() {
            public void done(ParseObject offer, ParseException e) {
                if (e == null) {

                    MyOfferFragment myOfferFragment = (MyOfferFragment) homePage.getSupportFragmentManager().findFragmentByTag(Constants.TAG_My_Offer_Fragment);
                    setRefreshInterface(myOfferFragment);

                    offer.put("status", "false");
                    offer.saveInBackground();

                    //callback method once, item has been deleted from server.
                    refreshInterface.refreshAfterStatusChangeForDelete();
                }
            }
        });
    }

    public List<ParseObject> sortOffersInCategory(OfferDTO offerDTO){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Offers");
        query.whereEqualTo("category_id", offerDTO.getCategoryId());
        query.whereEqualTo(Constants.DB_STATUS,"true");
        if(offerDTO.getSortCriteriaSelected().equals(Constants.SORT_PRICE_LOW_TO_HIGH)) {
            query.orderByAscending("price");
        } else if(offerDTO.getSortCriteriaSelected().equals(Constants.SORT_PRICE_HIGH_TO_LOW)) {
            query.orderByDescending("price");
        } else if(offerDTO.getSortCriteriaSelected().equals(Constants.SORT_DATE_NEW_TO_OLD)) {
            query.orderByAscending("createdAt");
        } else if(offerDTO.getSortCriteriaSelected().equals(Constants.SORT_DATE_OLD_TO_NEW)) {
            query.orderByDescending("createdAt");
        }
        final List<ParseObject> results = new ArrayList<ParseObject>();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (dbListener != null) {
                    results.addAll(objects);
                    dbListener.callback(objects);
                }
            }
        });
        return results;
    }

    public void messagesBetweenSenderAndReceiver(final String receiverId, final HomePage homePage){

        final ArrayList<ParseObject> messagesAll = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("receiverId", receiverId);

        String te = ParseUser.getCurrentUser().getObjectId();

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                    messagesAll.addAll(objects);

                ParseQuery<ParseObject> queryTwo = ParseQuery.getQuery("Message");
                queryTwo.whereEqualTo("receiverId", ParseUser.getCurrentUser().getObjectId());
                queryTwo.whereEqualTo("userId", receiverId);

                queryTwo.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        messagesAll.addAll(objects);

                       for(int i=0; i<messagesAll.size()-1;i++){

                           int j;

                           for(j=i+1;j<messagesAll.size();j++){


                               if ((messagesAll.get(i).getCreatedAt()).compareTo(messagesAll.get(j).getCreatedAt())==1){

                                   ParseObject temp = messagesAll.get(i);

                                   messagesAll.set(i, messagesAll.get(j));

                                   messagesAll.set(j, temp);

                               }
                       }

                       }

                        //Getting the fragment already created using tag.
                        ChatFragment chatFragment = (ChatFragment) homePage.getSupportFragmentManager().findFragmentByTag(Constants.TAG_Chat_Fragment);
                        setMessagesListener(chatFragment);

                        if (chatFragment != null) {
                            chatFragment.callbackForAllMessages(messagesAll, receiverId);
                        }

                    }
                });

            }
        });


    }

    public void getAllMessageThreadValuesfinal(final HomePage homePage){

        final ArrayList<ParseObject> messagesObjects = new ArrayList<>();
        final ArrayList<String> uniqueIds = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {


                messagesObjects.addAll(objects);


                ParseQuery<ParseObject> queryTwo = ParseQuery.getQuery("Message");
                queryTwo.whereEqualTo("receiverId", ParseUser.getCurrentUser().getObjectId());

                queryTwo.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        messagesObjects.addAll(objects);

                        ArrayList<String> myMessageThreadPeopleNames = new ArrayList<>();

                        int namesCount = 0;

                        while (namesCount < messagesObjects.size()){


                            String receiverId = (String)messagesObjects.get(namesCount).get("receiverId");
                            String senderId = (String)messagesObjects.get(namesCount).get("userId");
                            String userId = ParseUser.getCurrentUser().getObjectId();


                            if (uniqueIds.contains(receiverId)==false){
                                if (!receiverId.equals(userId)){
                                uniqueIds.add(receiverId);
                                myMessageThreadPeopleNames.add((String)messagesObjects.get(namesCount).get("other_person"));
                            }
                            }


                            if (uniqueIds.contains(senderId)==false){
                                if(!senderId.equals(userId)){
                                uniqueIds.add(senderId);
                                    if ((messagesObjects.get(namesCount).get("receiverId")).equals(userId)) {
                                        myMessageThreadPeopleNames.add((String) messagesObjects.get(namesCount).get("sender_person"));

                                    } else {
                                        myMessageThreadPeopleNames.add((String) messagesObjects.get(namesCount).get("other_person"));
                                    }
                            }
                            }

                            namesCount++;
                        }




                        //Getting the fragment already created using tag.
                        MyMessagesFragment myMessagesFragment = (MyMessagesFragment) homePage.getSupportFragmentManager().findFragmentByTag(Constants.Tag_My_Messages_Fragment);
                        setMessageThreadListener(myMessagesFragment);

                        if (myMessagesFragment != null) {
                            myMessagesFragment.callbackForAllMessagesThreads(uniqueIds,myMessageThreadPeopleNames);
                        }

                    }
                });

            }
        });


    }



    public void deleteMessage( final String objectToBeDeleted, final HomePage homePage) {

        final ArrayList<ParseObject> messagesObjects = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("receiverId", objectToBeDeleted);

        // Retrieve the object by id
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                messagesObjects.addAll(objects);

                ParseQuery<ParseObject> queryTwo = ParseQuery.getQuery("Message");
                queryTwo.whereEqualTo("receiverId", ParseUser.getCurrentUser().getObjectId());
                queryTwo.whereEqualTo("userId", objectToBeDeleted);

                queryTwo.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        messagesObjects.addAll(objects);


                        if (e == null) {
                            if (messagesObjects != null && messagesObjects.size() > 0) {
                                for (ParseObject p : messagesObjects)
                                    p.deleteInBackground();

                                MyMessagesFragment myMessagesFragment = (MyMessagesFragment) homePage.getSupportFragmentManager().findFragmentByTag(Constants.Tag_My_Messages_Fragment);
                                setRefreshInterface(myMessagesFragment);

                                //callback method once, item has been deleted from server.
                                refreshInterface.refreshAfterStatusChangeForDelete();
                            }
                        }
                    }
                });
            }
        });

    }

}