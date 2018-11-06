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

public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters
    private DataUtilityControl duc;
    private Credentials loginCreds;
    private EditText mEmail;

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
        EditText password = v.findViewById(R.id.passwordInput);

        Button b = v.findViewById(R.id.loginBtn);
        b.setOnClickListener(view -> attemptLogin(mEmail, password));

        b = v.findViewById(R.id.registerBtn);
        b.setOnClickListener(view ->
                mListener.onRegisterClickedFromLogin());
        return v;

    }

    private void attemptLogin(EditText nickname, EditText password) {
        if (nickname.getText().toString().length() == 0) {
            nickname.setError(getString(R.string.empty));
        } /*else if (!username.getText().toString().contains("@")) {
            username.setError(getString(R.string.missingChar));
        }*/ else if (password.getText().toString().length() == 0) {
            password.setError(getString(R.string.empty));
        } else {
            loginCreds = new Credentials.Builder(nickname.getText().toString(),
                    password.getText().toString()).build();

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

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void handleLoginOnPre() {
        mListener.onWaitFragmentInteractionShow();
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
