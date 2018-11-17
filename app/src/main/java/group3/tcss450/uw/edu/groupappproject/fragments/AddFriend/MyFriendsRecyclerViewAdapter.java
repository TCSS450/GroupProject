package group3.tcss450.uw.edu.groupappproject.fragments.AddFriend;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.activities.HomeActivity;
import group3.tcss450.uw.edu.groupappproject.fragments.AddFriend.FriendsFragment.OnListFragmentInteractionListener;
//import group3.tcss450.uw.edu.groupappproject.dummy.NameThisBetter.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Credentials} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyFriendsRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendsRecyclerViewAdapter.ViewHolder>{

    private final List<Credentials> mValues;
    private final OnListFragmentInteractionListener mListener;
    private DataUtilityControl duc;
    private Button mAddFriendButton;
    private Context context;
    private List<Button> buttons;
    private Integer currentPosition;

    public MyFriendsRecyclerViewAdapter(List<Credentials> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.duc = Constants.dataUtilityControl;
        buttons = new ArrayList<>();
        currentPosition = 0;
        System.out.println();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friends, parent, false);
        mAddFriendButton = view.findViewById(R.id.addbtn);
        buttons.add(mAddFriendButton);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getFirstName()+ " "+ mValues.get(position).getLastName());
        holder.mContentView.setText(mValues.get(position).getNickName());

        int relationship = Constants.searchResults.get(position).getRelationship();
        if (relationship == 1) {
            buttons.get(position).setBackgroundResource(R.drawable.ic_add_circle_outline_red_24dp);
            buttons.get(position).setOnClickListener(v-> onClick(position, 1));
        } else if(relationship == 2) {
            buttons.get(position).setBackgroundResource(R.drawable.ic_check_circle_green_24dp);
            buttons.get(position).setOnClickListener(v-> onClick(position, 2));
        } else if (relationship == 3) {
            buttons.get(position).setBackgroundResource(R.drawable.ic_pending_black_24dp);
            buttons.get(position).setOnClickListener(v-> onClick(position, 3));
        } else if (relationship == 4) {
            buttons.get(position).setBackgroundResource(R.drawable.ic_accept_green_24dp);
            buttons.get(position).setOnClickListener(v-> onClick(position, 4));
        }
    }

    public void onClick(int position, int clickBehavior) {
        System.out.println(position);
        if (clickBehavior == 1) { // add friend behavior
            JSONObject msg = new JSONObject();
            try {
                msg.put("userAId", this.duc.getUserCreds().getMemberId());
                msg.put("userBId", Constants.searchResults.get(position).getCred().getMemberId());
            } catch (JSONException e) { e.printStackTrace(); }
            currentPosition = position;
            new SendPostAsyncTask.Builder(this.duc.getAddFriendEndPointURI().toString(), msg)
                    .onPostExecute(this::handleAddFriendOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
            //duc.makeShortToast(context, "Add friend");
        } else if (clickBehavior == 2) { // already friend behavior
            duc.makeShortToast(context, "You are already friends with this user");
        } else if (clickBehavior == 3) { // pending friend request behavior
            duc.makeShortToast(context, "They must accept your already sent request");
        } else if (clickBehavior == 4) { // accept friend request behavior
            loadFragment(this.duc.getFriendRequests());
        }
    }

    private void loadFragment(Fragment frag) {
        HomeActivity myActivity = (HomeActivity) context;
        FragmentTransaction transaction =
                myActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.homeActivityFrame, frag)
                        .addToBackStack(null);
        transaction.commit();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void handleAddFriendOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            if (status == 1) { // friend request was sent successfully
                buttons.get(currentPosition).setBackgroundResource(R.drawable.ic_pending_black_24dp);
                buttons.get(currentPosition).setOnClickListener(v -> onClick(currentPosition, 3));
                this.duc.makeShortToast(context, "Request sent!");
                /** FireBase code here???? **/
            } else {
                this.duc.makeShortToast(context, "Error request not sent!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Credentials mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView =  view.findViewById(R.id.nickname_result);
            mContentView =  view.findViewById(R.id.fullname_result);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

}
