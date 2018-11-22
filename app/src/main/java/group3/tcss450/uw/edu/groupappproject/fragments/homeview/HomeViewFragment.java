package group3.tcss450.uw.edu.groupappproject.fragments.homeview;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.Arrays;
import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
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

    public HomeViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        duc = Constants.dataUtilityControl;

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
        myCredentials = duc.getUserCreds();
        textView.setText(myCredentials.getNickName().toString());


        //set a floating action button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(this::fabButtonClicked);

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
        JSONObject location = new JSONObject(); // todo: get this from device
        try {
            location.put("lat", 47.250040);
            location.put("lon", -122.287630);
            location.put("days", 1);
        } catch (JSONException e) {
            Log.wtf("JSON ERROR", "Error creating JSON: " + e.getMessage());
        }
        Log.d("HomeView", location.toString());

        new SendPostAsyncTask.Builder(Constants.WEATHER_END_POINT, location)
//                .onPreExecute(this::onP) //todo: make mListener for this class to parent??
                .onPostExecute(this::onPostGetWeather)
//                .onCancelled()
                .build().execute();

        // insert the friends list view
        insertNestedFragment(R.id.homeView_bestFriend_frame, new BestFriendsFragment());
    }

    private void onPostGetWeather(String result) {
//        Log.d("HomeViewFragment Weather post execute result: ", result);
        try {
            JSONObject data = new JSONObject(result);
            if (data.has("success")) {
                boolean success = data.getBoolean("success");
                if (success) {
                    JSONArray weather = data.getJSONArray("weather");
//                    Log.d("HomeView json weather --", weather.toString());
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
    }

    // Embeds the child fragment dynamically
    private void insertNestedFragment(int container, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(container, fragment).commit();
    }
}
