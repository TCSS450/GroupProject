package group3.tcss450.uw.edu.groupappproject.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
//import group3.tcss450.uw.edu.groupappproject.fragments.chat.ChatFragment
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {
    private boolean mLoadFromChatNotification = false;

    private static final String TAG = MainActivity.class.getSimpleName();
    public String checkNotify = "";
    public int notificationID = 0;
    public Boolean checkNotification = false;
    public int friendRequestCheck;
    public int friendAcceptedCheck;
    public String mOtherMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, PreLoginRegisterActivity.class);
        Constants.dataUtilityControl = new DataUtilityControl();

        System.out.println("check notify is first " + checkNotify);
        if (getIntent().getExtras() != null) {

            //System.out.println("the notification id at the start is now " + notificationID);
            if (getIntent().getExtras().containsKey("type")) {
                if(getIntent().getExtras().getString("type").equals("contact")){
                    notificationID = parseInt(getIntent().getExtras().getString("chatId"));
                    Constants.dataUtilityControl.setNotifyId(notificationID);
                    mOtherMembers = getIntent().getExtras().getString("otherMembers");
                    Constants.dataUtilityControl.setmOtherMembersChatNotification(mOtherMembers);
                    checkNotification = true;
                    Constants.dataUtilityControl.setBooleanNotify(checkNotification);
                }

                if (getIntent().getExtras().getString("type").equals("sent")){
                    friendRequestCheck = 33;
                    Constants.dataUtilityControl.setFriendRequests(friendRequestCheck);
                }
                if (getIntent().getExtras().getString("type").equals("accepted")){
                    friendAcceptedCheck = 33;
                    Constants.dataUtilityControl.setFriendAccept(friendAcceptedCheck);
                }

                Log.d(TAG, "type of message: " + getIntent().getExtras().getString("type"));
                //mLoadFromChatNotification = getIntent().getExtras().getString("type").equals("msg");
            } else {
                Log.d(TAG, "NO MESSAGE");
            }

        }
        System.out.println("check notify id is " + notificationID);
        startActivity(intent);
        finish();
    }
}
