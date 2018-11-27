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
 * {@link ForgotPasswordFragment.OnForgotPasswordFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ForgotPasswordFragment extends Fragment {

    private OnForgotPasswordFragmentInteractionListener mListener;
    private EditText mUserCreds;
    private DataUtilityControl duc;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        this.duc = Constants.dataUtilityControl;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        // user entered nickname or email to identify them
        mUserCreds = view.findViewById(R.id.forgotPass_userCreds_editText);

        Button b = view.findViewById(R.id.forgotPass_enterPin_button);
        b.setOnClickListener(this::IHavePinClicked);

        b = view.findViewById(R.id.forgotPass_sendPin_button);
        b.setOnClickListener(this::getPinClicked);


        return view;
    }

    private void IHavePinClicked(View view) {
        mListener.onGoToForgotPassVerify();
    }

    private void getPinClicked(View view) {

        if (mUserCreds.getText().toString().length() <= 0) {
            mUserCreds.setError(getResources().getString(R.string.empty));
            return;
        }
        // set both as I do not know which they entered
        Credentials credentials =
                new Credentials.Builder(mUserCreds.getText().toString(), "")
                        .addNickName(mUserCreds.getText().toString()).build();
//        duc.saveCreds(credentials);
        // setup the task to send to forgotPass endpoint
        Uri uri = duc.getPasswordForgotPointURI();
        JSONObject userName = new JSONObject();
        try {
            userName.put("user", mUserCreds.getText().toString());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), userName)
                .onPreExecute(this::handleForgotPassOnPre)
                .onPostExecute(this::handleForgotPassOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleForgotPassOnPre() { mListener.onWaitFragmentInteractionShow(); }
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void handleForgotPassOnPost(String result) {
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            String email = resultsJSON.getString("email");
            if (status == 1) { // Successful email sent
                mListener.onGoToForgotPassVerify();
                saveEmailInUserPrefs(email);

            } else if (status == 2) { // User doesn’t exist in DB
                duc.makeToast(getContext(),
                        getResources().getString(R.string.toast_error_unidentified_user));

            } else if (status == 3) { // User exists but user is not verified
                duc.makeToast(getContext(),
                        getResources().getString(R.string.toast_error_user_not_verified));

            } else { // status 4 error or something else
                duc.makeToast(getContext(),
                        getResources().getString(R.string.toast_error_unidentified_user));
            }

        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: ");
        }
        mListener.onWaitFragmentInteractionHide();
    }

    private void saveEmailInUserPrefs(final String email) {
        SharedPreferences settings =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
//        //Store the email in SharedPrefs
//        prefs.edit().putString(getString(R.string.keys_prefs_email), email).apply();

        SharedPreferences.Editor settingsEdit = settings.edit();
        settingsEdit.putString(getString(R.string.keys_prefs_email), email);
        settingsEdit.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnForgotPasswordFragmentInteractionListener) {
            mListener = (OnForgotPasswordFragmentInteractionListener) context;
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
    public interface OnForgotPasswordFragmentInteractionListener extends WaitFragment.OnWaitFragmentInteractionListener {
        // Go to fragment for user to enter pin
        void onGoToForgotPassVerify();

    }
}
