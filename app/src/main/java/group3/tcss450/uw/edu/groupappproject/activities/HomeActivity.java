package group3.tcss450.uw.edu.groupappproject.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.AddFriend.AddUserFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ChangePasswordFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.Chats.MyChatsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.chat.ChatFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.AddFriend.FriendsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.Chats.MyChats_Main;
import group3.tcss450.uw.edu.groupappproject.fragments.chat.MessagesListFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.homeview.BestFriendsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.homeview.HomeViewFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.SettingsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriendRequests.FriendRequests;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriendRequests.FriendRequestsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriendRequests.SentFriendRequestsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends.ViewFriends;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends.ViewFriends_Main;
import group3.tcss450.uw.edu.groupappproject.fragments.WaitFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.weather.MainWeatherFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.weather.Weather;
import group3.tcss450.uw.edu.groupappproject.fragments.weather.WeatherDetailListFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.weather.WeatherDetails;
import group3.tcss450.uw.edu.groupappproject.fragments.weather.WeatherFragment;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

import static java.lang.Integer.parseInt;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FriendsFragment.OnListFragmentInteractionListener,
        WaitFragment.OnWaitFragmentInteractionListener,
        SentFriendRequestsFragment.OnListFragmentInteractionListener,
        ViewFriends.OnListFragmentInteractionListener,
        FriendRequestsFragment.OnListFragmentInteractionListener,
        ViewFriends_Main.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        ChangePasswordFragment.OnChangePasswordFragmentInteractionListener,
        BestFriendsFragment.OnBestFriendInteractionListener,
        HomeViewFragment.OnHomeViewFragmentListener,
        WeatherFragment.OnWeatherListFragmentInteractionListener,
        MyChatsFragment.OnListFragmentInteractionListener,
        ChatFragment.OnChatFragmentListener
{
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int MY_PERMISSIONS_LOCATIONS = 8414;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private DataUtilityControl duc;
    private Credentials[] mTempFriendCredentials;
    private boolean myIsGotChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.duc = Constants.dataUtilityControl;
        super.onCreate(savedInstanceState);
        myIsGotChats = false;
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (duc.getBooleanId()) {
            ChatFragment frag = new ChatFragment();
            Bundle args = new Bundle();
            args.putInt("chatId", duc.getChAtId());
            frag.setArguments(args);
            System.out.println("first id gained is " + duc.getChAtId());
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.homeActivityFrame, frag)
                    .addToBackStack(null);
            transaction.commit();
        } else if (duc.getFriendRequest() == 33) {
            FriendRequests frag = new FriendRequests();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.homeActivityFrame, frag)
                    .addToBackStack(null);
            transaction.commit();
        } else if (duc.getFriendAccept() == 33){
            ViewFriends_Main frag = new ViewFriends_Main();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.homeActivityFrame, frag)
                    .addToBackStack(null);
            transaction.commit();
        } else{
            loadFragment(new HomeViewFragment());
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textView = headerView.findViewById(R.id.textView_header_user);
        String s;
        if (duc.getUserCreds().getDisplayPref() == 1) {
            s = duc.getUserCreds().getNickName() + " " + getString(R.string.nav_header_subtitle);
            textView.setText(s);
        } else if (duc.getUserCreds().getDisplayPref() == 2) {
            s = duc.getUserCreds().getFirstName() + " " + duc.getUserCreds().getLastName() + " " + getString(R.string.nav_header_subtitle);
            textView.setText(s);
        } else {
            s = duc.getUserCreds().getEmail() + " " + getString(R.string.nav_header_subtitle);
            textView.setText(s);
        }
        navigationView.setNavigationItemSelectedListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        } else {
            //The user has already allowed the use of Locations. Get the current location.
            requestLocation();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Log.d("MenuOptionsActivity", "settings button clicked");
            loadFragment(SettingsFragment.newInstance(this.duc.getUserCreds()));
            return true;
        }
        if (id == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addFriend) {
            loadFragment(new AddUserFragment());
        } else if (id == R.id.viewChats) {
            getMyChats();
        } else if (id == R.id.connections) {
            loadFragment(new ViewFriends_Main());
        } else if (id == R.id.requests) {
            loadFragment(new FriendRequests());
        } else if (id == R.id.weather) {

            JSONObject msg = new JSONObject();
            //requestLocation();

            try {
                msg.put("lat", Constants.MY_CURRENT_LOCATION.getLatitude());
                msg.put("lon", Constants.MY_CURRENT_LOCATION.getLongitude());
                msg.put("days", 10);
            }catch (JSONException e) {
                Log.wtf("CREDENTIALS", "Error: " + e.getMessage());
            }

            new SendPostAsyncTask.Builder(this.duc.getWeatherDateURI().toString(), msg)
                    .onPostExecute(this::handleOnPostWeatherDate)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();


        } else if (id == R.id.home) {

            loadFragment(new HomeViewFragment());
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleOnPostWeatherDate(String result) {

        try {
            JSONObject resultsJSON = new JSONObject(result);


            boolean success = resultsJSON.getBoolean("success");
            if (success) { // success

                JSONArray weatherArray = resultsJSON.getJSONArray("weather");

                ArrayList<Weather> weatherArrayList = new ArrayList<>();

                for (int i = 0 ;i < weatherArray.length(); i++ ) {
                    JSONObject weather = weatherArray.getJSONObject(i);

                    String date = weather.getString("valid_date");
                    String temp = weather.getString("temp");
                    String wind = weather.getString("wind_spd");
                    String humidity = weather.getString("rh");
                    String pressure = weather.getString("pres");
                    humidity = humidity + "%";
                    temp = temp + "°F";
                    wind = wind + "m/s";

                    JSONObject innerWeather = weather.getJSONObject("weather");

                    String description = innerWeather.getString("description");

                    String icon = innerWeather.getString("icon");



                    Weather weatherObject = new Weather(temp,date, description, wind, pressure, humidity, icon);

                    weatherArrayList.add(weatherObject);

                }
                Constants.weatherSearch = weatherArrayList;
                loadFragment(new MainWeatherFragment());

            }  else {
                this.duc.makeToast(getApplicationContext(), "END_POINT_ERROR");
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());

        }
    }

    private void loadFragment(Fragment frag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeActivityFrame, frag)
                .addToBackStack(null).commit();
    }
    private void logout() {

        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //remove the saved credentials from StoredPrefs
        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
        //close the app
        new DeleteTokenAsyncTask().execute();

        finishAndRemoveTask();
        //or close this activity and bring back the Login
        //Intent i = new Intent(this, MainActivity.class);
        //startActivity(i);
        //End this Activity and remove it from the Activity back stack.
        //finish();
    }

    //@Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.homeActivityFrame, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }

    @Override
    public void onHomeViewWaitShow(int container) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(container, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFriendListFragmentInteraction(Credentials credentials) {
        mTempFriendCredentials = new Credentials[1];
        mTempFriendCredentials[0] = credentials;
        JSONObject msg = new JSONObject();
        Uri createChatURI = this.duc.getCreateChatURI();
        int[] members = {duc.getUserCreds().getMemberId(),
                           credentials.getMemberId()};


        try {
            msg.put("chatmembers", new JSONArray(members));
            msg.put("chatname", "Friends Chat");
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }
        new SendPostAsyncTask.Builder(createChatURI.toString(), msg)
                .onPostExecute(this::handleCreateChatOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    @Override
    public void onListFragmentInteraction(Credentials item) {

    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    @Override
    public void onAcceptListFragmentInteraction(JSONObject msg) {

    }

    @Override
    public void onDenyListFragmentInteraction(JSONObject msg) {

    }

    @Override
    public void onStartChatFragmentInteraction(int chatId, Credentials[] creds) {
        ChatFragment frag = new ChatFragment();
        StringBuilder sb = new StringBuilder();
        // First "fencepost"
        if (creds[0].getMemberId() != duc.getUserCreds().getMemberId()) {
            // First post.
            if (creds[0].getDisplayPref() == 1) {
                sb.append(creds[0].getNickName());
            } else if (creds[0].getDisplayPref() == 2) {
                sb.append(creds[0].getFirstName() + " " + creds[0].getLastName());
            } else {
                sb.append(creds[0].getEmail());
            }
            // Do rest.
            for (int i = 1; i < creds.length; i++) {
                if (creds[i].getMemberId() != duc.getUserCreds().getMemberId()) {
                    if (creds[i].getDisplayPref() == 1) {
                        sb.append(", " + creds[i].getNickName());
                    } else if (creds[i].getDisplayPref() == 2) {
                        sb.append(", " + creds[i].getFirstName() + " " + creds[i].getLastName());
                    } else {
                        sb.append(", " + creds[i].getEmail());
                    }
                }
            }
        } else {
        // If I am at start of list, skip me, first post.
            if (creds[1] != null) {
                if (creds[1].getDisplayPref() == 1) {
                    sb.append(creds[1].getNickName());
                } else if (creds[1].getDisplayPref() == 2) {
                    sb.append(creds[1].getFirstName() + " " + creds[1].getLastName());
                } else {
                    sb.append(creds[1].getEmail());
                }
            }
            // rest of post.
            for (int i = 2; i < creds.length; i++) {
                if (creds[i].getMemberId() != duc.getUserCreds().getMemberId()) {
                    if (creds[i].getDisplayPref() == 1) {
                        sb.append(", " + creds[i].getNickName());
                    } else if (creds[i].getDisplayPref() == 2) {
                        sb.append(", " + creds[i].getFirstName() + " " + creds[i].getLastName());
                    } else {
                        sb.append(", " + creds[i].getEmail());
                    }
                }
            }
        }
        String chatMemberCreds = sb.toString();
        Bundle args = new Bundle();
        args.putInt("chatId", chatId);
        args.putString("chatName", chatMemberCreds);
        args.putSerializable("members", creds);
        frag.setArguments(args);
        System.out.println("first id gained is " + chatId);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeActivityFrame, frag)
                .addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onChangePasswordSubmit() {
        //log the user out to home screen
        Log.d("PreLoginActivity", "successful password change");
        loadFragment(duc.getLoginFragment());
    }




    @Override
    public void onWeatherListFragmentInteraction(Weather weather) {
        JSONObject msg = new JSONObject();

        try {
            msg.put("lat", Constants.MY_CURRENT_LOCATION.getLatitude());
            msg.put("lon", Constants.MY_CURRENT_LOCATION.getLongitude());
        }catch (JSONException e) {
            Log.wtf("WEATHER", "Error: " + e.getMessage());
        }

        new SendPostAsyncTask.Builder(this.duc.getWeatherHourURI().toString(), msg)
                .onPostExecute(this::handleONPostWeatherHour)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();



    }

    private void handleONPostWeatherHour(String result) {
        System.out.println("SUCCES WEATHER HOUR " + result);
        onWaitFragmentInteractionHide();

        try {
            JSONObject resultsJSON = new JSONObject(result);


            boolean success = resultsJSON.getBoolean("success");
            if (success) { // success

                JSONArray weatherArray = resultsJSON.getJSONArray("weather");

                ArrayList<WeatherDetails> weatherDetailsArrayList = new ArrayList<>();

                for (int i = 0 ;i < weatherArray.length(); i++ ) {
                    JSONObject weather = weatherArray.getJSONObject(i);

                    String time = weather.getString("timestamp_local");
                    String temp = weather.getString("temp");


                    JSONObject innerWeatherDetails = weather.getJSONObject("weather");

                    String description = innerWeatherDetails.getString("description");

                    String icon = innerWeatherDetails.getString("icon");



                    WeatherDetails weatherDetailsObject = new WeatherDetails(temp,time, description, icon);

                    weatherDetailsArrayList.add(weatherDetailsObject);

                }
                Constants.weatherDetails = weatherDetailsArrayList;

                loadFragment(new WeatherDetailListFragment());

            }  else {
                this.duc.makeToast(getApplicationContext(), "END_POINT_ERROR");
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());

        }



    }

    /**
     * not used here but used in MessagesListFragment in inner firebase class
     */
    @Override
    public void onSendMessage() {
        //Todo: not currently working !!!!!
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.chatFragment_id);
        if(f instanceof ChatFragment) {
            Fragment cf = (MessagesListFragment) f.getChildFragmentManager()
                    .findFragmentById(R.id.chatFrag_message_recycler);
            ((MessagesListFragment) cf).onSendMessage();
        }
    }

    // Deleting the InstanceId (Firebase token) must be done asynchronously. Good thing
    // we have something that allows us to do that.
    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onWaitFragmentInteractionShow();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
            prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
            try {
                //this call must be done asynchronously.
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                Log.e("FCM", "Delete error!");
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //close the app
            finishAndRemoveTask();
            //or close this activity and bring back the Login
            // Intent i = new Intent(this, MainActivity.class);
            // startActivity(i);
            // //Ends this Activity and removes it from the Activity back stack.
            // finish();
        }
    }

    private void handleCreateChatOnPost(String result) {
        /*  1 - Success! ChatId is created.
            2 - Error
        */
        //System.out.println("check notify is  third " + checkNotify);
        try {
            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            ArrayList<Credentials> searchResult = new ArrayList<>();
            int status = resultsJSON.getInt("status");
            if (status == 1) {
                int chatId = resultsJSON.getInt("chatid");
                onStartChatFragmentInteraction(chatId, mTempFriendCredentials);
            } else {
                duc.makeToast(this, getString(R.string.request_error));
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
            duc.makeToast(this, getString(R.string.request_error));
        }
    }

    private void getMyChats() {
        Uri getChatsURI = duc.getCurrentChatsURI();
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid", duc.getUserCreds().getMemberId());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }
        new SendPostAsyncTask.Builder(getChatsURI.toString(), msg)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleGetChatsOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();

    }

    private void handleGetChatsOnPost(String result) {
        /*  1 - Success! chats fragment is created.
            2 - Error
        */
        try {
            JSONObject root = new JSONObject(result);
            if (root.has("status")) {
                System.out.println("STATUS IN");
                int status = root.getInt("status");
                if (status == 1) {
                    System.out.println("STATUS == 1");
                    JSONArray chatDetails = root.getJSONArray("chatDetails");
                    ArrayList<Integer> chatIds = new ArrayList<>();
                    ArrayList<Credentials[]> allChats = new ArrayList<>();
                    for (int i = 0; i < chatDetails.length(); i++) {
                        System.out.println("IN FIRST LOOP");
                        JSONObject jsonChat = chatDetails.getJSONObject(i);
                        chatIds.add(jsonChat.getInt("chatid"));
                        System.out.println("CHAT ID" + jsonChat.getInt("chatid"));
                        JSONArray jsonMembers = jsonChat.getJSONArray("memberProfiles");
                        List<Credentials> chatMembers = new ArrayList<>();
                        for (int j = 0; j < jsonMembers.length(); j++) {
                            System.out.println("IN SECOND LOOP");
                            JSONObject member = jsonMembers.getJSONObject(j);
                            System.out.println("member is created");
                            chatMembers.add(new Credentials.Builder(member.getString("email"), "")
                                    .addDisplayPref(member.getInt("display_type"))
                                    .addFirstName(member.getString("firstname"))
                                    .addLastName(member.getString("lastname"))
                                    .addNickName(member.getString("nickname"))
                                    .addMemberId(member.getInt("memberid"))
                                    .addPhoneNumber(member.getString("phone_number"))
                                    .build());
                        }
                        Credentials[] tempCreds = new Credentials[chatMembers.size()];
                        tempCreds = chatMembers.toArray(tempCreds);
                        allChats.add(tempCreds);
                    }
                    Constants.myChatIds = chatIds;
                    Constants.myChatMembers = allChats;
                    onWaitFragmentInteractionHide();
                    loadFragment(new MyChats_Main());
                } else {
                    onWaitFragmentInteractionHide();
                    duc.makeToast(this, getString(R.string.request_error));
                }
            } else {
                onWaitFragmentInteractionHide();
                duc.makeToast(this, getString(R.string.request_error));
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
            duc.makeToast(this, getString(R.string.request_error));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // locations-related task you need to do.
                    requestLocation();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("PERMISSION DENIED", "Nothing to see or do here.");

                    //Shut down the app. In production release, you would let the user
                    //know why the app is shutting down...maybe ask for permission again?
                    finishAndRemoveTask();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("REQUEST LOCATION", "User did NOT allow permission to request location!");
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d("LOCATION", location.toString());
                                mCurrentLocation = location;
                            }
                        }
                    });
        }
    }
}
