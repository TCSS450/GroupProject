package group3.tcss450.uw.edu.groupappproject.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import group3.tcss450.uw.edu.groupappproject.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ForgotPasswordFragment.OnForgotPasswordFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ForgotPasswordFragment extends Fragment {

    private OnForgotPasswordFragmentInteractionListener mListener;
    private EditText userCreds;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        // user entered nickname or email to identify them
        userCreds = view.findViewById(R.id.forgotPass_userCreds_editText);

        Button b = view.findViewById(R.id.forgotPass_enterPin_button);
        b.setOnClickListener(this::enterPinClicked);

        b = view.findViewById(R.id.forgotPass_sendPin_button);
        b.setOnClickListener(this::sendPinClicked);


        return view;
    }

    private void enterPinClicked(View view) {
    }

    private void sendPinClicked(View view) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnForgotPasswordFragmentInteractionListener) {
            mListener = (OnForgotPasswordFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**

     */
    public interface OnForgotPasswordFragmentInteractionListener extends
                RegisterFragment.OnWaitRegisterFragmentInteractionListener {
        // will use reg frag onWait method to send user to verification fragment, change backend and verfify frag

        // user has a pin they want to enter
//        void onGetPinClicked();

    }
}
