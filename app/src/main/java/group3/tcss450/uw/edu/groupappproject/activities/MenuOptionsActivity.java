package group3.tcss450.uw.edu.groupappproject.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import group3.tcss450.uw.edu.groupappproject.R;

/**
 * Any class that uses these same menus should extend this class.
 *
 * Add any menus that you want displayed in the action bar in this
 * class.
 */
public class MenuOptionsActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                return true;

            case R.id.action_logout:
                // User chose to logout
                Log.d("MenuOptionsActivity", "logout menu item clicked");
                logoutClicked();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

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
}