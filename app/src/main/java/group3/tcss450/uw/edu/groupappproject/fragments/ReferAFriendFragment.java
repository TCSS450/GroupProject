package group3.tcss450.uw.edu.groupappproject.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.MyFirebaseMessagingService;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReferAFriendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReferAFriendFragment#} factory method to
 * create an instance of this fragment.
 */
public class ReferAFriendFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private DataUtilityControl duc;
    private EditText mFriendEmail;
    private TextView mResponse;
    private FirebaseMessageReciever mFirebaseMessageReciever;

    public ReferAFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_refer_afriend, container, false);
        duc = Constants.dataUtilityControl;
        mFriendEmail = v.findViewById(R.id.editText_referFriend_email);
        mResponse = v.findViewById(R.id.textView_refer_a_friend_response);

        Button b = v.findViewById(R.id.button_referFriend_sendInvitation);
        b.setOnClickListener(this::sendInvite);
        return v;
    }

    private void sendInvite(View v) {
        if (mFriendEmail.length() == 0) {
            mFriendEmail.setError(getString(R.string.empty));
        } else if (!mFriendEmail.getText().toString().contains("@")) {
            mFriendEmail.setError(getString(R.string.invalidEmail));
        } else {
            Uri getFriendURI = duc.getFriendReferralURI();
            JSONObject msg = new JSONObject();
            try {
                msg.put("email", mFriendEmail.getText().toString());
                msg.put("memberid", duc.getUserCreds().getMemberId());
            } catch (JSONException e) {
                Log.d("Error", e.toString());
            }
            new SendPostAsyncTask.Builder(getFriendURI.toString(), msg)
                    .onPreExecute(this::handleInviteOnPre)
                    .onPostExecute(this::handleGetChatsOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
    }

    private void handleGetChatsOnPost(String result) {
        /*1- Invitation Sent!
        2- Email already exists in Database and is verified (user is active)
        3- Email already exists in Database and is unverified (user is inactive)
        4- Incorrect Input to endpoint / any other error
        */
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");

            if (status == 1) { // success, sends email
                mListener.onWaitFragmentInteractionHide();
                mResponse.setText(R.string.referFriend_inviteSent);
            }  else if (status == 2) { // email exists with Verification
                mListener.onWaitFragmentInteractionHide();
                duc.makeToast(getActivity(), getString(R.string.referFriend_friend_active));
                mListener.sendToAddFriendPage(mFriendEmail.getText().toString());
            } else if (status == 3) { //Email already exists without verification.
                mListener.onWaitFragmentInteractionHide();
                mResponse.setText(R.string.referFriend_friend_inactive);
            } else {
                mListener.onWaitFragmentInteractionHide();
                duc.makeToast(getActivity(), getString(R.string.request_error));
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            duc.makeToast(getActivity(), getString(R.string.request_error));
        }
    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void handleInviteOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener extends WaitFragment.OnWaitFragmentInteractionListener {
        void sendToAddFriendPage(String theEmail);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFirebaseMessageReciever == null) {
            mFirebaseMessageReciever = new FirebaseMessageReciever();
        }
        IntentFilter iFilter = new IntentFilter(MyFirebaseMessagingService.RECEIVED_NEW_MESSAGE);
        getActivity().registerReceiver(mFirebaseMessageReciever, iFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mFirebaseMessageReciever != null){
            getActivity().unregisterReceiver(mFirebaseMessageReciever);
        }
    }

    /**
     * A BroadcastReceiver setup to listen for messages sent from
     * MyFirebaseMessagingService
     * that Android allows to run all the time.
     */
    private class FirebaseMessageReciever extends BroadcastReceiver {
        //String duc.DataUtilityControl
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("FCM Chat Frag", "start onRecieve");
            if (intent.hasExtra("DATA")) {
                String data = intent.getStringExtra("DATA");
                Log.d("Message data", data);
                try {
                    JSONObject jObj = new JSONObject(data);
                    if (jObj.has("type")) {
                        if (jObj.getString("type").equals("contact")) {
                            String contact = jObj.getString("sender");
                            String message = jObj.getString("message");
                            duc.makeToast(getActivity(), "New Message from " + contact + " \nMessage: " + message);
                        } else if (jObj.getString("type").equals("sent")) {
                            String contact = jObj.getString("senderString") ;
                            duc.makeToast(getActivity(), "New Friend Request from " + contact);
                        } else if (jObj.getString("type").equals("accepted")) {
                            String contact = jObj.getString("senderString");
                            duc.makeToast(getActivity(), contact + " accepted your friend request");
                        }
                    }
                } catch (JSONException e) {
                    Log.e("JSON PARSE", e.toString());
                }
            }
        }
    }
}
