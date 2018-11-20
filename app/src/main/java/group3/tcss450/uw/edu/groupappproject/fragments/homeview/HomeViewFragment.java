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

import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

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
    // TODO: Rename and change types and number of parameters
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

        if (getArguments() != null) {
            Log.d("ViewFriends", "getArgs not null");
            mCredentials = (Credentials[]) getArguments().getSerializable(ARG_CRED_LIST);
        }
//        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
//        fab.setVisibility(View.VISIBLE);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //load or do something
////                loadFragment(new ViewFriends_Main());
//                fab.setVisibility(View.INVISIBLE);
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_view, container, false);

        TextView textView = view.findViewById(R.id.HomeView_username_text);
        this.duc = Constants.dataUtilityControl;
        myCredentials = duc.getUserCreds();
        textView.setText(myCredentials.getNickName().toString());

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //call async task to get the weather before loading fragment

        insertNestedFragment();
    }

    // Embeds the child fragment dynamically
    private void insertNestedFragment() {
        Fragment miniWeatherFragment = new MiniWeatherFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.homeView_weather_frame, miniWeatherFragment).commit();
    }
}
