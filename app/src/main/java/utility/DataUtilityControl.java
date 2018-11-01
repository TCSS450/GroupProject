package utility;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import fragments.AddUserFragment;
import fragments.CreateChatFragment;
import fragments.CreateGroupFragment;
import fragments.HomeViewFragment;
import fragments.LoginFragment;
import fragments.RegisterFragment;
import fragments.VerificationFragment;
import fragments.ViewConnectionsFragment;
import fragments.ViewRequestsFragment;
import fragments.ViewWeatherFragment;
import fragments.WaitFragment;


public class DataUtilityControl extends AppCompatActivity {
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private WaitFragment waitFragment;
    private AddUserFragment addUserFragment;
    private CreateChatFragment createChatFragment;
    private CreateGroupFragment createGroupFragment;
    private ViewConnectionsFragment viewConnectionsFragment;
    private ViewRequestsFragment viewRequestsFragment;
    private ViewWeatherFragment viewWeatherFragment;
    private HomeViewFragment homeViewFragment;
    private VerificationFragment verificationFragment;


    private Credentials userCreds;


    public DataUtilityControl() {
        this.loginFragment = new LoginFragment();
        this.registerFragment = new RegisterFragment();
        this.waitFragment = new WaitFragment();
        this.addUserFragment = new AddUserFragment();
        this.createChatFragment = new CreateChatFragment();
        this.createGroupFragment = new CreateGroupFragment();
        this.viewConnectionsFragment = new ViewConnectionsFragment();
        this.viewRequestsFragment = new ViewRequestsFragment();
        this.viewWeatherFragment = new ViewWeatherFragment();
        this.homeViewFragment = new HomeViewFragment();
    }

    public Fragment getLoginFragment() {
        return this.loginFragment;
    }

    public Fragment getRegisterFragment() {
        return this.registerFragment;
    }

    public Fragment getWaitFragment() {return this.waitFragment;}

    public Fragment getAddUserFragment() {return this.addUserFragment;}

    public Fragment getCreateChatFragment() {return this.createChatFragment;}

    public Fragment getCreateGroupFragment() {return this.createGroupFragment;}

    public Fragment getViewConnectionsFragment() {return this.viewConnectionsFragment;}

    public Fragment getViewRequestsFragment() {return this.viewRequestsFragment;}

    public Fragment getViewWeatherFragment() {return this.viewWeatherFragment;}

    public Fragment getHomeViewFragment() {return this.homeViewFragment;}

    public Fragment getVerificationFragment() {
        if (verificationFragment == null) {
            this.verificationFragment = new VerificationFragment();
        }
        return verificationFragment;
    }


    public Uri getLoginEndPointURI() {
        return Uri.parse(Constants.LOGIN_END_POINT_URL);
    }

    public void saveCreds(Credentials userCredentials) {
        this.userCreds = userCredentials;
    }

    public Credentials getUserCreds() {
        return this.userCreds;
    }



    public void makeToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
