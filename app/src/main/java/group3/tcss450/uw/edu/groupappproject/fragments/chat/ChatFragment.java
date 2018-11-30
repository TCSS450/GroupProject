package group3.tcss450.uw.edu.groupappproject.fragments.chat;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private static final String TAG = "CHAT_FRAG";
    //private static final String CHAT_ID = "7";
    private EditText mMessageInputEditText;
    private String mEmail;
    private String mSendUrl;
    private Credentials[] mMembers;
    private String mGetUrl;
    private String oldMessages[];
    private String nickName;
    private DataUtilityControl duc;
//    private FirebaseMessageReciever mFirebaseMessageReciever;

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    int newChatId;
    protected static int mChatId;
    private OnChatFragmentListener mListener;

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

        mMessageInputEditText = rootLayout.findViewById(R.id.edit_chat_message_input);
        //assignName(this.duc.getUserCreds().getNickName());
        this.duc = Constants.dataUtilityControl;
        Bundle bundle = this.getArguments();
        newChatId = bundle.getInt("chatId");
        mChatId = newChatId;
        String chatName = bundle.getString("chatName");
        if (chatName.length() > 30) {
            chatName = chatName.substring(0, 30) + "...";
        }
        TextView chatNameTextView = rootLayout.findViewById(R.id.textView_chat_chatName);
        chatNameTextView.setText(chatName);
        mMembers = (Credentials[]) bundle.getSerializable("members");
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
                Log.d("Send result", result);
                System.out.println("Message SENT");
                mListener.onSendMessage();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChatFragmentListener) {
            mListener = (OnChatFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChatFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     */
    public interface OnChatFragmentListener
    {
        public void onSendMessage();
    }
}
