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
import java.util.List;

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
    private Credentials[] mFriends;

    public ViewFriends_Main() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_viewfriends_main, container, false);
        this.duc = Constants.dataUtilityControl;
        mChatName = v.findViewById(R.id.editText_viewfriends_chatname);
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
        members.add(duc.getUserCreds().getMemberId());
        int[] membersArray = new int[members.size()];

        for (int i = 0; i < members.size(); i++) {
            membersArray[i] = members.get(i);
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

        Uri getProfilesURI = this.duc.getProfilesEndPointURI();
        new SendPostAsyncTask.Builder(getProfilesURI.toString(), msg)
                .onPostExecute(this::handleGetProfilesOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();

        new SendPostAsyncTask.Builder(createChatURI.toString(), msg)
                .onPostExecute(this::handleCreateChatOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadFriendsFragment(new ViewFriends());
    }

    private void handleErrorsInTask(String result) {
        System.out.println("INSIDE ERRORS");
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBestFriendInteractionListener");
        }
    }

    private void handleGetProfilesOnPost(String result) {
        /*  1 - Success! ChatId is created.
            2 - Error
        */
        try {
            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);

            int status = resultsJSON.getInt("status");
            if (status == 1) {
                JSONArray data = resultsJSON.getJSONArray("profiles");
                ArrayList<Credentials> creds = new ArrayList<>();
                for(int i = 0; i < data.length(); i++) {
                    JSONObject jsonProfile = data.getJSONObject(i);
                    creds.add(new Credentials.Builder(jsonProfile.getString("email"), "")
                            .addMemberId(jsonProfile.getInt("memberid"))
                            .addFirstName(jsonProfile.getString("firstname"))
                            .addLastName(jsonProfile.getString("lastname"))
                            .addNickName(jsonProfile.getString("nickname"))
                            .addPhoneNumber(jsonProfile.getString("phone_number"))
                            .addDisplayPref(jsonProfile.getInt("display_type"))
                            .build());
                }
                mFriends = new Credentials[creds.size()];
                mFriends = creds.toArray(mFriends);
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

    private void handleCreateChatOnPost(String result) {
        /*  1 - Success! ChatId is created.
            2 - Error
        */
        try {
            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            if (status == 1) {
                int chatId = resultsJSON.getInt("chatid");

                if (mListener != null) {
                    mListener.onStartChatFragmentInteraction(chatId, mFriends);
                }
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
        void onStartChatFragmentInteraction(int chatId, Credentials[] friends);
    }
}
