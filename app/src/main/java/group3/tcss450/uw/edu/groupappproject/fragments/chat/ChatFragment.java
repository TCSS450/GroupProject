package group3.tcss450.uw.edu.groupappproject.fragments.chat;

import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.MyFirebaseMessagingService;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;
import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private static final String TAG = "CHAT_FRAG";
    //private static final String CHAT_ID = "7";
    private EditText mMessageInputEditText;
    //private TextView mType;
    private String mEmail;
    private String mSendUrl;
    private Credentials[] mMembers;
    private String mTheOneTyping;
    private String oldMessages[];
    private int memberId;
    private  String nickname;
    private DataUtilityControl duc;
    private GifImageView mGif;

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private FirebaseMessageReciever mFirebaseMessageReciever;
    private TextView mWhoseTypingTextView;
    private ImageButton mAddToChatButton;
    private ImageButton mLeaveChatButton;
    int newChatId;
    protected static int mChatId;
    private Map<String, String> mPeopleTalking;

    private OnFragmentInteractionListener mListener;

    //private String nickName;
    public ChatFragment() {
        //System.out.println("THE NICKNAME IS FINALLY " + duc);
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootLayout = inflater.inflate(R.layout.fragment_chat, container, false);

        //set up the recycler view
        RecyclerView mMessageRecycler = (RecyclerView) rootLayout.findViewById(R.id.chatFrag_message_recycler);

        // Set up variables/textViews/buttons/etc.
        mGif = rootLayout.findViewById(R.id.gifImageView);
        mGif.setVisibility(View.INVISIBLE);
        mAddToChatButton = rootLayout.findViewById(R.id.imageButton_chat_addFriend);
        mAddToChatButton.setOnClickListener(this::addFriendToChat);
        mAddToChatButton = rootLayout.findViewById(R.id.imageButton_chat_leave);
        mMessageInputEditText = rootLayout.findViewById(R.id.edit_chat_message_input);
        this.duc = Constants.dataUtilityControl;
        mPeopleTalking = new HashMap<>();

        // Get Chat ID from the bundle
        Bundle bundle = this.getArguments();
        newChatId = bundle.getInt("chatId");
        if (getArguments() != null) {
            mMembers = (Credentials[]) bundle.getSerializable("members");
        }
        mChatId = newChatId;
        String chatName = bundle.getString("chatName");
        if (chatName.length() > 30) {
            chatName = chatName.substring(0, 30) + "...";
        }
        TextView chatNameTextView = rootLayout.findViewById(R.id.textView_chat_chatName);
        mWhoseTypingTextView = rootLayout.findViewById(R.id.is_typing_name);
        chatNameTextView.setText(chatName);
        memberId = duc.getUserCreds().getMemberId();
        nickname = duc.getUserCreds().getNickName();
        System.out.println("----------------------NEW CHAT ID " + newChatId + "---------------------------");
        mMessageInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //System.out.println("STEP 0 DONE");

//                startTyping();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mMessageInputEditText.getText().length() > 0) {
                    startTyping();
                } else {
                    stopTyping();
                }
            }
        });

        String mGetUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_get_all))
                .build()
                .toString();

        JSONObject messageGetJson = new JSONObject();
        try {
            messageGetJson.put("chatId", newChatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(mGetUrl, messageGetJson)
                .onPostExecute(this::endOfGetMsgTask)
                .onCancelled(error -> Log.e(TAG, error))
                .build().execute();

        rootLayout.findViewById(R.id.button_chat_send).setOnClickListener(this::handleSendClick);
        return rootLayout;
    }

    private void addFriendToChat(View v) {
        mListener.onGoToFriendsFragmentInteraction(mMembers);
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

    private void startTyping(){
        //System.out.println("STEP 1 DONE");
        String mTypingUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_is_typing))
                .build()
                .toString();

        JSONObject messageTypeJson = new JSONObject();
        try {
            messageTypeJson.put("chatid", newChatId);
            if (duc.getUserCreds().getDisplayPref() == 1) {
                messageTypeJson.put("membername", duc.getUserCreds().getNickName());
            } else if (duc.getUserCreds().getDisplayPref() == 2) {
                messageTypeJson.put("membername", duc.getUserCreds().getFullName());
            } else {
                messageTypeJson.put("membername", duc.getUserCreds().getEmail());
            }
            messageTypeJson.put("memberid", duc.getUserCreds().getMemberId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(mTypingUrl, messageTypeJson)
                .onPostExecute(this::endOfTypingMsgTask)
                .onCancelled(error -> Log.e(TAG, error))
                .build().execute();
        mTheOneTyping = duc.getmUserTyping();
    }

    private void stopTyping(){
        //System.out.println("STEP 1 DONE");
        String mTypingUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_done_typing))
                .build()
                .toString();

        JSONObject messageTypeJson = new JSONObject();
        try {
            messageTypeJson.put("chatid", newChatId);
            messageTypeJson.put("membername", duc.getUserCreds().getNickName());
            messageTypeJson.put("memberid", duc.getUserCreds().getMemberId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(mTypingUrl, messageTypeJson)
                .onPostExecute(this::endOfTypingMsgTask)
                .onCancelled(error -> Log.e(TAG, error))
                .build().execute();
        mTheOneTyping = duc.getmUserTyping();
    }

    private void endOfTypingMsgTask(final String result) {

        try {
            JSONObject typeJson = new JSONObject(result);
            String typingStatus = typeJson.getString("status");
            System.out.println("The typing status FIRST is: " + typingStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                Log.d("Send result", result);
                System.out.println("Message SENT");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //fill textview with previous logs
    private void endOfGetMsgTask(final String result) {
        try {

            //This is the result from the web service
            JSONObject resJson = new JSONObject(result);
            String oldText = resJson.getString("messages");
            //oldText.replace("email","");
            System.out.println(oldText);

            Log.d("Message Chat Frag json result", result);
            JSONArray messagesArr = resJson.getJSONArray("messages");

            insertNestedFragment(R.id.chatFrag_message_frame,
                    MessagesListFragment.newInstance(messagesArr.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertNestedFragment(int container, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(container, fragment).commit();
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

    /**
     * A BroadcastReceiver setup to listen for messages sent from
     * MyFirebaseMessagingService
     * that Android allows to run all the time.
     */
    private class FirebaseMessageReciever extends BroadcastReceiver    {
        //String duc.DataUtilityControl
        @Override
        public void onReceive(Context context, Intent intent) {


//            Log.i("FCM Chat Frag", "start onRecieve");
            if (intent.hasExtra("DATA")) {
                String data = intent.getStringExtra("DATA");
                Log.d("Message data", data);
                try {
                    JSONObject jObj = new JSONObject(data);
                    if (jObj.has("type")) {
                        if (jObj.getString("type").equals("typing")) {
                            System.out.println("MY CHAT ID + " + mChatId);
                            if (jObj.getString("chatid").equals(Integer.toString(mChatId))) {
                                String sender = jObj.getString("members");
                                String memberId = jObj.getString("memberid_whos_typing");
                                StringBuilder sb = new StringBuilder();
                                sb.append(sender);
                                mPeopleTalking.put(memberId, sender);
                                for (HashMap.Entry<String, String> entry : mPeopleTalking.entrySet()) {
                                    if (!entry.getKey().equals(memberId)) {
                                        sb.append(", and " + entry.getValue());
                                    }
                                }
                                System.out.println("SENTINAL: " + mPeopleTalking.toString());
                                if (sb.length() > 25) {
                                    String outPutString = sb.substring(0, 25) + " is typing.";
                                    mWhoseTypingTextView.setText(outPutString);
                                } else {
                                    sb.append(" is typing.");
                                    mWhoseTypingTextView.setText(sb.toString());
                                }
                                mGif.setVisibility(View.VISIBLE);

//                                Log.i("FCM Chat Frag", sender);
                            }
                        } else if (jObj.getString("type").equals("done-typing")) {
                            if (jObj.getString("chatid").equals(Integer.toString(mChatId))) {
                                String memberId = jObj.getString("memberid_whos_typing");
                                mPeopleTalking.remove(memberId);
                                StringBuilder sb = new StringBuilder();
                                if (mPeopleTalking.size() > 0) {
                                    for (HashMap.Entry<String, String> entry : mPeopleTalking.entrySet()) {
                                        if (!entry.getKey().equals(memberId)) {
                                            sb.append(", and " + entry.getValue());
                                        }
                                    }
                                    if (sb.length() > 25) {
                                        String outPutString = sb.substring(0, 25) + " is typing.";
                                        mWhoseTypingTextView.setText(outPutString);
                                        mGif.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    mWhoseTypingTextView.setText("");
                                    mGif.setVisibility(View.INVISIBLE);
                                }
                                System.out.println("SENTINAL: " + mPeopleTalking.toString());
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.e("JSON PARSE", e.toString());
                }
            }
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
        void onGoToFriendsFragmentInteraction(Credentials[] members);
    }

}
