package group3.tcss450.uw.edu.groupappproject.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.SettingsFragment;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

/**
 * Any class that uses these same menus should extend this class.
 *
 * Add any menus that you want displayed in the action bar in this
 * class.
 *
 * TODO: this class is not used move homeActivity logic to here
 */
public class MenuOptionsActivity extends AppCompatActivity {

    private DataUtilityControl duc;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.duc = Constants.dataUtilityControl;
        Log.d("MenuOptionsActivity", "in onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        return true;
    }

    /**
     * Add any menus that you want displayed in the action bar here.
     * @param item menu item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Log.d("MenuOptionsActivity", "settings button clicked");
                loadFragment(SettingsFragment.newInstance(this.duc.getUserCreds()));
                return true;

            case R.id.action_logout:
                // User chose to logout
                Log.d("MenuOptionsActivity", "logout menu item clicked");
                logoutClicked();
                return true;

            case R.id.menu_nickname:
                // change display settings to nickname
                return true;

            case R.id.menu_email:
                // change display setting to email
                return true;

            case R.id.menu_fullName:
                // change display setting to full name
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void loadFragment(Fragment frag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeActivityFrame, frag)
                .addToBackStack(null).commit();
    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void handleRegisterOnPre() { }

    private void handleRegisterOnPost(String result) {
        /*1- Register Successful (Tell User to verify, send authNumber to their email)
        2- Email already exists without verification (Prompt user to verify)
        3- Email already exists with verification (Tell user to login)
        4- Nickname already exists (tell user to provide another nickname)
        5- Email is invalid (prompt user to enter a valid email)
        6- Incorrect Input to endpoint / any other error
        */
        try {
            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            if (status == 1) { // success, sends email and user must verify

            } else if (status == 2) { // email exists without Verification

            } else if (status == 3) { //Email already exists with verification.

            } else if (status == 4) { //Nickname already exists

            } else  {

            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * Log the user out of the app and redirect to home screen.
     */
    public void logoutClicked() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Log.d("SuccessFragment", "does it still run in method after call " +
                "to startActivity on a new intent?"); //the answer is yes!
        this.finish(); //finish the current activity
    }

    private void logout() {

    }

}