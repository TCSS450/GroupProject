package group3.tcss450.uw.edu.groupappproject.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

public class MainActivity extends AppCompatActivity {
    private boolean mLoadFromChatNotification = false;
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, PreLoginRegisterActivity.class);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("type")) {
                Log.d(TAG, "type of message: " + getIntent().getExtras().getString("type"));
                mLoadFromChatNotification = getIntent().getExtras().getString("type").equals("msg");
            } else {
                Log.d(TAG, "NO MESSAGE");
            }
        }
        Constants.dataUtilityControl = new DataUtilityControl();
        startActivity(intent);
        //End this Activity and remove it from the Activity back stack.
        finish();


    }
}
