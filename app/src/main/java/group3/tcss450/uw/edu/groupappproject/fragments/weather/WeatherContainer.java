package group3.tcss450.uw.edu.groupappproject.fragments.weather;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherContainer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherContainer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DataUtilityControl duc;


    public WeatherContainer() {
        // Required empty public constructor
        this.duc = Constants.dataUtilityControl;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherContainer.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherContainer newInstance(String param1, String param2) {
        WeatherContainer fragment = new WeatherContainer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
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
                    String temp = weather.getString("temp");


                    JSONObject innerWeatherDetails = weather.getJSONObject("weather");

                    String description = innerWeatherDetails.getString("description");

                    String icon = innerWeatherDetails.getString("icon");



                    WeatherDetails weatherDetailsObject = new WeatherDetails(temp,time, description, icon);

                    weatherDetailsArrayList.add(weatherDetailsObject);

                }
                Constants.weatherDetails = weatherDetailsArrayList;

                load24hoursFragment(new WeatherDetailListFragment());
                load10DaysFragment(new WeatherFragment());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_weather_container, container, false);
        // Inflate the layout for this fragment


        return v;
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




}
