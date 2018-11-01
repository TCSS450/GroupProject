package fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import group3.tcss450.uw.edu.groupappproject.R;
import utility.Constants;
import utility.DataUtilityControl;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnVerificationFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * Display a screen to new un-verified users where they can enter
 * their registration pin to become verified users.
 */
public class VerificationFragment extends Fragment {

    private DataUtilityControl duc;

    private OnVerificationFragmentInteractionListener mListener;

    public VerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.duc = Constants.dataUtilityControl;
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verification, container, false);

        Button b = view.findViewById(R.id.verification_verify_button);
        b.setOnClickListener(this::attemptToVerifyUser);

        b = view.findViewById(R.id.verification_resend_email_button);
        b.setOnClickListener(this::resendVerificationEmail);

        return view;
    }

    /**
     * Attempt to verify a user and if successful log the user in.
     * @param view button clicked
     */
    private void attemptToVerifyUser(View view) {
    }

    /**
     * Resend the verification pin to the users email.
     * @param view button clicked
     */
    private void resendVerificationEmail(View view) {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVerificationFragmentInteractionListener) {
            mListener = (OnVerificationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnVerificationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *
     */
    public interface OnVerificationFragmentInteractionListener {

    }
}
