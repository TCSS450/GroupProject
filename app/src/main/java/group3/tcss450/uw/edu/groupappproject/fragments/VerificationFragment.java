package group3.tcss450.uw.edu.groupappproject.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

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
    private Credentials myCredentials;
    private OnVerificationFragmentInteractionListener mListener;
    private EditText mCode;

    public VerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.duc = Constants.dataUtilityControl;
        myCredentials = duc.getUserCreds();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verification, container, false);
        mCode = view.findViewById(R.id.editText_verify_code);
        TextView email = view.findViewById(R.id.textView_verification_email);
        email.setText(myCredentials.getEmail());
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
        Uri verifyURI = this.duc.getRegisterEndPointURI();
        JSONObject msg = new JSONObject();
        try {
            msg.put("email", myCredentials.getEmail());
            msg.put("authNumber", mCode.getText());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }
        new SendPostAsyncTask.Builder(verifyURI.toString(), msg)
                .onPreExecute(this::handleRegisterOnPre)
                .onPostExecute(this::handleRegisterOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void handleRegisterOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    private void handleRegisterOnPost(String result) {
        /*
        1- User was verified (User is now eligible to login)
        2- User entered wrong authNumber (Tell them to re-enter)
        3- Incorrect Input to endpoint / any other error
        */
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            if (status == 1) { // success
                mListener.onWaitFragmentInteractionHide();
                mListener.verifiedUserSendToSuccess(myCredentials);
            }  else if (status == 2) { // Wrong Credentials
                mListener.onWaitFragmentInteractionHide();
                ((TextView) getView().findViewById(R.id.editText_verify_code))
                        .setError(getString(R.string.invalid_code));
            } else { //Endpoint Error
                mListener.onWaitFragmentInteractionHide();
                ((TextView) getView().findViewById(R.id.emailInput))
                        .setError(getString(R.string.verification_fail));
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.register_nicknameInput))
                    .setError("Login Unsuccessful");
        }
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

    public interface OnVerificationFragmentInteractionListener extends WaitFragment.OnWaitFragmentInteractionListener {
        void verifiedUserSendToSuccess(Credentials myCredentials);
    }
}
