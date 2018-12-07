package group3.tcss450.uw.edu.groupappproject.fragments.homeview;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
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
    public static MiniWeatherFragment newInstance(String todaysWeatherJSONString) {
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
            Log.d("json on create", mTodaysWeather);
            mWeatherJava = new WeatherFromJsonString(mTodaysWeather);
            Log.d("json on create json", mWeatherJava.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mini_weather, container, false);

        if (mWeatherJava != null) {

            TextView temp = view.findViewById(R.id.miniWeather_temp_text);
            temp.setText(mWeatherJava.getTemp());

            TextView date = view.findViewById(R.id.miniWeather_date_text);
            date.setText(mWeatherJava.getDateTime());

            TextView description = view.findViewById(R.id.miniWeather_weather_descript_text);
            description.setText(mWeatherJava.getWeatherDescription());

            //set the icon eventually
            ImageView weatherIcon = view.findViewById(R.id.miniWeather_icon_image);
            weatherIcon.setImageResource(Constants.dataUtilityControl
                            .getWeatherDrawable(getContext(), mWeatherJava.getWeatherIcon()));
        }

        return view;
    }

}
