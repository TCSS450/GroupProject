package group3.tcss450.uw.edu.groupappproject.fragments.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.MessageFromJsonString;

/**
 *
 */
public class MessagesListFragment extends Fragment {

    private static final String ARG_MESSAGES_STRING = "column-count";
    private String mMessagesJsonArray = "";
    private List<MessageFromJsonString> messagesList;

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

            try {
                JSONArray messagesArr = new JSONArray(mMessagesJsonArray);
                // fill the list of message objects
                messagesList = new ArrayList<>();
                Log.d("Messages List arr", messagesArr.toString());
                for (int i = 0; i < messagesArr.length(); i++) {
//                    Log.d("Chat Frag json arr item", messagesArr.getString(i));
                    MessageFromJsonString temp = new MessageFromJsonString(messagesArr.getString(i));
                    messagesList.add(temp);
//                    Log.d("Message MessageFromJsonString", temp.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myitems_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            Log.d("Message List onCreateView", messagesList.toString());
            recyclerView.setAdapter(new MessageListAdapter(context, messagesList));
        }
        return view;
    }

}
