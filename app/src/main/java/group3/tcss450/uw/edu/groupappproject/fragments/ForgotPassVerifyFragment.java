package group3.tcss450.uw.edu.groupappproject.fragments;


import android.content.Context;
import android.content.SharedPreferences;
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
 *
 * To verify already resisted users. Uses same xml layout as VerificationFragment.
 */
public class ForgotPassVerifyFragment extends Fragment {

    private DataUtilityControl duc;
    private Credentials myCredentials; // todo: remove
    private String mEmail = "";

    private EditText mCode;
    private OnForgotPassVerifyFragmentInteractionListener mListener;


    public ForgotPassVerifyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) ) {
           mEmail = prefs.getString(getString(R.string.keys_prefs_email), "");

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.duc = Constants.dataUtilityControl;
        myCredentials = duc.getUserCreds(); // todo: remove not needed ????

        SharedPreferences settings =
                getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        mEmail= settings.getString(getString(R.string.keys_prefs_email),"");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verification, container, false);


        mCode = view.findViewById(R.id.editText_verify_code);
        TextView email = view.findViewById(R.id.textView_verification_email);
        Log.d("ForgotPassVerifyFragment", mEmail);
        email.setText(mEmail);
        Button b = view.findViewById(R.id.verification_verify_button);
        b.setOnClickListener(this::doVerify);
        b = view.findViewById(R.id.verification_resend_email_button);
        b.setOnClickListener(this::reSendEmail);

        return view;
    }

    /**
     * Attempt to verify a user and if successful log the user in.
     * @param view button clicked
     */
    private void doVerify(View view) {
        Uri verifyURI = this.duc.getVerifyEndPointURI();
        JSONObject msg = new JSONObject();
        try {
            msg.put("email", mEmail);
            msg.put("inputToken", Integer.parseInt(mCode.getText().toString()));
            System.out.println(msg.toString());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }
        new SendPostAsyncTask.Builder(verifyURI.toString(), msg)
                .onPreExecute(this::handleVerifyOnPre)
                .onPostExecute(this::handleVerifyOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleVerifyOnPost(String result) {
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
                mListener.verifiedUserSendToResetPassword();
            }  else if (status == 2) { // Wrong Credentials
                mListener.onWaitFragmentInteractionHide();
                ((TextView) getView().findViewById(R.id.editText_verify_code))
                        .setError(getString(R.string.invalid_code));
            } else { //Endpoint Error
                mListener.onWaitFragmentInteractionHide();
                ((TextView) getView().findViewById(R.id.editText_verify_code))
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

    private void reSendEmail(View view) {
        Uri reSendEmailEndPoint = duc.getResendEndPointURI();
        JSONObject msg = new JSONObject();
        try {
            msg.put("email", mEmail);
        }catch (JSONException e) {
            Log.wtf("ForgotPassVerifyFragment", "Error creating JSON: " + e.getMessage());
        }
        new SendPostAsyncTask.Builder(reSendEmailEndPoint.toString(), msg)
                .onPreExecute(this::handleVerifyOnPre)
                .onPostExecute(this::handleResendEmailOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleVerifyOnPre() { mListener.onWaitFragmentInteractionShow(); }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void handleResendEmailOnPost(String result) {
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            if (status == 1) { // success email sent
                duc.makeToast(getActivity(), getString(R.string.email_sent_msg));
            } else { //Endpoint Error email not sent
                duc.makeToast(getActivity(), "OOPS! Something went wrong.");
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());
            ((TextView) getView().findViewById(R.id.editText_verify_code))
                    .setError("Unsuccessful");
        }
        mListener.onWaitFragmentInteractionHide();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnForgotPassVerifyFragmentInteractionListener ) {
            mListener = (OnForgotPassVerifyFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeViewFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     */
    public interface OnForgotPassVerifyFragmentInteractionListener extends WaitFragment.OnWaitFragmentInteractionListener {
        // Go to fragment for user to enter pin
        void verifiedUserSendToResetPassword();

    }
}
