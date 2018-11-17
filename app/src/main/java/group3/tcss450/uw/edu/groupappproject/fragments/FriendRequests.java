package group3.tcss450.uw.edu.groupappproject.fragments;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequests extends Fragment {

    private DataUtilityControl duc;


    public FriendRequests() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        this.duc = Constants.dataUtilityControl;
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Uri receivedUri = this.duc.getFriendRequestsRecievedEndPointURI();
        JSONObject msg = new JSONObject();
        try {
           msg.put("loggedInUserId", duc.getUserCreds().getMemberId());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }
        new SendPostAsyncTask.Builder(receivedUri.toString(), msg)
                .onPostExecute(this::handleReceivedOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();

        Uri sentUri = this.duc.getFriendRequestsSentEndPointURI();
        new SendPostAsyncTask.Builder(sentUri.toString(), msg)
                .onPostExecute(this::handleSentOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void handleReceivedOnPost(String result) {
        /*  1- Nobody has sent a Friend Request to you
            2- You have friend requests. Parse the JSON
            3- Error
        */
        try {
            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            ArrayList<Credentials> searchResult = new ArrayList<>();
            int status = resultsJSON.getInt("status");
            if (status == 1) {
                Constants.receivedRequests = searchResult;
                loadReceivedFragment(duc.getFriendRequestsFragment());
            } else if (status == 2) {
                try {
                    JSONArray data = resultsJSON.getJSONArray("data");
                    for (int i = 0; i< data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        Credentials cred = new Credentials.Builder("", "")
                                .addFirstName(c.getString("firstname"))
                                .addLastName(c.getString("lastname"))
                                .addNickName(c.getString("nickname"))
                                .build();
                        cred.setMemberId(c.getInt("memberId"));
                        searchResult.add(cred);
                    }
                    Constants.receivedRequests = searchResult;
                    loadReceivedFragment(duc.getFriendRequestsFragment());
                } catch (JSONException e) {
                    Log.e("JSON_PARSE_ERROR", result);
                }
            } else {
                duc.makeToast(getActivity(), "Cannot connect");
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
            duc.makeToast(getActivity(), "OOPS! Something went wrong!");
        }
    }

    private void handleSentOnPost(String result) {
        /*  1- You haven't sent a friend request.
            2- You have sent requests. Parse the JSON
            3- Error
        */
        try {
            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            ArrayList<Credentials> searchResult = new ArrayList<>();
            int status = resultsJSON.getInt("status");
            if (status == 1) {
                Constants.sentRequests = searchResult;
                loadReceivedFragment(duc.getSentFriendRequestsFragment());
            } else if (status == 2) {
                try {
                    JSONArray data = resultsJSON.getJSONArray("data");
                    for (int i = 0; i< data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        Credentials cred = new Credentials.Builder("", "")
                                .addFirstName(c.getString("firstname"))
                                .addLastName(c.getString("lastname"))
                                .addNickName(c.getString("nickname"))
                                .build();
                        cred.setMemberId(c.getInt("memberId"));
                        searchResult.add(cred);
                    }
                    Constants.sentRequests = searchResult;
                    loadSentFragment(duc.getSentFriendRequestsFragment());
                } catch (JSONException e) {
                    Log.e("JSON_PARSE_ERROR", result);
                }
            } else {
                duc.makeToast(getActivity(), "Cannot connect");
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
            duc.makeToast(getActivity(), "OOPS! Something went wrong!");
        }
    }

    private void loadReceivedFragment(Fragment frag) {
        FragmentTransaction transaction =
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FrameLayout_ReceivedRequests, frag)
                        .addToBackStack(null);
        transaction.commit();
    }

    private void loadSentFragment(Fragment frag) {
        FragmentTransaction transaction =
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FrameLayout_SentRequests, frag)
                        .addToBackStack(null);
        transaction.commit();
    }
}
