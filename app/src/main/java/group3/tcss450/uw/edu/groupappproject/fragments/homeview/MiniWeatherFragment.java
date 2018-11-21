package group3.tcss450.uw.edu.groupappproject.fragments.homeview;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.WeatherFromJsonString;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MiniWeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Display the weather on the home page.
 */
public class MiniWeatherFragment extends Fragment {

    private static final String TAG_TODAYS_WEATHER = "param1";
    private String mTodaysWeather;
    /** Java object of weather data */
    private WeatherFromJsonString mWeatherJava;


    public MiniWeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param todaysWeatherJSONString a string representing a json object of todays weather
     * @return A new instance of fragment MiniWeatherFragment.
     */
    public static MiniWeatherFragment newInstance(String todaysWeatherJSONString) { //todo: implement
        MiniWeatherFragment fragment = new MiniWeatherFragment();
        Bundle args = new Bundle();
        args.putString(TAG_TODAYS_WEATHER, todaysWeatherJSONString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTodaysWeather = getArguments().getString(TAG_TODAYS_WEATHER);
            // setup the weather object
            mWeatherJava = new WeatherFromJsonString(mTodaysWeather);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mini_weather, container, false);

        TextView weather = view.findViewById(R.id.miniWeather_weather_text);
        weather.setText(mTodaysWeather);
        weather.setText(mWeatherJava.getTemp());

        Log.d("Mini Weather todays weather: ", mTodaysWeather);

        return view;
    }

}
