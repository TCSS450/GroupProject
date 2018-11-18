package group3.tcss450.uw.edu.groupappproject.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.ChatFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.AddFriend.FriendsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.HomeViewFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriendRequests.SentFriendRequestsFragment;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends.ViewFriends;
import group3.tcss450.uw.edu.groupappproject.fragments.WaitFragment;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

public class HomeActivity extends MenuOptionsActivity implements NavigationView.OnNavigationItemSelectedListener,
        FriendsFragment.OnListFragmentInteractionListener,
        WaitFragment.OnWaitFragmentInteractionListener,
        SentFriendRequestsFragment.OnListFragmentInteractionListener,
        ViewFriends.OnListFragmentInteractionListener {


    private DataUtilityControl duc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.duc = Constants.dataUtilityControl;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Load up the friends Fragment.
        loadFriendHelper();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(R.id.textView_header_user);
        String s;
        if (duc.getUserCreds().getDisplayPref() == 1) {
            s = duc.getUserCreds().getNickName() + " " + getString(R.string.nav_header_subtitle);
            textView.setText(s);
        } else if (duc.getUserCreds().getDisplayPref() == 2) {
            s = duc.getUserCreds().getFirstName() + " " + duc.getUserCreds().getLastName() + " " + getString(R.string.nav_header_subtitle);
            textView.setText(s);
        } else {
            s = duc.getUserCreds().getEmail() + " " + getString(R.string.nav_header_subtitle);
            textView.setText(s);
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addFriend) {
            loadFragment(this.duc.getAddUserFragment());
        } else if (id == R.id.createChat) {
            ChatFragment chat = new ChatFragment();
            loadFragment(chat);
            //loadFragment(this.duc.getCreateChatFragment());
        } else if (id == R.id.connections) {
            loadFriendHelper();
        } else if (id == R.id.requests) {
            loadFragment(this.duc.getFriendRequests());
        } else if (id == R.id.weather) {
            loadFragment(this.duc.getViewWeatherFragment());
        } else if (id == R.id.home) {
            loadFriendHelper();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment frag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeActivityFrame, frag)
                .addToBackStack(null).commit();
    }
    private void logout() {

        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //remove the saved credentials from StoredPrefs
        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
        //close the app
        new DeleteTokenAsyncTask().execute();

        finishAndRemoveTask();
        //or close this activity and bring back the Login
        //Intent i = new Intent(this, MainActivity.class);
        //startActivity(i);
        //End this Activity and remove it from the Activity back stack.
        //finish();
    }

    //@Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.homeActivityFrame, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }

    @Override
    public void onFriendListFragmentInteraction(Credentials credentials) {
        loadFragment(new ChatFragment());
    }

    @Override
    public void onListFragmentInteraction(Credentials item) {

    }

    // Deleting the InstanceId (Firebase token) must be done asynchronously. Good thing
    // we have something that allows us to do that.
    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onWaitFragmentInteractionShow();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
            prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
            try {
                //this call must be done asynchronously.
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                Log.e("FCM", "Delete error!");
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //close the app
            finishAndRemoveTask();
            //or close this activity and bring back the Login
            // Intent i = new Intent(this, MainActivity.class);
            // startActivity(i);
            // //Ends this Activity and removes it from the Activity back stack.
            // finish();
        }
    }

    private void loadFriendHelper() {
        JSONObject msg = new JSONObject();
        try {
            msg.put("user", duc.getUserCreds().getEmail());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }
        Uri uri = duc.getAllFriendsURI();
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleGetFriendsOnPostExecute) //todo: need errors
                .build().execute();
    }

    private void handleGetFriendsOnPostExecute(final String result) {
        //parse JSON
        Log.d("ViewFriends post execute result: ", result);
        try {
            JSONObject resultsJSON = new JSONObject(result);
            if (resultsJSON.has("error")) {
                boolean error = resultsJSON.getBoolean("error");
                if (!error) {
                    if (resultsJSON.has("friends")) {
                        JSONArray friendsArray = resultsJSON.getJSONArray("friends");
                        List<Credentials> creds = new ArrayList<>();
                        for(int i = 0; i < friendsArray.length(); i++) {
                            JSONObject jsonFriend = friendsArray.getJSONObject(i);
//                            Log.d("ViewFriends post execute friend: ", jsonFriend.toString());
                            creds.add(new Credentials.Builder(jsonFriend.getString("email"), "")
                                    .addNickName(jsonFriend.getString("nickname"))
                                    .addFirstName(jsonFriend.getString("firstname"))
                                    .addLastName(jsonFriend.getString("lastname"))
                                    .addPhoneNumber(jsonFriend.getString("phone"))
                                    .build());
                        }
                        Credentials[] credsAsArray = new Credentials[creds.size()];
                        credsAsArray = creds.toArray(credsAsArray);

                        //if the user has no friends make a toast
                        if (friendsArray.length() == 0) {
                            duc.makeToast(getBaseContext(), "You have no friends");
                        }

                        Bundle args = new Bundle();
                        args.putSerializable(ViewFriends.ARG_CRED_LIST, credsAsArray);
                        Fragment frag = new ViewFriends();
                        frag.setArguments(args);
                        onWaitFragmentInteractionHide();
                        loadFragment(frag);
                    } else {
                        Log.e("ERROR!", "No friends array");
                        //notify user
                        onWaitFragmentInteractionHide();
                    }
                } else {
                    duc.makeToast(getBaseContext(), "STATUS = 1");
                }
            } else {
                Log.e("ERROR!", "No response");
                duc.makeToast(getBaseContext(), "NOTHING STATUSY");
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            duc.makeToast(getBaseContext(), "IN CATCH??");
            onWaitFragmentInteractionHide();
        }
    }
}
