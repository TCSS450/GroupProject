package group3.tcss450.uw.edu.groupappproject.utility;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.preference.PreferenceManager;
import android.widget.CheckBox;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import group3.tcss450.uw.edu.groupappproject.fragments.weather.Weather;
import group3.tcss450.uw.edu.groupappproject.fragments.weather.WeatherDetails;

public class Constants {


    //Add constants used across CODE here, NOT application

    //CONSTANTS
    public static final String PASSWORD_CHANGE_END_POINT_URL = "https://group3-backend.herokuapp.com/password-change";
    public static final String PASSWORD_FORGOT_END_POINT_URL = "https://group3-backend.herokuapp.com/password-forgot";
    public static final String REGISTER_END_POINT_URL = "https://group3-backend.herokuapp.com/register";
    public static final String VERIFY_END_POINT_URL = "https://group3-backend.herokuapp.com/verify";
    public static final String RESEND_END_POINT_URL = "https://group3-backend.herokuapp.com/resend";
    public static final String LOGIN_END_POINT_FIREBASE_URL = "https://group3-backend.herokuapp.com/login/with_token";
    public static final String ADD_FRIEND_URL = "https://group3-backend.herokuapp.com/send-friend-request";
    public static final String RECEIVED_REQUESTS_END_POINT_URL = "https://group3-backend.herokuapp.com/view-request-sent-to-me";
    public static final String SENT_REQUESTS_END_POINT_URL = "https://group3-backend.herokuapp.com/view-request-I-sent";
    public static final String SEARCH_END_POINT_URL = "https://group3-backend.herokuapp.com/search-members";
    public static final String FRIEND_STATUS_URL = "https://group3-backend.herokuapp.com/friend-status";
    public static final String REJECT_REQUEST_URL = "https://group3-backend.herokuapp.com/reject-friend-request";
    public static final String ACCEPT_REQUEST_URL = "https://group3-backend.herokuapp.com/accept-friend-request";
    public static final String VIEW_FRIENDS_URL = "https://group3-backend.herokuapp.com/view-friends";
    public static final String GET_PROFILES_URL = "https://group3-backend.herokuapp.com/get-profiles-by-id";
    public static final String CREATE_CHAT_URL = "https://group3-backend.herokuapp.com/create-chat";
    public static final String DELETE_FRIEND_URL ="https://group3-backend.herokuapp.com/delete-friend";
    public static final String WEATHER_END_POINT ="https://group3-backend.herokuapp.com/weather";
    public static final String GET_ALL_CHATS_END_POINT_URL = "https://group3-backend.herokuapp.com/get-current-chats";
    public static final String WEATHER_DATE_URL = "https://group3-backend.herokuapp.com/weather";
    public static final String WEATHER_HOUR_URL = "https://group3-backend.herokuapp.com/weather/24hour";
    public static final String CHANGE_DISPLAY_TYPE_URL = "https://group3-backend.herokuapp.com/update-display-type";
    public static final String GET_ALL_MESSAGES_URL = "https://group3-backend.herokuapp.com/messaging/getAll";
    public static final String SEND_REFERRAL_URL = "https://group3-backend.herokuapp.com/send-referral";
    public static final String SHARE_PREF_LOCATION = "SP-Location";


    //VARIABLES
    public static DataUtilityControl dataUtilityControl = null;
    public static ArrayList<FriendStatus> searchResults = null;
    public static ArrayList<Credentials> receivedRequests = null;
    public static ArrayList<Credentials> sentRequests = null;
    public static ArrayList<Credentials> temporaryCreds = new ArrayList<>();
    public static ArrayList<Credentials> myFriends = null;
    public static ArrayList<Integer> myChatIds = null;
    public static ArrayList<Credentials[]> myChatMembers = null;
    public static ArrayList<CheckBox> chatCheckBoxes = null;
    public static Location MY_CURRENT_LOCATION = null;
    public static ArrayList<Weather> weatherSearch = null;
    public static ArrayList<WeatherDetails> weatherDetails = null;
    public static boolean myLoadHomeFragChats = true;
    public static List<Address> previousLocation = new ArrayList<>();

    public static SendPostAsyncTask saveAsunc = null;

    public static boolean refreshLocation = false;

}
