package group3.tcss450.uw.edu.groupappproject.utility;

import android.widget.CheckBox;

import java.util.ArrayList;

public class Constants {


    //Add constants used across CODE here, NOT application



    //CONSTANTS
    public static final String PASSWORD_CHANGE_END_POINT_URL = "https://group3-backend.herokuapp.com/password-change";
    public static final String PASSWORD_FORGOT_END_POINT_URL = "https://group3-backend.herokuapp.com/password-forgot";
    public static final String LOGIN_END_POINT_URL = "https://group3-backend.herokuapp.com/login";
    public static final String REGISTER_END_POINT_URL = "https://group3-backend.herokuapp.com/register";
    public static final String VERIFY_END_POINT_URL = "https://group3-backend.herokuapp.com/verify";
    public static final String RESEND_END_POINT_URL = "https://group3-backend.herokuapp.com/resend";
    public static final String LOGIN_END_POINT_FIREBASE_URL = "https://group3-backend.herokuapp.com/login/with_token";
    public static final String BASE_END_POINT_URL = "group3-backend.herokuapp.com";
    public static final String ADD_FRIEND_URL = "https://group3-backend.herokuapp.com/send-friend-request";
    public static final String RECEIVED_REQUESTS_END_POINT_URL = "https://group3-backend.herokuapp.com/view-request-sent-to-me";
    public static final String SENT_REQUESTS_END_POINT_URL = "https://group3-backend.herokuapp.com/view-request-I-sent";
    public static final String SEARCH_END_POINT_URL = "https://group3-backend.herokuapp.com/search-members";
    public static final String FRIEND_STATUS_URL = "https://group3-backend.herokuapp.com/friend-status";
    public static final String REJECT_REQUEST_URL = "https://group3-backend.herokuapp.com/reject-friend-request";
    public static final String ACCEPT_REQUEST_URL = "https://group3-backend.herokuapp.com/accept-friend-request";
    public static final String VIEW_FRIENDS_URL = "https://group3-backend.herokuapp.com/view-friends";
    public static final String CREATE_CHAT_URL = "https://group3-backend.herokuapp.com/create-chat";

    //VARIABLES
    public static DataUtilityControl dataUtilityControl = null;
    public static ArrayList<FriendStatus> searchResults = null;
    public static ArrayList<Credentials> receivedRequests = null;
    public static ArrayList<Credentials> sentRequests = null;
    public static ArrayList<Credentials> temporaryCreds = new ArrayList<>();
    public static ArrayList<Credentials> myFriends = null;
    public static ArrayList<CheckBox> chatCheckBoxes = null;

}
