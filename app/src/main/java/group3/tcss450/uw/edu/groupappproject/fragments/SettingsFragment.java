package group3.tcss450.uw.edu.groupappproject.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PARAM_TAG = "credentials for user";
    private DataUtilityControl duc;

    // TODO: Rename and change types of parameters
    private Credentials mCredentials;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param credentials object to pass in
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(Credentials credentials) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_TAG, credentials);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //get the duc
        duc = Constants.dataUtilityControl;

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCredentials = (Credentials) getArguments().getSerializable(PARAM_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Button b = view.findViewById(R.id.settingsFrag_changeNickname_button);
        b.setOnClickListener(this::changeNickname);

        b = view.findViewById(R.id.settingsFrag_changePassword_button);
        b.setOnClickListener(this::changePassword);

        b = view.findViewById(R.id.settingsFrag_changeTheme_button);
        b.setOnClickListener(this::changeTheme);

        b= view.findViewById(R.id.settingsFrag_changeDisplayType);
        b.setOnClickListener(this::changeDisplayName);
        return view;
    }

    private void changeTheme(View view) {

    }

    private void changeDisplayName(View view) {
        loadFragment(new ChangeDisplayName());
    }

    private void changePassword(View view) {
        loadFragment(duc.getChangePasswordFragment());
    }

    private void changeNickname(View view) {

    }

    private void loadFragment(Fragment frag) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeActivityFrame, frag)
                .addToBackStack(null).commit();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     */
    public interface OnFragmentInteractionListener
    {

    }
}
