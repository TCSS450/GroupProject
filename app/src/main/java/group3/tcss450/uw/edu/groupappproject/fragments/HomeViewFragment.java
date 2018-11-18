package group3.tcss450.uw.edu.groupappproject.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends.ViewFriends;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;

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
    private String mParam1;
    private String mParam2;
    public static final String ARG_CRED_LIST = "creds lists";
    private List<Credentials> mCreds;
    private Credentials[] mCredentials;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_view, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = new Bundle();
        args.putSerializable(ViewFriends.ARG_CRED_LIST, mCredentials);
        Fragment frag = new HomeViewFragment();
        frag.setArguments(args);
        FragmentTransaction transaction =
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FrameLayout_viewFriends_mainFrame, new ViewFriends())
                        .addToBackStack(null);
        transaction.commit();
    }
}
