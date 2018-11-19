package group3.tcss450.uw.edu.groupappproject.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.activities.HomeActivity;
import group3.tcss450.uw.edu.groupappproject.activities.MainActivity;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.MyFirebaseMessagingService;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private static final String TAG = "CHAT_FRAG";
    //private static final String CHAT_ID = "7";
    private TextView mMessageOutputTextView;
    private EditText mMessageInputEditText;
    private String mEmail;
    private String mSendUrl;
    private String mGetUrl;
    private String oldMessages[];
    private String nickName;
    private DataUtilityControl duc;
    private FirebaseMessageReciever mFirebaseMessageReciever;

    int newChatId;

    //private String nickName;
    public ChatFragment() {
        //System.out.println("THE NICKNAME IS FINALLY " + duc);
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootLayout = inflater.inflate(R.layout.fragment_chat, container, false);
        mMessageOutputTextView = rootLayout.findViewById(R.id.text_chat_message_display);
        mMessageOutputTextView.setMovementMethod(new ScrollingMovementMethod());
        mMessageInputEditText = rootLayout.findViewById(R.id.edit_chat_message_input);
        //assignName(this.duc.getUserCreds().getNickName());
        this.duc = Constants.dataUtilityControl;
        Bundle bundle = this.getArguments();
        newChatId = bundle.getInt("chatId");
        //String newNotifyId = getInt
        System.out.println("----------------------NEW CHAT ID " + newChatId + "---------------------------");
        //String prefName[] = new String[3];

        //System.out.println("The new chat id is " + newChatId);

        mGetUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_get_all))
                .build()
                .toString();

        JSONObject messageGetJson = new JSONObject();
        try {
            System.out.println("the notification without id is " + newChatId);
            messageGetJson.put("chatId", newChatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(mGetUrl, messageGetJson)
                .onPostExecute(this::endOfGetMsgTask)
                .onCancelled(error -> Log.e(TAG, error))
                .build().execute();


        //mMessageOutputTextView.setText(mGetUrl);
        rootLayout.findViewById(R.id.button_chat_send).setOnClickListener(this::handleSendClick);
        return rootLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (prefs.contains(getString(R.string.keys_prefs_email))) {
            mEmail = prefs.getString(getString(R.string.keys_prefs_email), "");
        } else {
            throw new IllegalStateException("No EMAIL in prefs!");
        }
        //We will use this url every time the user hits send. Let's only build it once, ya?
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_send))
                .build()
                .toString();
    }

    private void handleSendClick(final View theButton) {

        String msg = mMessageInputEditText.getText().toString();
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put("email", mEmail);
            messageJson.put("message", msg);
            messageJson.put("chatId", newChatId);
            //messageJson.put("nickname", nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(error -> Log.e(TAG, error))
                .build().execute();

    }
    //Clear input after message sent
    private void endOfSendMsgTask(final String result) {
        try {
            //This is the result from the web service
            JSONObject res = new JSONObject(result);
            if(res.has("success") && res.getBoolean("success")) {
                //The web service got our message. Time to clear out the input EditText
                mMessageInputEditText.setText("");
                //its up to you to decide if you want to send the message to the output here
                //or wait for the message to come back from the web service.
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //fill textview with previous logs
    private void endOfGetMsgTask(final String result) {
        try {

            //This is the result from the web service
            JSONObject res = new JSONObject(result);
            String oldText = res.getString("messages");
            //oldText.replace("email","");
            System.out.println(oldText);

            mMessageOutputTextView.setText(oldText);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
     MyFirebaseMessagingService
     * that Android allows to run all the time.
     */
    private class FirebaseMessageReciever extends BroadcastReceiver {
        //String duc.DataUtilityControl
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("FCM Chat Frag", "start onRecieve");

            if(intent.hasExtra("DATA")) {
                String data = intent.getStringExtra("DATA");
                JSONObject jObj = null;

                //JSONObject userPref
                try {
                    jObj = new JSONObject(data);
                    if(jObj.has("message") && jObj.has("sender")) {
                        String sender = jObj.getString("sender");
                        String msg = jObj.getString("message");

                        mMessageOutputTextView.setText(System.lineSeparator()+  mMessageOutputTextView.getText());
                        mMessageOutputTextView.setText(System.lineSeparator()+  mMessageOutputTextView.getText());
                        mMessageOutputTextView.setText(sender + ": " + msg +  mMessageOutputTextView.getText());
                        mMessageOutputTextView.setText(System.lineSeparator()+  mMessageOutputTextView.getText());
                        mMessageOutputTextView.setText(System.lineSeparator()+  mMessageOutputTextView.getText());
                        //System.out.println("THE SECOND PASS");
                        Log.i("FCM Chat Frag", sender + " " + msg);
                    }
                } catch (JSONException e) {
                    Log.e("JSON PARSE", e.toString());
                }
            }
        }
    }
}
