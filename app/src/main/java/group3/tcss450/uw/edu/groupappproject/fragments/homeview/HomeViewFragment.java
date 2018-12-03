package group3.tcss450.uw.edu.groupappproject.fragments.homeview;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.Chats.MyChats_Main;
import group3.tcss450.uw.edu.groupappproject.fragments.WaitFragment;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.MyFirebaseMessagingService;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeViewFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private DataUtilityControl duc;
    private String username;
    public static final String ARG_CRED_LIST = "creds lists";
    private List<Credentials> mCreds;
    private Credentials[] mCredentials;
    private Credentials myCredentials;
    private FirebaseMessageReciever mFirebaseMessageReciever;


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
    private OnHomeViewFragmentListener mListener;
    private GoogleApiClient mGoogleApiClient;

    public HomeViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        duc = Constants.dataUtilityControl;
        //was made with a list of friends
        if (getArguments() != null) {
            Log.d("ViewFriends", "getArgs not null");
            mCredentials = (Credentials[]) getArguments().getSerializable(ARG_CRED_LIST);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_view, container, false);

        TextView textView = view.findViewById(R.id.homeView_username_text);
        this.duc = Constants.dataUtilityControl;
        Credentials creds = duc.getUserCreds();
        if (creds.getDisplayPref() == 1 ) {
            textView.setText(creds.getNickName());
        } else if (creds.getDisplayPref() == 2) {
            textView.setText(duc.getUserCreds().getFullName());
        } else {
            textView.setText(creds.getEmail());
        }

        //set a floating action button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(this::fabButtonClicked);
        insertNestedFragment(R.id.homeView_bestFriend_frame, new MyChats_Main());
        return view;
    }

    /**
     * Todo: update action that happens here
     * @param view fabButton
     */
    private void fabButtonClicked(View view) {
//        Toast.makeText(getContext(), "you clicked me", Toast.LENGTH_LONG).show();
        Log.d("fab", "fab button clicked");
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //call async task to get the weather before loading fragment
        JSONObject latLong = new JSONObject();


        try {
            latLong.put("lat", Constants.MY_CURRENT_LOCATION.getLatitude());
            latLong.put("lon", Constants.MY_CURRENT_LOCATION.getLongitude());
            latLong.put("days", 10);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(Constants.WEATHER_END_POINT, latLong)
                .onPreExecute(this::onPreGetWeather)
                .onPostExecute(this::onPostGetWeather)
                .build().execute();
    }


    private void onPostGetWeather(String result) {
        Log.d("HomeViewFragment Weather post execute result: ", result);
        try {
            JSONObject data = new JSONObject(result);
            if (data.has("success")) {
                boolean success = data.getBoolean("success");
                if (success) {
                    JSONArray weather = data.getJSONArray("weather");
                    Log.d("HomeView json weather --", weather.toString());
                    JSONObject todaysWeather = new JSONObject(weather.getJSONObject(0).toString());
                    Log.d("HomeView json todays weather", todaysWeather.toString());

                    // instantiate this with factory method
                    insertNestedFragment(R.id.homeView_weather_frame,
                            MiniWeatherFragment.newInstance(todaysWeather.toString()));

                } else { //failed to get weather
                    Toast.makeText(getContext(), "Failed to get weather", Toast.LENGTH_LONG).show();
                }
            } else { // wrong info sent up or something went wrong
                Toast.makeText(getContext(), "We can not get the weather", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) { // todo: set text in frag instead
            Log.e("JSON_PARSE_ERROR", result);
            Toast.makeText(getContext(), "OOPS! Something went wrong!", Toast.LENGTH_LONG).show();
        }
        mListener.onWaitFragmentInteractionHide();
    }

    /**
     * Get the friends of this user
     */
    @Override
    public void onStart() {
        super.onStart();
        // only call the server to get friends on first startup
        if (Constants.myFriends == null) {
            Uri getFriendsURI = this.duc.getAllFriendsURI();
            JSONObject msg = new JSONObject();
            try {
                msg.put("user", duc.getUserCreds().getEmail());
            } catch (JSONException e) {
                Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
            }
            new SendPostAsyncTask.Builder(getFriendsURI.toString(), msg)
                    .onPostExecute(this::handleGetFriendsOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private void handleErrorsInTask(String result) {
        System.out.println("INSIDE ERRORS");
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void handleGetFriendsOnPost(String result) {
        //parse JSON
        Log.d("ViewFriends post execute result: ", result);
        try {
            JSONObject resultsJSON = new JSONObject(result);
            if (resultsJSON.has("error")) {
                boolean error = resultsJSON.getBoolean("error");
                if (!error) {
                    if (resultsJSON.has("friends")) {
                        JSONArray friendsArray = resultsJSON.getJSONArray("friends");
                        ArrayList<Credentials> creds = new ArrayList<>();
                        for (int i = 0; i < friendsArray.length(); i++) {
                            JSONObject jsonFriend = friendsArray.getJSONObject(i);
                            Log.d("ViewFriends post execute friend: ", jsonFriend.toString());
                            creds.add(new Credentials.Builder(jsonFriend.getString("email"), "")
                                    .addNickName(jsonFriend.getString("nickname"))
                                    .addFirstName(jsonFriend.getString("firstname"))
                                    .addLastName(jsonFriend.getString("lastname"))
                                    .addPhoneNumber(jsonFriend.getString("phone"))
                                    .addMemberId(jsonFriend.getInt("memberid"))
                                    .build());
                        }
                        Constants.myFriends = creds;
                        // insert the friends list view
//                        insertNestedFragment(R.id.homeView_bestFriend_frame, new BestFriendsFragment());
                    }
                } else {
                    duc.makeToast(getActivity(), "Oops! An Error has occurred");
                }
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result);
            duc.makeToast(getActivity(), "OOPS! Something went wrong!");
        }
    }

    // Embeds the child fragment dynamically
    private void insertNestedFragment(int container, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(container, fragment).commit();
    }

    private void onPreGetWeather() { mListener.onHomeViewWaitShow(R.id.homeView_weather_frame); }


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
                    Log.d("PERMISSION DENIED", "Nothing to see or do here.");
                    getActivity().finishAndRemoveTask();
                }
                return;
            }
        }
    }

    private void requestLocation() {
        System.out.println("INSIDE REQUEST LOCATION");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("REQUEST LOCATION", "User did NOT allow permission to request location!");
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d("LOCATION", location.toString());
                                mCurrentLocation = location;
                                Constants.MY_CURRENT_LOCATION = mCurrentLocation;
                            }
                        }
                    });
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeViewFragmentListener) {
            mListener = (OnHomeViewFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChatFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     */
    public interface OnHomeViewFragmentListener
            extends WaitFragment.OnWaitFragmentInteractionListener
    {
        public void onHomeViewWaitShow(int container);
        void onNewMessage();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFirebaseMessageReciever == null) {
            mFirebaseMessageReciever = new FirebaseMessageReciever();
        }
        IntentFilter iFilter = new IntentFilter(MyFirebaseMessagingService.RECEIVED_NEW_MESSAGE);
        getActivity().registerReceiver(mFirebaseMessageReciever, iFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mFirebaseMessageReciever != null){
            getActivity().unregisterReceiver(mFirebaseMessageReciever);
        }
    }

    /**
     * A BroadcastReceiver setup to listen for messages sent from
     * MyFirebaseMessagingService
     * that Android allows to run all the time.
     */
    private class FirebaseMessageReciever extends BroadcastReceiver {
        //String duc.DataUtilityControl
        @Override
        public void onReceive(Context context, Intent intent) {

            System.out.println("IS THIS WORKING");
            Log.i("FCM Chat Frag", "start onRecieve");
            if (intent.hasExtra("DATA")) {
                System.out.println("THERE IS AN INTENT");
                String data = intent.getStringExtra("DATA");
                Log.d("Message data", data);
                try {
                    System.out.println("PARSING");
                    JSONObject jObj = new JSONObject(data);
                    if (jObj.has("type")) {
                        if (jObj.getString("type").equals("contact")) {
                            String contact = jObj.getString("sender");
                            String message = jObj.getString("message");
                            duc.makeToast(getActivity(), "New Message from " + contact + " \nMessage: " + message);
                            mListener.onNewMessage();
                        } else if (jObj.getString("type").equals("sent")) {
                                String contact = jObj.getString("senderString") ;
                                duc.makeToast(getActivity(), "New Friend Request from " + contact);
                        } else if (jObj.getString("type").equals("accepted")) {
                                String contact = jObj.getString("senderString");
                                duc.makeToast(getActivity(), contact + " accepted your friend request");
                        }
                    }
                } catch (JSONException e) {
                    Log.e("JSON PARSE", e.toString());
                }
            }
        }
    }
}
