package group3.tcss450.uw.edu.groupappproject.fragments;

import android.net.Uri;
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
import group3.tcss450.uw.edu.groupappproject.fragments.FriendsFragment.OnListFragmentInteractionListener;
//import group3.tcss450.uw.edu.groupappproject.dummy.DummyContent.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.FriendStatus;

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

    public MyFriendsRecyclerViewAdapter(List<Credentials> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.duc = Constants.dataUtilityControl;
        System.out.println();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friends, parent, false);
        mAddFriendButton = view.findViewById(R.id.addbtn);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getFirstName()+ " "+ mValues.get(position).getLastName());
        holder.mContentView.setText(mValues.get(position).getNickName());
        FriendStatus currentNode =  Constants.searchResults.get(position);

        //holder.mIdView.setText(currentNode.getCred().getFirstName() + " " + Constants.searchResults.get(position).getCred().getLastName());
        //holder.mContentView.setText(currentNode.getCred().getNickName());
        System.out.println("---------------------------------- MEMBER ID: " + duc.getUserCreds().getMemberId());

        if (Constants.searchResults.get(position).getRelationship() == 1) {
            mAddFriendButton.setBackgroundResource(R.drawable.ic_add_circle_outline_red_24dp);
        } else if(Constants.searchResults.get(position).getRelationship() == 2) {
            mAddFriendButton.setBackgroundResource(R.drawable.ic_check_circle_green_24dp);
        } else if (Constants.searchResults.get(position).getRelationship() == 3) {
            mAddFriendButton.setBackgroundResource(R.drawable.ic_pending_black_24dp);
        } else if (Constants.searchResults.get(position).getRelationship() == 4) {
            mAddFriendButton.setBackgroundResource(R.drawable.ic_accept_green_24dp);
        }
        mAddFriendButton.setOnClickListener(view -> onClick(position));
    }

    public void onClick(int position) {
        Uri addFriendUri = this.duc.getAddFriendEndPointURI();
        JSONObject msg = new JSONObject();
        try {
            msg.put("userAId", duc.getUserCreds().getMemberId());
            msg.put("userBId", mValues.get(position).getMemberId());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }
        System.out.println("STH CLICKED   " + position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Credentials mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.nickname_result);
            mContentView = (TextView) view.findViewById(R.id.fullname_result);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
