package group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

public class ViewFriends_Main extends Fragment {

    private DataUtilityControl duc;

    public ViewFriends_Main() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_viewfriends_main, container, false);
        this.duc = Constants.dataUtilityControl;
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Uri getFriendsURI = this.duc.getAllFriendsURI();
        JSONObject msg = new JSONObject();
        try {
            msg.put("user", duc.getUserCreds().getEmail());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }
        new SendPostAsyncTask.Builder(getFriendsURI.toString(), msg)
                .onPostExecute(this::handleGetFriendsOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }


    private void handleGetFriendsOnPost(String result) {

        //parse JSON
        Log.d("ViewFriends post execute result: ", result);
        try {
            JSONObject resultsJSON = new JSONObject(result);
            if (resultsJSON.has("error")) {
                boolean error = resultsJSON.getBoolean("error");
                if (!error) {
                    if (resultsJSON.has("friends")) {
                        JSONArray friendsArray = resultsJSON.getJSONArray("friends");
                        ArrayList<Credentials> creds = new ArrayList<>();
                        for (int i = 0; i < friendsArray.length(); i++) {
                            JSONObject jsonFriend = friendsArray.getJSONObject(i);
//                            Log.d("ViewFriends post execute friend: ", jsonFriend.toString());
                            creds.add(new Credentials.Builder(jsonFriend.getString("email"), "")
                                    .addNickName(jsonFriend.getString("nickname"))
                                    .addFirstName(jsonFriend.getString("firstname"))
                                    .addLastName(jsonFriend.getString("lastname"))
                                    .addPhoneNumber(jsonFriend.getString("phone"))
                                    .addMemberId(jsonFriend.getInt("memberid"))
                                    .build());
                        }
                        Constants.myFriends = creds;
                        loadFriendsFragment(new ViewFriends());
                    }
                } else {
                    duc.makeToast(getActivity(), "Oops! An Error has occurred");
                }
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result);
            duc.makeToast(getActivity(), "OOPS! Something went wrong!");
        }
    }

    private void loadFriendsFragment(Fragment frag) {
        FragmentTransaction transaction =
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FrameLayout_viewFriends_listFrame, frag)
                        .addToBackStack(null);
        transaction.commit();
    }
}
