package group3.tcss450.uw.edu.groupappproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.weather.Weather;
import group3.tcss450.uw.edu.groupappproject.fragments.weather.WeatherContainer;
import group3.tcss450.uw.edu.groupappproject.fragments.weather.WeatherDetailListFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.weather.WeatherFragment;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener
{

    private GoogleMap mMap;
    private Location mCurrentLocation;
    private Marker mMarker;
    private EditText mZipCode;
    private Button mSubmitButton;
    private SharedPreferences sharedPreferences;
    private DataUtilityControl duc = Constants.dataUtilityControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            JSONArray jsonArray2 = new JSONArray(sharedPreferences.getString("location", "[]"));

            for (int i = 0 ;i <jsonArray2.length(); i++) {
                Log.d("your JSON Array", jsonArray2.get(i).toString());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCurrentLocation = (Location) getIntent().getParcelableExtra("LOCATION");

        mZipCode = findViewById(R.id.maps_activity_enter_zip_text);
        mSubmitButton = findViewById(R.id.maps_activity_submitZip_button);
        mSubmitButton.setOnClickListener(this::getLocByZip);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handle setting weather by searching for a zip code on the map.
     * @param view the submit button clicked
     */
    private void getLocByZip(View view) {
        final Geocoder geocoder = new Geocoder(this);

        final String zip = mZipCode.getText().toString();
        try {
            List<Address> addresses = geocoder.getFromLocationName(zip, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Use the address as needed
                mCurrentLocation.setLatitude(address.getLatitude());
                mCurrentLocation.setLongitude(address.getLongitude());

                Constants.MY_CURRENT_LOCATION = mCurrentLocation;
                String message = setCityText(mCurrentLocation);
                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(),
                                mCurrentLocation.getLongitude()), 15.0f));

                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            } else {
                // Display appropriate message when Geocoder services are not available
                Toast.makeText(this, "Unable to geocode zipcode ", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // handle exception
        }
    }

    /**
     * Return the city of the zip entered
     */
    private String setCityText(Location loc) {
        Geocoder geoCoder = new Geocoder(this);
        List<Address> list = Constants.previousLocation;
        String result = "Weather set to ";
        try {
            list = geoCoder.getFromLocation(Constants.MY_CURRENT_LOCATION
                    .getLatitude(), Constants.MY_CURRENT_LOCATION.getLongitude(), 1);

            for (int i =0; i < list.size(); i++) {
                Constants.previousLocation.add(list.get(i));
            }

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(Constants.previousLocation);


            SharedPreferences.Editor editor = this.sharedPreferences.edit();

            editor.putString("location", jsonArray.toString());
            editor.commit();


        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null & list.size() > 0) {
            Address address = list.get(0);
            result += address.getLocality();
        }
        Log.d("MapsActivity setCityText zip lat/long",
                String.format("Latitude: %f, Longitude: %f", loc.getLatitude(), loc.getLongitude()));
        return result;
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a mMarker in the current device location and move the camera
        LatLng current = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
        //Zoom levels are from 2.0f (zoomed out) to 21.f (zoomed in)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f));
        mMap.setOnMapClickListener(this);
    }
    @Override
    public void onBackPressed() {
        Constants.refreshLocation = true;

        super.onBackPressed();


    }
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    /**
     * Set new weather location by clicking on the map
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("LAT/LONG", latLng.toString());

        mMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("New Weather Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));

        LatLng lat_long = mMarker.getPosition();
        Location loc = mCurrentLocation;
        loc.setLongitude(mMarker.getPosition().longitude);
        loc.setLatitude(mMarker.getPosition().latitude);
        Constants.MY_CURRENT_LOCATION = loc;
        Toast.makeText(this, setCityText(loc), Toast.LENGTH_LONG).show();

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
                loadFragment(new WeatherContainer());

//                loadFragment(new MainWeatherFragment());

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


}
