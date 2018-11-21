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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeViewFragment.
     */
    // TODO: delete factory method
    public static HomeViewFragment newInstance(String param1, String param2) {
        HomeViewFragment fragment = new HomeViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load or do something

                Toast.makeText(getContext(), "you clicked me", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //call async task to get the weather before loading fragment
        JSONObject location = new JSONObject();
        try {
            location.put("lat", 47.250040);
            location.put("lon", -122.287630);
            location.put("days", 1);
        } catch (JSONException e) {
            Log.wtf("JSON ERROR", "Error creating JSON: " + e.getMessage());
        }
        Log.d("HomeView", location.toString());
        new SendPostAsyncTask.Builder(Constants.WEATHER_END_POINT, location)
//                .onPreExecute(this::onP) //todo: make mListener for this class to parent
                .onPostExecute(this::onPostGetWeather)
//                .onCancelled()
                .build().execute();

        insertNestedFragment();
    }

    private void onPostGetWeather(String result) {

        Log.d("HomeViewFragment Weather post execute result: ", result);
        insertNestedFragment();
//        try {
//            JSONObject resultsJSON = new JSONObject(result);
//            if (resultsJSON.has("error")) {
//                boolean error = resultsJSON.getBoolean("error");
//                if (!error) {
//                    if (resultsJSON.has("friends")) {
//                        JSONArray friendsArray = resultsJSON.getJSONArray("friends");
//                        ArrayList<Credentials> creds = new ArrayList<>();
//                        for (int i = 0; i < friendsArray.length(); i++) {
//                            JSONObject jsonFriend = friendsArray.getJSONObject(i);
////                            Log.d("ViewFriends post execute friend: ", jsonFriend.toString());
//                            creds.add(new Credentials.Builder(jsonFriend.getString("email"), "")
//                                    .addNickName(jsonFriend.getString("nickname"))
//                                    .addFirstName(jsonFriend.getString("firstname"))
//                                    .addLastName(jsonFriend.getString("lastname"))
//                                    .addPhoneNumber(jsonFriend.getString("phone"))
//                                    .addMemberId(jsonFriend.getInt("memberid"))
//                                    .build());
//                        }
//                        Constants.myFriends = creds;
//                        loadFriendsFragment(new ViewFriends());
//                    }
//                } else {
//                    duc.makeToast(getActivity(), "Oops! An Error has occurred");
//                }
//            }
//        } catch (JSONException e) {
//            Log.e("JSON_PARSE_ERROR", result);
//            duc.makeToast(getActivity(), "OOPS! Something went wrong!");
//        }
    }

    // Embeds the child fragment dynamically
    private void insertNestedFragment() {
        Fragment miniWeatherFragment = new MiniWeatherFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.homeView_weather_frame, miniWeatherFragment).commit();
    }
}
