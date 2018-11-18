package group3.tcss450.uw.edu.groupappproject.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateChatFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private int mParam1;


    public CreateChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param creds Parameter 1.
     * @return A new instance of fragment CreateChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateChatFragment newInstance(Credentials creds) {
        CreateChatFragment fragment = new CreateChatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, creds.getMemberId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_create_chat, container, false);
    }

}
