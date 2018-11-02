package group3.tcss450.uw.edu.groupappproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;

public class RegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Credentials registerCreds;

    private OnWaitRegisterFragmentInteractionListener mListener;

    public RegisterFragment() {
        this.registerCreds = null;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        Button b = v.findViewById(R.id.registerBtn);
        EditText username =  v.findViewById(R.id.usernameInput);
        EditText password = v.findViewById(R.id.passwordInput);
        EditText firstName = v.findViewById(R.id.firstNameInput);
        EditText lastName = v.findViewById(R.id.lastNameInput);
        EditText passwordConf = v.findViewById(R.id.passwordConfirm);
        b.setOnClickListener(view -> attemptRegister(username, password, firstName,
                lastName, passwordConf));
        return v;
    }

    private void attemptRegister(EditText username, EditText password, EditText firstName,
                                 EditText lastName, EditText passwordConf) {
        if (username.getText().toString().length() == 0) {
            username.setError(getString(R.string.empty));
        } else if (password.getText().toString().length() == 0) {
            password.setError(getString(R.string.empty));
        } else if (passwordConf.getText().toString().length() == 0) {
            passwordConf.setError(getString(R.string.empty));
        } else if (password.getText().toString().length() < 6) {
            password.setError(getString(R.string.passToSmall));
        } else if (!password.getText().toString().equals(passwordConf.getText().toString())) {
            passwordConf.setError(getString(R.string.passNotMatch));
        } else if (firstName.getText().toString().length() == 0) {
            firstName.setError(getString(R.string.empty));
        } else if (lastName.getText().toString().length() == 0) {
            lastName.setError(getString(R.string.empty));
        } else {

            Credentials registerCreds = new Credentials.Builder(username.getText().toString(),
                    password.getText().toString())
                    .addFirstName(firstName.getText().toString())
                    .addLastName(lastName.getText().toString())
                    .addUsername(username.getText().toString())
                    .build();
            JSONObject msg = registerCreds.asJSONObject();
            this.registerCreds = registerCreds;
            Toast.makeText(getContext(),"Register Button Clicked! No implementation as of now!",
                    Toast.LENGTH_SHORT).show();



            /*new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleRegisterOnPre)
                    .onPostExecute(this::handleRegisterOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build()
                    .execute();*/
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
    }
}
