package group3.tcss450.uw.edu.groupappproject.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

/**
 * A simple {@link Fragment} subclass.
 *
 * To verify already resisted users. Uses same xml layout as VerificationFragment.
 */
public class ForgotPassVerifyFragment extends Fragment {

    private DataUtilityControl duc;
    private Credentials myCredentials;


    public ForgotPassVerifyFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.duc = Constants.dataUtilityControl;
        myCredentials = duc.getUserCreds();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verification, container, false);


//        From verificationFragment
//        mCode = view.findViewById(R.id.editText_verify_code);
//        TextView email = view.findViewById(R.id.textView_verification_email);
//        email.setText(myCredentials.getEmail());
//        Button b = view.findViewById(R.id.verification_verify_button);
//        b.setOnClickListener(this::doVerify);
//        b = view.findViewById(R.id.verification_resend_email_button);
//        b.setOnClickListener(this::reSendEmail);

        return view;
    }

}
