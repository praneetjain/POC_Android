package com.scu.tausch.Misc;

import com.scu.tausch.Activities.HomePage;

/**
 * Created by Praneet on 1/20/16.
 */
public class Constants {

    public static final int SCREEN_HOME_PAGE = 0;
    public static final int SCREEN_OFFERS_LIST = 1;
    public static final int SCREEN_FILTER_PAGE = 2;
    public static final int SCREEN_OFFER_POST_1 = 4;
    public static final int SCREEN_OFFER_POST_2 = 5;
    public static final int SCREEN_MY_OFFERS_LIST = 6;
    public static final int SCREEN_EDIT_OFFERS_1 = 7;
    public static final int SCREEN_EDIT_OFFERS_2 = 8;
    public static final int SCREEN_MY_MESSAGES_LIST = 9;
    public static final int SCREEN_MY_MESSAGES_CHAT = 10;
    public static final int SCREEN_OFFER_DETAILS_CHAT_WINDOW = 11;
    public static final int SCREEN_SETTINGS = 12;
    public static final int SCREEN_ABOUT = 13;
    public static final int SCREEN_HELP = 14;
    public static HomePage homePage;
    public static String lastReceiverName;
    public static String lastReceiverObjectId;
    public static String lastReceiverEmail;
    public static String lastSenderEmail;
    public static boolean PUSH_RECEIVED;

    public static int CURRENT_SCREEN = SCREEN_HOME_PAGE;
    public static boolean WAS_LAST_SCREEN_ITEM_DESCRIPTION = false;


    public static final int LAUNCH_SCREEN_TIMEOUT = 1000;
    public static final int DELETE_ITEM_TIME_TO_REFRESH = 2000;
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String NUMBER = "number";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String CONFIRM_PASSWORD = "confirmpassword";
    public static final String EMAIL_DOMAIN_SCU = "scu.edu";
    public static final String HANDLE_USERNAME = "";
    public static final String HANDLE_PASSWORD = "";
    public static final String USER_PREFS_NAME = "TauschPrefs";
    public static final int CATEGORY_AUTOMOBILES = 0;
    public static final int CATEGORY_BOOKS = 1;
    public static final int CATEGORY_LAPTOPS =2;
    public static final int CATEGORY_FURNITURE = 3;
    public static final int CATEGORY_RENTALS = 4;
    public static final String CATEGORY_AUTOMOBILES_OBJECT_ID="qcTS2jc5Di";
    public static final String CATEGORY_BOOKS_OBJECT_ID="PupH4JYB6T";
    public static final String CATEGORY_LAPTOPS_OBJECT_ID="baaf0IRDvl";
    public static final String CATEGORY_RENTALS_OBJECT_ID="hPCobHXvGv";
    public static final String CATEGORY_FURNITURE_OBJECT_ID="NR5bYNsrgH";
    public static final int SEARCH_CODE_HOME_PAGE = 0;
    public static final int SEARCH_CODE_AUTOMOBILES = 1;
    public static final int SEARCH_CODE_BOOKS = 2;
    public static final int SEARCH_CODE_LAPTOPS = 3;
    public static final int SEARCH_CODE_FURNITURE = 4;
    public static final int SEARCH_CODE_RENTALS = 5;


    public static final String TAG_Image_Edit = "tagEditImage";
    public static final String TAG_Edit_Offer_Fragment = "tagEditOffer";
    public static final String TAG_Image_Add = "tagImageAdd";
    public static final String TAG_Offer_List = "tagOfferList";
    public static final String TAG_Add_Offer_Fragment = "tagAddOfferFragment";
    public static final String TAG_My_Offer_Fragment = "tagMyOfferFragment";
    public static final String TAG_Chat_Fragment = "tagChatFragment";
    public static final String Tag_My_Messages_Fragment = "tagMyMessageFragment";
    public static final String TAG_Home_Fragment = "tagHomeFragment";
    public static final String TAG_Item_Details_Fragment = "tagItemDetailsFragment";


    public static final String DB_Categories = "Categories";
    public static final String DB_Offers = "Offers";
    public static final String DB_Offer_Title = "offer_title";
    public static final String DB_Price = "price";
    public static final String DB_Image_ONE = "image_one";
    public static final String DB_Image_TWO = "image_two";
    public static final String DB_Image_THREE = "image_three";
    public static final String DB_Image_FOUR = "image_four";
    public static final String DB_Image_FIVE = "image_five";

    public static final String HOMEPAGE_HOME = "Home";
    public static final String HOMEPAGE_MY_OFFERS = "My Offers";
    public static final String HOMEPAGE_MY_MESSAGES = "My Messages";
    public static final String HOMEPAGE_SETTINGS = "Settings";
    public static final String HOMEPAGE_HELP = "Help";
    public static final String HOMEPAGE_ABOUT = "About";
    public static final String HOMEPAGE_SIGNOUT = "Sign out";
    public static final String DB_STATUS = "status";

    public static final String DB_USER_ID = "user_id";
    public static final String Array_Category_Automobiles = "Automobiles";
    public static final String Array_Category_Books = "Books";
    public static final String Array_Category_Laptops = "Laptops";
    public static final String Array_Category_Furniture = "Furniture";
    public static final String Array_Category_Rentals = "Rentals";
    public static int CURRENT_SELECTED_CATEGORY = CATEGORY_AUTOMOBILES;

    public static final String ITEM_TYPE_NEW = "New";
    public static final String ITEM_TYPE_USED = "Used";
    public static final String ITEM_TYPE_All = "All";
    public static final String DB_PRIMARY_CITY = "primary_city";
    public static final String DB_USERNAME = "username";
    public static final String DB_OFFERS = "Offers";
    public static final String DB_CATEGORY_ID = "category_id";
    public static final String DB_ZIP_CODE_DATABASE = "zip_code_database";
    public static final String DB_ZIP = "zip";
    public static final String DB_OFFER_DESCRIPTION = "offer_description";
    public static final String DB_OFFEROR = "offeror";
    public static final String DB_CONDITION = "condition";
    public static final String DB_CITY = "city";
    public static final String DB_CREATEDAT = "createdAt";

    public static final String USER_ID_KEY = "userId";
    public static final String BODY_KEY = "body";
    public static String RECEIVER_ID_KEY = "receiverId";
    public static final String SORT_CRITERIA_DEFAULT="Sort";
    public static final String SORT_PRICE_LOW_TO_HIGH="Price: Low to High";
    public static final String SORT_PRICE_HIGH_TO_LOW="Price: High to Low";
    public static final String SORT_DATE_OLD_TO_NEW="Newest Items";
    public static final String SORT_DATE_NEW_TO_OLD="Oldest Items";


}
