package group3.tcss450.uw.edu.groupappproject.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
//import group3.tcss450.uw.edu.groupappproject.fragments.ChatFragment
import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.ChatFragment;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {
    private boolean mLoadFromChatNotification = false;

    private static final String TAG = MainActivity.class.getSimpleName();
    public String checkNotify = "";
    public int notificationID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, PreLoginRegisterActivity.class);
        System.out.println("check notify is first " + checkNotify);
        if (getIntent().getExtras() != null) {

            notificationID = parseInt(getIntent().getExtras().getString("chatId"));
            System.out.println("the notification id at the start is now " + notificationID);
            if (getIntent().getExtras().containsKey("type")) {
                Log.d(TAG, "type of message: " + getIntent().getExtras().getString("type"));
                mLoadFromChatNotification = getIntent().getExtras().getString("type").equals("msg");
            } else {
                Log.d(TAG, "NO MESSAGE");
            }

        }
        System.out.println("check notify is second " + notificationID);

        Constants.dataUtilityControl = new DataUtilityControl();
        startActivity(intent);
        //End this Activity and remove it from the Activity back stack.
        finish();
    }
}
