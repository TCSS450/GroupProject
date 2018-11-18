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
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.PasswordRules;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnChangePasswordFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChangePasswordFragment extends Fragment {

    private OnChangePasswordFragmentInteractionListener mListener;
    private EditText password1;
    private EditText password2;
    private DataUtilityControl duc;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.duc = Constants.dataUtilityControl;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_password, container, false);
        password1 = v.findViewById(R.id.changePass_pass1_editText);
        password2 = v.findViewById(R.id.changePass_pass2_editText);

        Button b = v.findViewById(R.id.changePass_confirm_button);
        b.setOnClickListener(this::submitPasswords);

        return v;
    }

    /**
     * Verify that the password meets minimum requirements. If true and change the users password.
     * @param view submit button clicked
     */
    private void submitPasswords(View view) {
        //verify the password satisfies rules
        if (!PasswordRules.isValidPasswords(this, password1, password2)) {
            //start the async task to change password
            // todo: do this better .... clean up and refactor
            JSONObject msg = new JSONObject();
            SharedPreferences settings =
                    getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);

            String email = settings.getString(getString(R.string.keys_prefs_email),"");
            try {
                msg.put("user", email);
                msg.put("password", password1.getText().toString());
                System.out.println(msg.toString());
            } catch (JSONException e) {
                Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
            }
            Uri changePasswordEndpoint = duc.getPasswordChangeEndPointURI();
            new SendPostAsyncTask.Builder(changePasswordEndpoint.toString(), msg)
                    .onPreExecute(this::handleChangePassOnPre)
                    .onPostExecute(this::handleChangePassOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        } else {
            duc.makeToast(getContext(), "Password does not meet minimum requirements");
        }

    }

    private void handleChangePassOnPost(String result) {
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            if (status == 1) { // success
                mListener.onWaitFragmentInteractionHide();
                mListener.onChangePasswordSubmit();
                duc.makeToast(getContext(), "password change success. Please login with new password"); // Todo make this a resourse
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

    private void handleChangePassOnPre() { mListener.onWaitFragmentInteractionShow(); }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChangePasswordFragmentInteractionListener) {
            mListener = (OnChangePasswordFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChangePasswordFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     */
    public interface OnChangePasswordFragmentInteractionListener
                        extends WaitFragment.OnWaitFragmentInteractionListener{
        // notify successful of password change
        void onChangePasswordSubmit();

    }
}
