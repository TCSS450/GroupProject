package group3.tcss450.uw.edu.groupappproject.fragments.AddFriend;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.WaitFragment;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.FriendStatus;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;
public class AddUserFragment extends Fragment {

    private DataUtilityControl duc;
    private RadioButton nickname;
    private RadioButton email;
    private RadioButton fullname;
    private ArrayList<FriendStatus> searchResult;
    private SendPostAsyncTask task1;
    private ArrayList<Credentials> tempCreds;
    private int[] firstStatuses;
    private int[] secondStatuses;
    private EditText searchView;
    private int entered = 0;
    private int entered2 = 0;

    public AddUserFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_user, container, false);
        this.duc = Constants.dataUtilityControl;
        nickname = v.findViewById(R.id.nicknamebtn);
        email = v.findViewById(R.id.emailbtn);
        fullname = v.findViewById(R.id.fullnamebtn);
        searchView = v.findViewById(R.id.searchView);
        Button b = v.findViewById(R.id.searchbtn);
        b.setOnClickListener(view -> attemptSearch(searchView.getText().toString()));
        nickname.toggle();
        return v;
    }

    private void attemptSearch(String input) {
        if (input.length() > 0) {
            int searchtype =  -1;
            if (nickname.isChecked()) { searchtype = 1;
            } else if (email.isChecked()) { searchtype = 3;
            } else  if (fullname.isChecked()) { searchtype = 2;
            } else { searchtype = 4;
            }

            Uri uri = duc.getSearchEndPointURI();
            JSONObject msg = new JSONObject();
            try {
                msg.put("loggedInUserNickname", this.duc.getUserCreds().getNickName());
                msg.put("searchtype", searchtype);
                msg.put("searchstring", input);
            }catch (JSONException e) {
                Log.wtf("CREDENTIALS", "Error: " + e.getMessage());
            }
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleSearchOnPre)
                    .onPostExecute(this::handleSearchOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        } else {
            searchResult = new ArrayList<>();
            Constants.searchResults = searchResult;
            duc.makeToast(this.getContext(), "You must enter a search string to search");
            loadFragment(this.duc.getNewFriendFragment());
        }
    }

    private void handleSearchOnPre() {
        loadFragment(new WaitFragment());
    }


    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction =
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.framelayoutforlist, frag)
                        .addToBackStack(null);

        transaction.commit();
    }

    private void handleSearchOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            JSONArray data = resultsJSON.getJSONArray("data");
            System.out.println(data);
            tempCreds = new ArrayList<>();
            entered = 0;
            entered2 = 0;
            firstStatuses = new int[data.length()];
            secondStatuses = new int[data.length()];
            duc.getUserCreds().setMemberId(resultsJSON.getInt("loggedInMemeberId"));
            searchResult = new ArrayList<>();
            for (int i = 0; i< data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                Credentials cred = new Credentials.Builder("", "")
                        .addFirstName(c.getString("firstName"))
                        .addLastName(c.getString("lastName"))
                        .addNickName(c.getString("nickname"))
                        .addMemberId(c.getInt("memberid"))
                        .build();
                tempCreds.add(cred);
            }
            Uri friendUri = duc.getFriendStatusURI();
            if (tempCreds.size() == 0) {
                Constants.searchResults = searchResult;
                duc.makeShortToast(getContext(), "Zero search results");
                loadFragment(duc.getNewFriendFragment());
            }
            for (int i = 0; i < tempCreds.size(); i++) {
                JSONObject msg = new JSONObject();
                try {
                    msg.put("userAId", this.duc.getUserCreds().getMemberId());
                    msg.put("userBId", tempCreds.get(i).getMemberId());
                } catch (JSONException e) {
                    Log.wtf("CREDENTIALS", "Error: " + e.getMessage());
                }
                task1 = new SendPostAsyncTask.Builder(friendUri.toString(), msg)
                        .onPostExecute(this::handFirstThreadAfter)
                        .onCancelled(this::handleErrorsInTask)
                        .build();
                task1.setIndex(i);
                task1.execute();
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    private void handFirstThreadAfter(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            int relationship = resultsJSON.getInt("status");
            firstStatuses[entered2] = relationship;
            Uri uri = duc.getFriendStatusURI();
            JSONObject msg = new JSONObject();
            try {
                msg.put("userAId", tempCreds.get(entered2).getMemberId());
                msg.put("userBId", this.duc.getUserCreds().getMemberId());
            }catch (JSONException e) {
                Log.wtf("CREDENTIALS", "Error: " + e.getMessage());
            }
            entered2++;
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(this::handSecondThreadAfter)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handSecondThreadAfter(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            int relationship = resultsJSON.getInt("status");
            secondStatuses[entered] = relationship;
            handleRelationshipOnPost();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleRelationshipOnPost() {
        int firstStatus = firstStatuses[entered];
        int secondStatus = secondStatuses[entered];
        if (firstStatus == 1 && secondStatus == 1) { // not friends
            searchResult.add(new FriendStatus(tempCreds.get(entered), 1));
        } else if (firstStatus == 2 || secondStatus == 2) { // friends
            searchResult.add(new FriendStatus(tempCreds.get(entered), 2));
        } else if (firstStatus == 3) { // you sent them friend request, still pending
            searchResult.add(new FriendStatus(tempCreds.get(entered), 3));
        } else if (secondStatus == 3) { // they sent you friend request, waiting your approval
            searchResult.add(new FriendStatus(tempCreds.get(entered), 4));
        }
        entered++;
        Constants.searchResults = searchResult;
        loadFragment(duc.getNewFriendFragment());
    }
}