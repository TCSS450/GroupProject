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

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters
    private DataUtilityControl duc;
    private Credentials loginCreds;
    private EditText mEmail;
    private EditText mPassword;
    private String mFirebaseToken;

    private OnLoginWaitFragmentInteractionListener mListener;

    public LoginFragment() {
        this.loginCreds = null;
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
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mEmail = v.findViewById(R.id.nicknameInput);
        mPassword = v.findViewById(R.id.passwordInput);

        Button b = v.findViewById(R.id.loginBtn);
        b.setOnClickListener(view -> getFirebaseToken(mEmail.getText().toString(), mPassword.getText().toString()));
        //attemptLogin(mEmail.getText().toString(), mPassword.getText().toString()));

        b = v.findViewById(R.id.registerBtn);
        b.setOnClickListener(view ->
                mListener.onRegisterClickedFromLogin());
        return v;

    }
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) &&
                prefs.contains(getString(R.string.keys_prefs_password))) {
            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");
            //Load the two login EditTexts with the credentials found in SharedPrefs
            EditText emailEdit = getActivity().findViewById(R.id.nicknameInput);
            emailEdit.setText(email);
            EditText passwordEdit = getActivity().findViewById(R.id.passwordInput);
            passwordEdit.setText(password);

            getFirebaseToken(mEmail.getText().toString(),mPassword.getText().toString());
        }
    }


    private void attemptLogin(String nickname, String password) {
        if (nickname.length() == 0) {
            mEmail.setError(getString(R.string.empty));
        } /*else if (!username.getText().toString().contains("@")) {
            username.setError(getString(R.string.missingChar));
        }*/ else if (password.length() == 0) {
            mPassword.setError(getString(R.string.empty));
        } else {
            loginCreds = new Credentials.Builder(nickname,
                    password).build();

            Uri loginUri = this.duc.getLoginEndPointURI();
            JSONObject msg = loginCreds.asJSONObject();

            new SendPostAsyncTask.Builder(loginUri.toString(), msg)
                    .onPreExecute(this::handleLoginOnPre)
                    .onPostExecute(this::handleLoginOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build()
                    .execute();
        }

    }

    //Retrieve firebase token
    private void getFirebaseToken(final String email, final String password) {
        mListener.onWaitFragmentInteractionShow();

        //add this app on this device to listen for the topic all
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        //the call to getInstanceId happens asynchronously. task is an onCompleteListener
        //similar to a promise in JS.
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM: ", "getInstanceId failed", task.getException());
                        mListener.onWaitFragmentInteractionHide();
                        return;
                    }

                    // Get new Instance ID token
                    mFirebaseToken = task.getResult().getToken();
                    Log.d("FCM: ", mFirebaseToken);
                    //the helper method that initiates login service
                    attemptLogin(email, password);
                });
        //no code here. wait for the Task to complete.
    }


    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void handleLoginOnPre() {
        //mListener.onWaitFragmentInteractionShow();
    }

    private void handleLoginOnPost(String result) {
        /*1- Successful Login
		2- Email/NN doesn’t exist in DB (prompt user to register)
		3- Email/NN exists in DB, but password was incorrect (tell user to re-enter pass)
		4- Email/NN exists, but user is still not verified
		5-  Incorrect Input to endpoint / any other error */
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            if (status == 1) { // success
                saveCredentials(loginCreds);
                mListener.onWaitFragmentInteractionHide();
                mListener.OnLogin(this.loginCreds);
            }  else if (status == 2) { // email does not exist in DB, prompt to register
                mListener.onWaitFragmentInteractionHide();
                this.duc.makeToast(getContext(),
                        "Email not in our system/unrecognized nickname, please register");
            } else if (status == 3) { // Email/NN exists in DB, but password was incorrect (tell user to re-enter pass)
                mListener.onWaitFragmentInteractionHide();
                ((TextView) getView().findViewById(R.id.passwordInput))
                        .setError("Password Invalid");
            } else if (status == 4) { // Email is unverified. Resend email and go to verification.
                Log.d("LoginFragment", "recieved code 4");
                mListener.onWaitFragmentInteractionHide();
                Uri resendEmail = this.duc.getResendEndPointURI();
                JSONObject msg = new JSONObject();

                try {
                    msg.put("email", mEmail.getText().toString());

                }catch (JSONException e) {
                    Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
                }
                new SendPostAsyncTask.Builder(resendEmail.toString(), msg)
                        .onPostExecute(this::handleResendEmailOnPost)
                        .onCancelled(this::handleErrorsInTask)
                        .build().execute();
            } else {
                //mListener.onWaitFragmentInteractionHide();
                ((TextView) getView().findViewById(R.id.nicknameInput))
                        .setError("Login Unsuccessful");
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.nicknameInput))
                    .setError("Login Unsuccessful");
        }
    }

    /**
     * Method that handles the JSON response from the backend when dealing with a user
     * needing another Verification Code sent in an email.
     * @param result The results of the JSON.
     */
    private void handleResendEmailOnPost(String result) {
        /*
        1- Email was sent
        2- Email not sent.
        */
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            String email = resultsJSON.getString("email");
            if (status == 1) { // success go to verification page
                mListener.onWaitFragmentInteractionHide();
                duc.makeToast(getActivity(), getString(R.string.verification_greeting_subtitle) + " " + email);
                loginCreds = new Credentials.Builder(email,"").build();
                mListener.registeredUserSendToVerification(loginCreds);
            } else { //Endpoint Error
                mListener.onWaitFragmentInteractionHide();
                duc.makeToast(getActivity(), "OOPS! Something went wrong.");
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.nicknameInput))
                    .setError("Unsuccessful");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginWaitFragmentInteractionListener) {
            mListener = (OnLoginWaitFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginWaitFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void saveCredentials(final Credentials credentials) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //Store the credentials in SharedPrefs
        prefs.edit().putString(getString(R.string.keys_prefs_email), credentials.getEmail()).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_password), credentials.getPassword()).apply();
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLoginWaitFragmentInteractionListener extends WaitFragment.OnWaitFragmentInteractionListener{
        // TODO: Update argument type and name
        void OnLogin(Credentials credentials);
        void onRegisterClickedFromLogin();
        void registeredUserSendToVerification(Credentials credentials);
    }
}
