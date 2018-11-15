package group3.tcss450.uw.edu.groupappproject.utility;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import java.util.ArrayList;

import group3.tcss450.uw.edu.groupappproject.fragments.AddUserFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.CreateChatFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.CreateGroupFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ForgotPassVerifyFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ForgotPasswordFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.FriendRequestsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.FriendsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.HomeViewFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.LoginFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.RegisterFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ChangePasswordFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.SentFriendRequestsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.VerificationFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewWeatherFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.WaitFragment;


public class DataUtilityControl extends AppCompatActivity {
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private WaitFragment waitFragment;
    private AddUserFragment addUserFragment;
    private CreateChatFragment createChatFragment;
    private CreateGroupFragment createGroupFragment;
    private ViewConnectionsFragment viewConnectionsFragment;
    private ViewWeatherFragment viewWeatherFragment;
    private HomeViewFragment homeViewFragment;
    private VerificationFragment verificationFragment;
    private ForgotPasswordFragment forgotPasswordFragment;
    private ForgotPassVerifyFragment forgotPassVerifyFragment;
    private ChangePasswordFragment changePasswordFragment;

    private FriendsFragment friendsFragment;
    private FriendRequestsFragment friendRequestsFragment;
    private SentFriendRequestsFragment sentFriendRequestsFragment;
    private Credentials userCreds;


    public DataUtilityControl() {
        this.loginFragment = new LoginFragment();
        this.registerFragment = new RegisterFragment();
        this.waitFragment = new WaitFragment();
        this.addUserFragment = new AddUserFragment();
        this.createChatFragment = new CreateChatFragment();
        this.createGroupFragment = new CreateGroupFragment();
        this.viewConnectionsFragment = new ViewConnectionsFragment();
        this.viewWeatherFragment = new ViewWeatherFragment();
        this.homeViewFragment = new HomeViewFragment();
        this.verificationFragment = new VerificationFragment();
        this.friendsFragment = new FriendsFragment();
        this.friendRequestsFragment = new FriendRequestsFragment();
        this.sentFriendRequestsFragment = new SentFriendRequestsFragment();
        this.forgotPasswordFragment = new ForgotPasswordFragment();
        this.forgotPassVerifyFragment = new ForgotPassVerifyFragment();
        this.changePasswordFragment = new ChangePasswordFragment();
    }

    /* ***** Get Fragments **** ***/
    public Fragment getLoginFragment() {
        return this.loginFragment;
    }

    public Fragment getRegisterFragment() {
        return this.registerFragment;
    }

    public Fragment getWaitFragment() { return this.waitFragment;}

    public Fragment getAddUserFragment() { return this.addUserFragment;}

    public Fragment getCreateChatFragment() { return this.createChatFragment;}

    public Fragment getCreateGroupFragment() { return this.createGroupFragment;}

    public Fragment getViewConnectionsFragment() { return this.viewConnectionsFragment;}

    public Fragment getViewWeatherFragment() { return this.viewWeatherFragment;}

    public Fragment getHomeViewFragment() { return this.homeViewFragment;}

    public Fragment getVerificationFragment() { return this.verificationFragment;}

    public Fragment getNewFriendFragment() { return new FriendsFragment(); }

    public Fragment getFriendRequestsFragment() { return this.friendRequestsFragment; }

    public Fragment getSentFriendRequestsFragment() { return this.sentFriendRequestsFragment; }

    public Fragment getForgotPasswordFragment() {return this.forgotPasswordFragment; }

    public Fragment getPassForgotVerifyFragment() {return this.forgotPassVerifyFragment; }

    public Fragment getChangePasswordFragment() { return this.changePasswordFragment; }

    public ArrayList<Credentials> getCredFromFriendStatusList(ArrayList<FriendStatus> friendStatuses) {
        ArrayList<Credentials> toSend = new ArrayList<Credentials>();

        for (int i = 0; i <friendStatuses.size(); i++) {
            toSend.add(friendStatuses.get(i).getCred());

        }
        return toSend;
    }
    /* ***** Get Endpoints **** ***/

    public Uri getPasswordChangeEndPointURI() {
        return Uri.parse(Constants.PASSWORD_CHANGE_END_POINT_URL);
    }

    public Uri getPasswordForgotPointURI() {
        return Uri.parse(Constants.PASSWORD_FORGOT_END_POINT_URL);
    }

    public Uri getLoginEndPointURI() {
        return Uri.parse(Constants.LOGIN_END_POINT_URL);
    }

    public Uri getLoginEndPointFirebaseURI() { return Uri.parse(Constants.LOGIN_END_POINT_FIREBASE_URL); }

    public Uri getRegisterEndPointURI() { return Uri.parse(Constants.REGISTER_END_POINT_URL); }

    public Uri getVerifyEndPointURI() { return Uri.parse(Constants.VERIFY_END_POINT_URL); }

    public Uri getResendEndPointURI() { return Uri.parse(Constants.RESEND_END_POINT_URL); }

    public Uri getBaseEndPointURI() {return Uri.parse(Constants.BASE_END_POINT_URL); }

    public Uri getAddFriendEndPointURI() { return Uri.parse(Constants.ADD_FRIEND_URL); }

    public Uri getFriendRequestsRecievedEndPointURI() { return Uri.parse(Constants.RECEIVED_REQUESTS_END_POINT_URL); }

    public Uri getFriendRequestsSentEndPointURI() { return Uri.parse(Constants.SENT_REQUESTS_END_POINT_URL); }

    public void saveCreds(Credentials userCredentials) {
        this.userCreds = userCredentials;
    }

    public Credentials getUserCreds() {
        return this.userCreds;
    }



    public void makeToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
