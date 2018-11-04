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
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

public class RegisterFragment extends Fragment {
    private Credentials registerCreds;
    private int mDisplayPref;
    private DataUtilityControl duc;

    private OnWaitRegisterFragmentInteractionListener mListener;

    public RegisterFragment() {
        this.registerCreds = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.duc = Constants.dataUtilityControl;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        EditText nickName =  v.findViewById(R.id.register_nicknameInput);
        EditText password = v.findViewById(R.id.passwordInput);
        EditText email = v.findViewById(R.id.emailInput);
        EditText phoneNumber = v.findViewById(R.id.phoneNumberInput);
        EditText firstName = v.findViewById(R.id.firstNameInput);
        EditText lastName = v.findViewById(R.id.lastNameInput);
        EditText passwordConf = v.findViewById(R.id.passwordConfirm);
        RadioButton rb = (RadioButton) v.findViewById(R.id.radioButtonNickname);
        rb = (RadioButton) v.findViewById(R.id.radioButtonNickname);
        rb.setChecked(true);
        mDisplayPref = 1;
        rb.setOnClickListener((view) -> {onRadioButtonClicked(view);});
        rb = (RadioButton) v.findViewById(R.id.radioButtonFullName);
        rb.setOnClickListener((view) -> {onRadioButtonClicked(view);});
        rb = (RadioButton) v.findViewById(R.id.radioButtonEmail);
        rb.setOnClickListener((view) -> {onRadioButtonClicked(view);});
        Button b = v.findViewById(R.id.registerBtn);
        b.setOnClickListener(view -> attemptRegister(nickName, password, firstName,
                lastName, passwordConf, phoneNumber, email));
        return v;
    }

    private void attemptRegister(EditText theNickname, EditText thePassword, EditText theFirstName,
                                 EditText theLastName, EditText thePasswordVerification, EditText thePhoneNumber,
                                 EditText theEmail) {
        if (theNickname.getText().toString().length() == 0) {
            theNickname.setError(getString(R.string.empty));
        } else if (thePassword.getText().toString().length() == 0) {
            thePassword.setError(getString(R.string.empty));
        } else if (thePasswordVerification.getText().toString().length() == 0) {
            thePasswordVerification.setError(getString(R.string.empty));
        } else if (thePassword.getText().toString().length() < 6) {
            thePassword.setError(getString(R.string.passToSmall));
        } else if (!thePassword.getText().toString().equals(thePasswordVerification.getText().toString())) {
            thePasswordVerification.setError(getString(R.string.passNotMatch));
        } else if (theFirstName.getText().toString().length() == 0) {
            theFirstName.setError(getString(R.string.empty));
        } else if (theLastName.getText().toString().length() == 0) {
            theLastName.setError(getString(R.string.empty));
        } else if (thePhoneNumber.getText().toString().length() != 10) {
            thePhoneNumber.setError(getString(R.string.invalidPhone));
        } else if (theEmail.getText().toString().length() == 0) {
            theEmail.setError(getString(R.string.empty));
        } else if (!theEmail.getText().toString().contains("@")) {
            theEmail.setError(getString(R.string.invalidEmail));
        } else {
            Credentials registerCredentials = new Credentials.Builder(theEmail.getText().toString(),
                    thePassword.getText().toString())
                    .addFirstName(theFirstName.getText().toString())
                    .addLastName(theLastName.getText().toString())
                    .addNickName(theNickname.getText().toString())
                    .addPhoneNumber(thePhoneNumber.getText().toString())
                    .addDisplayPref(mDisplayPref)
                    .addVerifyCode((int)(Math.random()*9000)+1000)
                    .build();
            Uri registerUri = this.duc.getRegisterEndPointURI();
            this.registerCreds = registerCredentials;
            JSONObject msg = registerCreds.asJSONObject();
            System.out.println(msg.toString());
            new SendPostAsyncTask.Builder(registerUri.toString(), msg)
                    .onPreExecute(this::handleRegisterOnPre)
                    .onPostExecute(this::handleRegisterOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonNickname:
                if (checked)
                    mDisplayPref = 1;
                break;
            case R.id.radioButtonFullName:
                if (checked)
                    mDisplayPref = 2;
                break;
            case R.id.radioButtonEmail:
                if (checked)
                    mDisplayPref = 3;
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWaitRegisterFragmentInteractionListener) {
            mListener = (OnWaitRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWaitRegisterFragmentInteractionListener");
        }
    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void handleRegisterOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    private void handleRegisterOnPost(String result) {
        /*1- Register Successful (Tell User to verify, send authNumber to their email)
        2- Email already exists without verification (Prompt user to verify)
        3- Email already exists with verification (Tell user to login)
        4- Nickname already exists (tell user to provide another nickname)
        5- Email is invalid (prompt user to enter a valid email)
        6- Incorrect Input to endpoint / any other error
        */
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            if (status == 1) { // success
                mListener.onWaitFragmentInteractionHide();
                mListener.registerUser(registerCreds);
            }  else if (status == 2) { // email exists without Verification
                mListener.onWaitFragmentInteractionHide();
                mListener.verifyUser(registerCreds);
            } else if (status == 3) { //Email already exists.
                mListener.onWaitFragmentInteractionHide();
                ((TextView) getView().findViewById(R.id.emailInput))
                        .setError(getString(R.string.emailExists));
            } else if (status == 4) { //Nickname already exists
                mListener.onWaitFragmentInteractionHide();
                ((TextView) getView().findViewById(R.id.register_nicknameInput))
                        .setError(getString(R.string.emailExists));
            } else if (status == 5) {
                mListener.onWaitFragmentInteractionHide();
                ((TextView) getView().findViewById(R.id.register_nicknameInput))
                        .setError(getString(R.string.invalidEmail));
            } else {
                mListener.onWaitFragmentInteractionHide();
                ((TextView) getView().findViewById(R.id.register_nicknameInput))
                        .setError("6");
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
    public interface OnWaitRegisterFragmentInteractionListener extends WaitFragment.OnWaitFragmentInteractionListener {
        // TODO: Update argument type and name
        void registerUser(Credentials credentials);
        void verifyUser(Credentials credentials);
    }
}
