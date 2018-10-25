package fragments;

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
import utility.Constants;
import utility.Credentials;
import utility.DataUtilityControl;
import utility.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLoginWaitFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DataUtilityControl duc;
    private Credentials loginCreds;

    private OnLoginWaitFragmentInteractionListener mListener;

    public LoginFragment() {
        this.loginCreds = null;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.duc = Constants.dataUtilityControl;
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        EditText username = v.findViewById(R.id.usernameInput);
        EditText password = v.findViewById(R.id.passwordInput);

        Button b = v.findViewById(R.id.loginBtn);
        b.setOnClickListener(view -> attemptLogin(username, password));

        b = v.findViewById(R.id.registerBtn);
        b.setOnClickListener(view ->
                mListener.onRegisterClickedFromLogin());
        return v;

    }

    private void attemptLogin(EditText username, EditText password) {
        if (username.getText().toString().length() == 0) {
            username.setError(getString(R.string.empty));
        } else if (!username.getText().toString().contains("@")) {
            username.setError(getString(R.string.missingChar));
        } else if (password.getText().toString().length() == 0) {
            password.setError(getString(R.string.empty));
        } else {
            loginCreds = new Credentials.Builder(username.getText().toString(),
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

    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleLoginOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            mListener.onWaitFragmentInteractionHide();
            if (success) {
                mListener.OnLogin(this.loginCreds);
            } else {
                //Login was unsuccessful. Don’t switch fragments and inform the user
                System.out.println("HERE");
                System.out.println(result);
                ((TextView) getView().findViewById(R.id.usernameInput))
                        .setError("Login Unsuccessful");
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.usernameInput))
                    .setError("Login Unsuccessful");
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
    }
}
