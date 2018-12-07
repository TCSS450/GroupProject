package group3.tcss450.uw.edu.groupappproject.fragments.weather;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.activities.MapsActivity;
import group3.tcss450.uw.edu.groupappproject.fragments.AddFriend.AddUserFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.homeview.MiniWeatherFragment;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.MyFirebaseMessagingService;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherContainer#} factory method to
 * create an instance of this fragment.
 */
public class WeatherContainer extends Fragment {

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DataUtilityControl duc;
    private TextView mainTV;
    private FirebaseMessageReciever mFirebaseMessageReciever;

    private SharedPreferences sharedPreferences;

    public WeatherContainer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.duc = Constants.dataUtilityControl;
        if (Constants.MY_CURRENT_LOCATION != null) {
            makeDayWeatherCall(Constants.MY_CURRENT_LOCATION.getLatitude(), Constants.MY_CURRENT_LOCATION.getLongitude());
            makeHourWeatherCall(Constants.MY_CURRENT_LOCATION.getLatitude(), Constants.MY_CURRENT_LOCATION.getLongitude());
        } else {
            makeDayWeatherCall(47.2529, 122.4443);
            makeDayWeatherCall(47.2529, 122.4443);
        }
    }

    private void makeDayWeatherCall(double lat, double lon) {
        JSONObject msg = new JSONObject();
        //requestLocation();
        try {
            msg.put("lat", lat);
            msg.put("lon", lon);
            msg.put("days", 10);
        }catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error: " + e.getMessage());
        }
        new SendPostAsyncTask.Builder(this.duc.getWeatherDateURI().toString(), msg)
                .onPostExecute(this::handleOnPostWeatherDate)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void makeHourWeatherCall(double lat, double lon) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("lat", lat);
            msg.put("lon", lon);
        }catch (JSONException e) {
            Log.wtf("WEATHER", "Error: " + e.getMessage());
        }
        new SendPostAsyncTask.Builder(this.duc.getWeatherHourURI().toString(), msg)
                .onPostExecute(this::handleONPostWeatherHour)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
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



            }  else {
                this.duc.makeToast(duc.getApplicationContext(), "END_POINT_ERROR");
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());

        }
    }


    private void handleErrorsInTask(String result) {
    }

    private void handleONPostWeatherHour(String result) {
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
                    String substr = time.substring(time.indexOf("T")+ 1);
                    String temp = weather.getString("temp");
                    temp = temp + "ºF";
                    JSONObject innerWeatherDetails = weather.getJSONObject("weather");
                    String description = innerWeatherDetails.getString("description");
                    String icon = innerWeatherDetails.getString("icon");
                    WeatherDetails weatherDetailsObject = new WeatherDetails(temp,substr, description, icon);
                    weatherDetailsArrayList.add(weatherDetailsObject);
                }
                Constants.weatherDetails = weatherDetailsArrayList;
                load24hoursFragment(new WeatherDetailListFragment());
                load10DaysFragment(new WeatherFragment());
