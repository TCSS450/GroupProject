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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private boolean firstThreadDone = false;
    private boolean secondThreadDone = false;

    private RadioButton nickname;
    private RadioButton email;
    private RadioButton fullname;
    private RadioButton all;

    private Credentials currentCred;
    private ArrayList<FriendStatus> searchResult;

    private EditText searchView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ViewGroup container;
    private LayoutInflater inflater;

    public AddUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddUserFragment.
     */
    // TODO: Rename and change types and number of parameters
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



            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .authority(Constants.BASE_END_POINT_URL)
                    .appendPath("search-members")
                    .build();


            JSONObject msg = new JSONObject();
            try {
                msg.put("loggedInUserNickname", this.duc.getUserCreds().getNickName());
                msg.put("searchtype", searchtype);
                msg.put("searchstring", input);

            }catch (JSONException e) {
                Log.wtf("CREDENTIALS", "Error: " + e.getMessage());
            }
            System.out.println(msg);


            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(this::handleSearchOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
            System.out.println("TEMPERRAYYY CRED " + Constants.temporaryCreds.size());


//            task.execute(uri.toString());
        }

    }

    private void getFriendStatus(ArrayList<Credentials> cred) {
        int myId =duc.getUserCreds().getMemberId();
        Uri friendUri = new Uri.Builder()
                .scheme("https")
                .authority(Constants.BASE_END_POINT_URL)
                .appendPath("friend-status")
                .build();
        for (int i = 0 ;i < cred.size(); i++) {
            int searchResultID = cred.get(i).getMemberId();


                JSONObject msg = new JSONObject();
                try {
                    msg.put("userAId", myId);
                    msg.put("userBId", searchResultID);

                }catch (JSONException e) {
                    Log.wtf("CREDENTIALS", "Error: " + e.getMessage());
                }
                currentCred = cred.get(i);

            new SendPostAsyncTask.Builder(friendUri.toString(), msg)
                        .onPostExecute(this::handleRelationshipOnPost)
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

            duc.getUserCreds().setMemberId(resultsJSON.getInt("loggedInMemeberId"));

            searchResult = new ArrayList<>();


            Constants.temporaryCreds.clear();

            for (int i = 0; i< data.length(); i++) {
                JSONObject c = data.getJSONObject(i);

                Credentials cred = new Credentials.Builder("","")
                        .addFirstName(c.getString("firstName"))
                        .addLastName(c.getString("lastName"))
                        .addNickName(c.getString("nickname"))
                        .addMemberId(c.getInt("memberid"))
                        .build();

                Constants.temporaryCreds.add(cred);
                currentCred = cred;

                Uri friendUri = new Uri.Builder()
                        .scheme("https")
                        .authority(Constants.BASE_END_POINT_URL)
                        .appendPath("friend-status")
                        .build();


                JSONObject msg = new JSONObject();
                try {
                    msg.put("userAId", this.duc.getUserCreds().getMemberId());
                    msg.put("userBId", cred.getMemberId());

                }catch (JSONException e) {
                    Log.wtf("CREDENTIALS", "Error: " + e.getMessage());
                }


                new SendPostAsyncTask.Builder(friendUri.toString(), msg)
                        .onPostExecute(this::handleRelationshipOnPost)
                        .onCancelled(this::handleErrorsInTask)
                        .build().execute();



//

                //searchResult.add(cred);

            }

            //getFriendStatus(Constants.temporaryCreds);

            System.out.println("TEMPERRAYYY CREDDDDDD " + Constants.temporaryCreds.size());


           //Constants.searchResults = searchResult;



            loadFragment(duc.getNewFriendFragment());


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void handleRelationshipOnPost(String result) {


        try {
            JSONObject resultsJSON = new JSONObject(result);

            int relationship = resultsJSON.getInt("status");

            System.out.println("RELATIONSHIP " + relationship);
            searchResult.add(new FriendStatus(currentCred, 1));
            View v = getView().findViewById(R.id.list);
            Button b = v.findViewById(R.id.cardlayout).findViewById(R.id.constraintinsidecard).findViewById(R.id.addbtn);

           View test = this.inflater.inflate(R.layout.fragment_friends_list,container,false);

            if (relationship == 1) {

            } else if (relationship == 2) {
                b.setBackgroundResource(R.drawable.ic_check_circle_green_24dp);
                System.out.println("ALREADY FRIEND");

            } else if (relationship == 3) {
                b.setBackgroundResource(R.drawable.ic_pending_black_24dp);
            } else if (relationship == 4){
                b.setBackgroundResource(R.drawable.ic_accept_green_24dp);
            } else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Constants.searchResults = searchResult;

    }

    private void testMethod() {
        System.out.println("BUTTON ACCESSED");
    }


}
