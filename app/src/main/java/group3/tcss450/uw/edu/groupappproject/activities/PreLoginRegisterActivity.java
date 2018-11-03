package group3.tcss450.uw.edu.groupappproject.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import group3.tcss450.uw.edu.groupappproject.fragments.LoginFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.RegisterFragment;
import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

public class PreLoginRegisterActivity extends AppCompatActivity implements
        LoginFragment.OnLoginWaitFragmentInteractionListener,
        RegisterFragment.OnWaitRegisterFragmentInteractionListener {

    private DataUtilityControl duc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login_register);
        this.duc = Constants.dataUtilityControl;
        this.loadFragment(this.duc.getLoginFragment());
    }


    private void loadFragment(Fragment frag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preLoginRegFrame, frag)
                .addToBackStack(null).commit();
    }


    @Override
    public void OnLogin(Credentials credentials) {
        this.duc.saveCreds(credentials);
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onRegisterClickedFromLogin() {
        loadFragment(this.duc.getRegisterFragment());
    }

    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.preLoginRegFrame, this.duc.getWaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();

    }

    /**
     * Loads the Verification Fragment after user registers.
     * @param credentials Users credentials
     */
    @Override
    public void registerUser(Credentials credentials) {
        this.duc.saveCreds(credentials);
        loadFragment(duc.getVerificationFragment());
    }

    public void verifyUser(Credentials credentials) {
        //Do we still save the credentials?
        loadFragment(duc.getVerificationFragment());
    }
}
