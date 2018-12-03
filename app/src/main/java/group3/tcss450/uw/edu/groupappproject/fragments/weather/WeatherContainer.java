package group3.tcss450.uw.edu.groupappproject.fragments.weather;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.zip.Inflater;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.activities.MapsActivity;
import group3.tcss450.uw.edu.groupappproject.fragments.homeview.MiniWeatherFragment;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
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
    private LayoutInflater inflater;
    private ViewGroup viewGroup;
    private Bundle bundle;

    private SharedPreferences sharedPreferences;


    public WeatherContainer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.duc = Constants.dataUtilityControl;
        this.bundle = savedInstanceState;
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

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.viewGroup = container;
        this.bundle = savedInstanceState;

        View v = inflater.inflate(R.layout.fragment_weather_container, container, false);
        // Inflate the layout for this fragment

        TextView cityText = v.findViewById(R.id.textView_weather_city_title);
        setCityText(cityText);

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

                Dialog d = onCreateDialog();
                d.show();
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                System.out.println("outside DIALOG");
//
//                builder.setTitle("TEST")
//                        .setItems(arr, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                System.out.println("INSIDE DIALOG");
//                            }
//                        });
//                builder.create();
//
//                builder.show();
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
        String[] arr = new String[Constants.previousLocation.size()];
        for(int i = 0; i<Constants.previousLocation.size();i++) {
            arr[i] = Constants.previousLocation.get(i).getLocality() + ", " + Constants.previousLocation.get(i).getPostalCode();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Location")
                .setItems(arr , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        insertNestedFragment(R.layout.fragment_weather_container, new WeatherFragment());
//                        onCreateView(inflater, viewGroup, bundle);
//                        load24hoursFragment(new WeatherDetailListFragment());
//                        load10DaysFragment(new WeatherFragment());
//                        onCreate(bundle);

                    }
                });
        return builder.create();
    }
    private void setCityText(TextView text) {
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

    private void loadFragment(Fragment frag, int id) {
        FragmentTransaction transaction =
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(id, frag)
                        .addToBackStack(null);
        transaction.commit();
    }

    private void insertNestedFragment(int container, Fragment fragment) {

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(container, fragment).commit();
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
//        mListener.onWaitFragmentInteractionHide();
    }



}
