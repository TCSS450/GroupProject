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
//import group3.tcss450.uw.edu.groupappproject.dummy.NameThisBetter.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

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
        System.out.println("---------------------------------- MEMBER ID: " + duc.getUserCreds().getMemberId());
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
