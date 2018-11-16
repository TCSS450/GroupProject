package group3.tcss450.uw.edu.groupappproject.fragments;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.FriendStatus;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddUserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DataUtilityControl duc;

    private int firstFriendStatus = -1;
    private int secondFriendStatus= -1;

    private RadioButton nickname;
    private RadioButton email;
    private RadioButton fullname;
    private RadioButton all;

    private Credentials currentCred;
    private ArrayList<FriendStatus> searchResult;
    private SendPostAsyncTask task1;
    private ArrayList<Credentials> tempCreds;

    private EditText searchView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ViewGroup container;
    private LayoutInflater inflater;
    private int entered =0;

    public AddUserFragment() {
        // Required empty public constructor
    }

    public static AddUserFragment newInstance(String param1, String param2) {
        AddUserFragment fragment = new AddUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        this.container = container;

        View v = inflater.inflate(R.layout.fragment_add_user, container, false);

        this.duc = Constants.dataUtilityControl;

        nickname = v.findViewById(R.id.nicknamebtn);
        email = v.findViewById(R.id.emailbtn);
        fullname = v.findViewById(R.id.fullnamebtn);
        all = v.findViewById(R.id.allbtn);

        searchView = v.findViewById(R.id.searchView);

        Button b = v.findViewById(R.id.searchbtn);
        b.setOnClickListener(view -> attemptSearch(searchView.getText().toString()));

        nickname.toggle();
        return v;
    }

    private void attemptSearch(String input) {
        if (input.length() > 0) {
            int searchtype =  -1;
            if (nickname.isChecked()) {
                searchtype = 1;
            } else if (email.isChecked()) {
                searchtype = 3;
            } else  if (fullname.isChecked()) {
                searchtype = 2;
            } else {
                searchtype = 4;
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
                    .onPostExecute(this::handleSearchOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }

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
            JSONObject msg = new JSONObject();
            for (int i = 0; i < tempCreds.size(); i++) {
                try {
                    msg.put("userAId", this.duc.getUserCreds().getMemberId());
                    msg.put("userBId", tempCreds.get(i).getMemberId());
                } catch (JSONException e) {
                    Log.wtf("CREDENTIALS", "Error: " + e.getMessage());
                }
                currentCred = tempCreds.get(i);
                System.out.println("DEBUG: IN SEARCH POST Handler " + currentCred.getNickName());
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
            firstFriendStatus = relationship;
            Uri uri = duc.getFriendStatusURI();
            JSONObject msg = new JSONObject();
            System.out.println("DEBUG: IN FIRSTTHREAD POST Handler " + tempCreds.get(task1.getIndex()).getNickName());

            try {
                msg.put("userAId", /*task1.getMemberId()*/ tempCreds.get(task1.getIndex()).getMemberId());
                msg.put("userBId", this.duc.getUserCreds().getMemberId());
            }catch (JSONException e) {
                Log.wtf("CREDENTIALS", "Error: " + e.getMessage());
            }
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
            System.out.println("DEBUG: IN SECTHREAD POST Handler " + tempCreds.get(task1.getIndex()).getNickName());
            secondFriendStatus = relationship;
            handleRelationshipOnPost();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void handleRelationshipOnPost() {

        System.out.println("DEBUG: IN RELATIONSHIP POST Handler " + tempCreds.get(task1.getIndex()).getNickName());
        if (firstFriendStatus == 1 && secondFriendStatus == 1) {
            //b.setBackgroundResource(R.drawable.ic_add_circle_outline_red_24dp);
            //searchResult.add(new FriendStatus(currentCred, 1));
            searchResult.add(new FriendStatus(tempCreds.get(entered), 1));
        } else if (firstFriendStatus == 2 || secondFriendStatus == 2) {
            //b.setBackgroundResource(R.drawable.ic_check_circle_green_24dp);
            searchResult.add(new FriendStatus(tempCreds.get(entered), 2));

        } else if (firstFriendStatus == 3) {
            //b.setBackgroundResource(R.drawable.ic_pending_black_24dp);
            searchResult.add(new FriendStatus(tempCreds.get(entered), 3));

        } else if (secondFriendStatus == 3) {
            //b.setBackgroundResource(R.drawable.ic_accept_green_24dp);
            searchResult.add(new FriendStatus(tempCreds.get(entered), 4));

        }
        entered++;
        Constants.searchResults = searchResult;
        loadFragment(duc.getNewFriendFragment());
    }

    private void testMethod() {
        System.out.println("BUTTON ACCESSED");
    }


}
