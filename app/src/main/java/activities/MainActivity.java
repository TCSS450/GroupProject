package activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import utility.Constants;
import utility.DataUtilityControl;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, PreLoginRegisterActivity.class);
        Constants.dataUtilityControl = new DataUtilityControl();
        startActivity(intent);
    }
}
