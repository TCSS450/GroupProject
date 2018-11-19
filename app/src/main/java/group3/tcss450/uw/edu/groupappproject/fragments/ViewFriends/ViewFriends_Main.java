package group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends;

import android.content.Context;
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
    private Button mButton;
    private EditText mChatName;
    private OnFragmentInteractionListener mListener;

    public ViewFriends_Main() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_viewfriends_main, container, false);
        this.duc = Constants.dataUtilityControl;
        mChatName = v.findViewById(R.id.editText_viewFriends_chatname);
        mButton = v.findViewById(R.id.button_viewFriendsMain_startChat);
        mButton.setOnClickListener(view -> startChat());
        return v;
    }

    public void startChat() {
        ArrayList<Integer> members = new ArrayList<>();
        for (int i = 0; i < Constants.chatCheckBoxes.size(); i++) {
            if (Constants.chatCheckBoxes.get(i).isChecked()) {
                members.add(Constants.myFriends.get(i).getMemberId());
            }
        }
        String[] membersArray = new String[members.size()];
        for (int i = 0; i < members.size(); i++) {
            membersArray[i] = Integer.toString(members.get(i));
        }
        Uri createChatURI = this.duc.getCreateChatURI();
        JSONObject msg = new JSONObject();
        try {
            msg.put("chatmembers", new JSONArray(membersArray));
            if (mChatName.getText().length() == 0) {
                msg.put("chatname", "Friends Chat");
            } else {
                msg.put("chatname", mChatName.getText().toString());
            }
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }
        new SendPostAsyncTask.Builder(createChatURI.toString(), msg)
                .onPostExecute(this::handleCreateChatOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
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

    private void handleCreateChatOnPost(String result) {
        /*  1 - Success! ChatId is created.
            2 - Error
        */
        try {
            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            ArrayList<Credentials> searchResult = new ArrayList<>();
            int status = resultsJSON.getInt("status");
            if (status == 1) {
                String chatId = resultsJSON.getString("chatid");
                System.out.println(chatId);
                mListener.onStartChatFragmentInteraction(chatId);
            } else {
                duc.makeToast(getActivity(), getString(R.string.request_error));
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
            duc.makeToast(getActivity(), getString(R.string.request_error));
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onStartChatFragmentInteraction(String chatId);
    }
}