package group3.tcss450.uw.edu.groupappproject.fragments.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.MessageFromJsonString;
import group3.tcss450.uw.edu.groupappproject.utility.MyFirebaseMessagingService;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

/**
 * Hold the list of messages on the chat fragment.
 */
public class MessagesListFragment extends Fragment
{

    private static final String ARG_MESSAGES_STRING = "column-count";
    private String mMessagesJsonArray = "";
    private List<MessageFromJsonString> mMessagesList;
    private MessageListAdapter mMessageListAdapter;
//    private onMessagesListInteraction mListener;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private FirebaseMessageReciever mFirebaseMessageReciever;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessagesListFragment() {
    }

    public static MessagesListFragment newInstance(String jsonStringArray) {
        MessagesListFragment fragment = new MessagesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGES_STRING, jsonStringArray);
        Log.d("Message List static", jsonStringArray);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMessagesJsonArray = getArguments().getString(ARG_MESSAGES_STRING);
            parseGetAllMessages(mMessagesJsonArray);

        }
    }

    private void parseGetAllMessages(String jsonArray) {
//        if (mMessagesList != null)
//            mMessagesList.clear();
        try {
            JSONArray messagesArr = new JSONArray(jsonArray);
            // fill the list of message objects
            mMessagesList = new ArrayList<>();
            Log.d("Messages List arr", messagesArr.toString());
            for (int i = 0; i < messagesArr.length(); i++) {
//                    Log.d("Chat Frag json arr item", messagesArr.getString(i));
                MessageFromJsonString temp = new MessageFromJsonString(messagesArr.getString(i));
                mMessagesList.add(temp);
//                    Log.d("Message MessageFromJsonString", temp.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myitems_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;

            mLinearLayoutManager = new LinearLayoutManager(context);
            mLinearLayoutManager.setReverseLayout(true);

            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            mMessageListAdapter = new MessageListAdapter(context, mMessagesList);
//            mListener.notifyOfNewMessage(); // here
            Log.d("Message List onCreateView", mMessagesList.toString());
            mRecyclerView.setAdapter(mMessageListAdapter);
            mRecyclerView.scrollToPosition(0);
        }
        return view;
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
    private class FirebaseMessageReciever extends BroadcastReceiver

    {
        //String duc.DataUtilityControl
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("FCM Chat Frag", "start onRecieve");
            if (intent.hasExtra("DATA")) {
                String data = intent.getStringExtra("DATA");
                JSONObject jObj = null;
                Log.d("Message data", data);
                //JSONObject userPref
                try {
                    jObj = new JSONObject(data);
                    if (jObj.has("message") && jObj.has("sender")) {
                        String sender = jObj.getString("sender");
                        String msg = jObj.getString("message");
                        String chatId = jObj.getString("chatId");
                        System.out.println(chatId + " == " + ChatFragment.mChatId);
                        if (chatId.equals(Integer.toString(ChatFragment.mChatId))) {
                            JSONObject messageGetJson = new JSONObject();
                            try {
                                messageGetJson.put("chatId", ChatFragment.mChatId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new SendPostAsyncTask.Builder(Constants.GET_ALL_MESSAGES_URL, messageGetJson)
                                    .onPostExecute(this::endOfGetMsgTask)
                                    .build().execute();

                            Log.d("Message return", data);

                            //System.out.println("THE SECOND PASS");
                            Log.i("FCM Chat Frag", sender + " " + msg);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("JSON PARSE", e.toString());
                }
            }
        }

        private void endOfGetMsgTask(final String result) {
            try {

                //This is the result from the web service
                JSONObject resJson = new JSONObject(result);
                String oldText = resJson.getString("messages");
                //oldText.replace("email","");
                System.out.println(oldText);

                Log.d("Message Chat Frag json result", result);
                JSONArray messagesArr = resJson.getJSONArray("messages");
                Log.d("Refresh messages", messagesArr.get(0).toString());

                try {
                    MessageFromJsonString newMessage = new MessageFromJsonString(messagesArr.get(0).toString());
                    mMessageListAdapter.addMessage(newMessage);
                    mRecyclerView.scrollToPosition(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
