package group3.tcss450.uw.edu.groupappproject.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, PreLoginRegisterActivity.class);
        Constants.dataUtilityControl = new DataUtilityControl();
        startActivity(intent);
        //End this Activity and remove it from the Activity back stack.
        finish();


    }
}
