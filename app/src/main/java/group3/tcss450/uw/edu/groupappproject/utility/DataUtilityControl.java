package group3.tcss450.uw.edu.groupappproject.utility;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import java.util.ArrayList;

import group3.tcss450.uw.edu.groupappproject.fragments.AddFriend.AddUserFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.chat.CreateChatFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ForgotPassVerifyFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ForgotPasswordFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriendRequests.FriendRequests;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriendRequests.FriendRequestsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.AddFriend.FriendsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.homeview.HomeViewFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.LoginFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.RegisterFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ChangePasswordFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriendRequests.SentFriendRequestsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.VerificationFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.weather.ViewWeatherFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.WaitFragment;


public class DataUtilityControl extends AppCompatActivity {
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private WaitFragment waitFragment;
    private AddUserFragment addUserFragment;
    private CreateChatFragment createChatFragment;

    private ViewWeatherFragment viewWeatherFragment;
    private HomeViewFragment homeViewFragment;
    private VerificationFragment verificationFragment;
    private ForgotPasswordFragment forgotPasswordFragment;
    private ForgotPassVerifyFragment forgotPassVerifyFragment;
    private ChangePasswordFragment changePasswordFragment;

    private FriendsFragment friendsFragment;
    private FriendRequests friendRequests;
    private FriendRequestsFragment friendRequestsFragment;
    private SentFriendRequestsFragment sentFriendRequestsFragment;
    private Credentials userCreds;
    private boolean mStartChatWithNotification;
    private int tempId;
    private int mFriendRequest;
    private int mFriendAccept;
    private String mUserTyping;
    private String mOtherMembersChatNotification;


    public DataUtilityControl() {
        this.loginFragment = new LoginFragment();
        this.registerFragment = new RegisterFragment();
        this.waitFragment = new WaitFragment();
        this.addUserFragment = new AddUserFragment();
        this.createChatFragment = new CreateChatFragment();

        this.viewWeatherFragment = new ViewWeatherFragment();
        this.homeViewFragment = new HomeViewFragment();
        this.verificationFragment = new VerificationFragment();
        this.friendsFragment = new FriendsFragment();
        this.friendRequestsFragment = new FriendRequestsFragment();
        this.sentFriendRequestsFragment = new SentFriendRequestsFragment();
        this.forgotPasswordFragment = new ForgotPasswordFragment();
        this.forgotPassVerifyFragment = new ForgotPassVerifyFragment();
        this.changePasswordFragment = new ChangePasswordFragment();
        this.tempId = 0;
        this.mFriendRequest = 0;
        this.mFriendAccept =0;
        this.mOtherMembersChatNotification = "";
        this.mUserTyping = "";
        this.mStartChatWithNotification = false;
        this.friendRequests = new FriendRequests();
    }

    /* ***** Get Fragments **** ***/
    public Fragment getLoginFragment() {
        return this.loginFragment;
    }

    public Fragment getRegisterFragment() {
        return this.registerFragment;
    }

    public Fragment getWaitFragment() { return this.waitFragment;}

    public Fragment getViewWeatherFragment() { return this.viewWeatherFragment;}

    public Fragment getVerificationFragment() { return this.verificationFragment;}

    public Fragment getNewFriendFragment() { return new FriendsFragment(); }

    public Fragment getForgotPasswordFragment() {return this.forgotPasswordFragment; }

    public Fragment getPassForgotVerifyFragment() {return this.forgotPassVerifyFragment; }

    public Fragment getChangePasswordFragment() { return this.changePasswordFragment; }

    public Fragment getFriendRequests() { return this.friendRequests; }

    public Boolean getBooleanId() { return this.mStartChatWithNotification;}

    public int getChAtId() { return this.tempId;}


    public int getFriendRequest() { return this.mFriendRequest;}

    public int getFriendAccept() { return this.mFriendAccept;}

    public String getmOtherMembersChatNotification() { return mOtherMembersChatNotification; }

    public String getmUserTyping(){ return mUserTyping; }

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

    public Uri getLoginEndPointFirebaseURI() { return Uri.parse(Constants.LOGIN_END_POINT_FIREBASE_URL); }

    public Uri getRegisterEndPointURI() { return Uri.parse(Constants.REGISTER_END_POINT_URL); }

    public Uri getVerifyEndPointURI() { return Uri.parse(Constants.VERIFY_END_POINT_URL); }

    public Uri getResendEndPointURI() { return Uri.parse(Constants.RESEND_END_POINT_URL); }

    public Uri getProfilesEndPointURI() {return Uri.parse(Constants.GET_PROFILES_URL); }

    public Uri getAddFriendEndPointURI() { return Uri.parse(Constants.ADD_FRIEND_URL); }

    public Uri getFriendRequestsRecievedEndPointURI() { return Uri.parse(Constants.RECEIVED_REQUESTS_END_POINT_URL); }

    public Uri getFriendRequestsSentEndPointURI() { return Uri.parse(Constants.SENT_REQUESTS_END_POINT_URL); }

    public Uri getSearchEndPointURI() { return Uri.parse(Constants.SEARCH_END_POINT_URL); }

    public Uri getFriendStatusURI() { return Uri.parse(Constants.FRIEND_STATUS_URL); }

    public Uri getRejectFriendURI() { return Uri.parse(Constants.REJECT_REQUEST_URL); }

    public Uri getAcceptFriendURI() { return Uri.parse(Constants.ACCEPT_REQUEST_URL); }

    public Uri getAllFriendsURI() { return Uri.parse(Constants.VIEW_FRIENDS_URL); }

    public Uri getCreateChatURI() { return Uri.parse(Constants.CREATE_CHAT_URL); }

    public Uri getDeleteFriendURI() { return Uri.parse(Constants.DELETE_FRIEND_URL); }

    public Uri getCurrentChatsURI() { return Uri.parse(Constants.GET_ALL_CHATS_END_POINT_URL); }

    public Uri getWeatherDateURI() { return Uri.parse(Constants.WEATHER_DATE_URL); }

    public Uri getWeatherHourURI() { return Uri.parse(Constants.WEATHER_HOUR_URL); }

    public Uri getChangeDisplayTypeURI() { return Uri.parse(Constants.CHANGE_DISPLAY_TYPE_URL); }

    public Uri getFriendReferralURI() { return Uri.parse(Constants.SEND_REFERRAL_URL); }

    public int getWeatherDrawable(Context context, String iconCode) {
        return context.getResources().getIdentifier("group3.tcss450.uw.edu.groupappproject:drawable/" + iconCode, null, null);
    }


    public void setBooleanNotify(Boolean b) { mStartChatWithNotification = b;}

    public void setNotifyId(int i) { tempId = i; }

    public void setFriendRequests(int i) { mFriendRequest = i; }

    public void setFriendAccept(int i) {
        mFriendAccept = i;
    }

    public void setmOtherMembersChatNotification(String otherMembers) { mOtherMembersChatNotification = otherMembers; }

    public void setmUserTyping(String type) {mUserTyping = type; }

    public void saveCreds(Credentials userCredentials){this.userCreds = userCredentials;}

    public Credentials getUserCreds() {
        return this.userCreds;
    }





    public void makeToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public void makeShortToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
