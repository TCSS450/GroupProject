package group3.tcss450.uw.edu.groupappproject.fragments.homeview;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.Chats.MyChats_Main;
import group3.tcss450.uw.edu.groupappproject.fragments.WaitFragment;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
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

        // ask user for permission
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //check permission user has given and take appropriate actions
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        } else {
            //The user has already allowed the use of Locations. Get the current location.
            requestLocation();
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    //setLocation(location);
                    Log.d("LOCATION UPDATE!", location.toString());
                }
            };
        };

        createLocationRequest();
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
            String fullname = creds.getFirstName() + " " + creds.getLastName();
            textView.setText(fullname);
        } else {
            textView.setText(creds.getEmail());
        }

        //set a floating action button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
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
                    duc.makeToast(getContext(),"Failed to get weather");
                }
            } else { // wrong info sent up or something went wrong
                duc.makeToast(getContext(),"We can not get the weather");
            }
        } catch (JSONException e) { // todo: set text in frag instead
            Log.e("JSON_PARSE_ERROR", result);
            duc.makeToast(getActivity(), "OOPS! Something went wrong!");
        }
        mListener.onWaitFragmentInteractionHide();
    }

    @Override
    public void onStart() {
        super.onStart();
        // only call the server to get friends on first startup
//        if (Constants.myFriends == null) {
//            Uri getFriendsURI = this.duc.getAllFriendsURI();
//            JSONObject msg = new JSONObject();
//            try {
//                msg.put("user", duc.getUserCreds().getEmail());
//            } catch (JSONException e) {
//                Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
//            }
//            new SendPostAsyncTask.Builder(getFriendsURI.toString(), msg)
//                    .onPostExecute(this::handleGetFriendsOnPost)
//                    .onCancelled(this::handleErrorsInTask)
//                    .build().execute();
//            insertNestedFragment(R.id.homeView_bestFriend_frame, new MyChats_Main());
//
//        } else {
//            insertNestedFragment(R.id.homeView_bestFriend_frame, new MyChats_Main());
//        }
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
                        insertNestedFragment(R.id.homeView_bestFriend_frame, new BestFriendsFragment());
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

    /* ****** Location request & get user location Code below *************************************/

    /**
     * See if the user has granted permission to access their location. If not
     * ask the user for permission.
     */
    private void requestLocation() {
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
                                Constants.MY_CURRENT_LOCATION = location;
                                //setLocation(location);
                                Log.d("LOCATION", location.toString());
                            }
                        }
                    });
        }
    }


    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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
                    getActivity().finishAndRemoveTask();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Create and configure a Location Request used when retrieving location updates
     */
    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Set the textview to have the current location.
     * @param
     */
//    private void setLocation(final Location location) {
//        mCurrentLocation = location;
//        Constants.MY_CURRENT_LOCATION = mCurrentLocation;
//        //call async task to get the weather before loading fragment
//        JSONObject jsonLocation = new JSONObject();
//        try {
//            jsonLocation.put("lat", mCurrentLocation.getLatitude());
//            jsonLocation.put("lon", mCurrentLocation.getLongitude());
//            jsonLocation.put("days", 1);
//        } catch (JSONException e) {
//            Log.wtf("JSON ERROR", "Error creating JSON: " + e.getMessage());
//        }
//        Log.d("LOCATION: ",mCurrentLocation.getLatitude() +
//                                " " + mCurrentLocation.getLongitude());
//        new SendPostAsyncTask.Builder(Constants.WEATHER_END_POINT, jsonLocation)
//                .onPreExecute(this::onPreGetWeather)
//                .onPostExecute(this::onPostGetWeather)
//                .build().execute();
//    }

    private void onPreGetWeather() { mListener.onHomeViewWaitShow(R.id.homeView_weather_frame); }

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
    }
}
