package group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends;

import android.content.Context;
import android.content.SharedPreferences;
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
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.WaitFragment;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

/**
 * A fragment representing a list of Items.
 *
 * Holds the user nickname and fname. JSON data returned from endpoint
 */
public class ViewFriendsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    public static final String VIEW_FRIENDS_ITEM_CONTENT_LIST = "View Friends item content list";
    private List<ViewFriendsItemContent> mViewFriendsItemContent;
    private OnViewFriendsListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ViewFriendsFragment() {
    }

//    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ViewFriendsFragment newInstance(int columnCount) {
        ViewFriendsFragment fragment = new ViewFriendsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get data here
        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);
        String email = prefs.getString(getString(R.string.keys_prefs_email), "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // initialize data for the list
        new SendPostAsyncTask.Builder(Constants.VIEW_FRIENDS_END_POINT_URL, jsonObject)
                .onPreExecute(this::handleViewFriendsOnPre)
                .onPostExecute(this::handleViewFriendsOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();

//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT); // was here
//            mViewFriendsItemContent = new ArrayList<ViewFriendsItemContent>(
//                    Arrays.asList((ViewFriendsItemContent[]) getArguments()
//                            .getSerializable(VIEW_FRIENDS_ITEM_CONTENT_LIST)));
//            Log.d("ViewFriendsFragment", "get arguments not null");
//        } else {
//
//            Log.d("ViewFriendsFragment", "get arguments is null!!");
//        }
    }


    private void handleViewFriendsOnPost(String result) {
        try {
            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean error = resultsJSON.getBoolean("error");
            if (!error) { // retrieved the list of friends
                Log.d("ViewFriendsFragment", "not an error");

                JSONArray friends = resultsJSON.getJSONArray("friends");
                ArrayList<ViewFriendsItemContent> friendsList = new ArrayList<>();

                if (friends.length() > 0) { //user has friends
                    for (int i = 0; i < friends.length(); i++) {
//                        Log.d("ViewFriendsFragment", String.valueOf(i));
                        JSONArray temp = friends.getJSONArray(i);
                        friendsList.add(new ViewFriendsItemContent
                            .Builder(temp.getString(2), temp.getString(0))
                            .addLastName(temp.getString(1))
                            .build());
                    }
                }

                Log.d("ViewFriendsFragment", "friendList" + friendsList.toString());


                mViewFriendsItemContent = new ArrayList<>(friendsList);
                //set the ARG_SET_LIST here
                Bundle bArgs = new Bundle();
                bArgs.putSerializable(ViewFriendsFragment.VIEW_FRIENDS_ITEM_CONTENT_LIST , mViewFriendsItemContent.toArray());

            } else { // error on retrieval
//                duc.makeToast(getContext(),
//                        getResources().getString(R.string.toast_error_unidentified_user));
                Log.d("ViewFriendsFragment", "error");
            }

        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: ");
        }
        mListener.onWaitFragmentInteractionHide();
    }

    private void handleViewFriendsOnPre() { mListener.onWaitFragmentInteractionShow(); }
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewfriends_list, container, false);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT); // was here
            mViewFriendsItemContent = new ArrayList<ViewFriendsItemContent>(
                    Arrays.asList((ViewFriendsItemContent[]) getArguments()
                            .getSerializable(VIEW_FRIENDS_ITEM_CONTENT_LIST)));
            Log.d("ViewFriendsFragment", "get arguments not null");
        } else {

            Log.d("ViewFriendsFragment", "get arguments is null!!");

        }

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyViewFriendsRecyclerViewAdapter(mViewFriendsItemContent, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnViewFriendsListFragmentInteractionListener) {
            mListener = (OnViewFriendsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    //I added
//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        // TODO implement some logic
//    }

    /**
     */
    public interface OnViewFriendsListFragmentInteractionListener
            extends WaitFragment.OnWaitFragmentInteractionListener
    {
        void viewFriendsListItemclicked(ViewFriendsItemContent item);
    }
}