//                loadFragment(new WeatherContainer(), R.id.weather_container_id);

            }  else {
                this.duc.makeToast(duc.getApplicationContext(), "END_POINT_ERROR");
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());

        }

    }

    private void onWaitFragmentInteractionHide() {

    }

    private void onWaitFragmentInteractionShow() {

    }

    @Override
    public void onResume() {
        if (Constants.refreshLocation) {
            System.out.println("CAME FROM MAP ACTIVITY");
//            insertNestedFragment(R.id.frameLayout6, new WeatherDetailListFragment());
//            insertNestedFragment(R.id.days_frame, new WeatherFragment());
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
            Constants.refreshLocation = false;
        }
        super.onResume();
        if (mFirebaseMessageReciever == null) {
            mFirebaseMessageReciever = new FirebaseMessageReciever();
        }
        IntentFilter iFilter = new IntentFilter(MyFirebaseMessagingService.RECEIVED_NEW_MESSAGE);
        getActivity().registerReceiver(mFirebaseMessageReciever, iFilter);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weather_container, container, false);
        this.mainTV = v.findViewById(R.id.textView_weather_city_title);

        // Inflate the layout for this fragment

        //TextView cityText = v.findViewById(R.id.textView_weather_city_title);
        setCityText(mainTV);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.WeatherContainer_floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.MY_CURRENT_LOCATION == null) {
                    Snackbar.make(view, "Please wait for location to enable", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Intent i = new Intent(getActivity(), MapsActivity.class);
                    //pass the current location on to the MapActivity when it is loaded
                    i.putExtra("LOCATION", Constants.MY_CURRENT_LOCATION);
                    startActivity(i);
                }
            }
        });




        FloatingActionButton fab2 = (FloatingActionButton) v.findViewById(R.id.mainWeather_floatingActionButtonNew);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateDialog().show();
            }
        });
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
        JSONObject msg2 = new JSONObject();
        try {
            msg2.put("lat", Constants.MY_CURRENT_LOCATION.getLatitude());
            msg2.put("lon", Constants.MY_CURRENT_LOCATION.getLongitude());
        }catch (JSONException e) {
            Log.wtf("WEATHER", "Error: " + e.getMessage());
        }

        new SendPostAsyncTask.Builder(this.duc.getWeatherHourURI().toString(), msg2)
                .onPostExecute(this::handleONPostWeatherHour)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
        return v;
    }

    public Dialog onCreateDialog() {

        sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        try {
            JSONArray jsonArray2 = new JSONArray(sharedPreferences.getString("location", "[]"));
            System.out.println(jsonArray2.length());
            for (int i = 0 ; i <jsonArray2.length(); i++) {
                System.out.println("TEST " + jsonArray2.get(i));
                String[] parts = jsonArray2.get(i).toString().split(",");
                for (int j = 0; j < parts.length; j++) {
                    System.out.println(parts[j]);
                }
                //System.out.println("TEST2 " + jsonObject.toString().split(","));

                //Address address = new Address(Locale.ENGLISH);
                //address.set
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] arr = new String[Constants.previousLocation.size()];
        for(int i = 0; i<Constants.previousLocation.size();i++) {
            arr[i] = Constants.previousLocation.get(i).getLocality() + ", " + Constants.previousLocation.get(i).getPostalCode();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Location")
                .setItems(arr , (dialog, which) -> {
                    makeDayWeatherCall(Constants.previousLocation.get(which).getLatitude(),
                            Constants.previousLocation.get(which).getLongitude());
                    makeHourWeatherCall(Constants.previousLocation.get(which).getLatitude(),
                            Constants.previousLocation.get(which).getLongitude());
                    mainTV.setText("Weather in " +Constants.previousLocation.get(which).getLocality());
                });
        return builder.create();
    }
    private void setCityText(TextView text) {
        if(Constants.MY_CURRENT_LOCATION != null) {
            Geocoder geoCoder = new Geocoder(getContext());
            List<Address> list = null;
            String result = "Weather in ";
            try {
                list = geoCoder.getFromLocation(Constants.MY_CURRENT_LOCATION
                        .getLatitude(), Constants.MY_CURRENT_LOCATION.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list != null & list.size() > 0) {
                Address address = list.get(0);
                result += address.getLocality();
            }
            text.setText(result);
            Log.d("MapsActivity getLoc", result);
        }
    }

    private void load10DaysFragment(Fragment frag) {
        FragmentTransaction transaction =
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.days_frame, frag)
                        .addToBackStack(null);
        transaction.commit();
    }
    private void load24hoursFragment(Fragment frag) {
        FragmentTransaction transaction =
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout6, frag)
                        .addToBackStack(null);
        transaction.commit();
    }

    private void insertNestedFragment(int container, Fragment fragment) {

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(container, fragment).commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFirebaseMessageReciever != null){
            getActivity().unregisterReceiver(mFirebaseMessageReciever);
        }
    }

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
