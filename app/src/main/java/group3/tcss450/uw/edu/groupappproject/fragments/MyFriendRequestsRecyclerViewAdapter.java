package group3.tcss450.uw.edu.groupappproject.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.FriendRequestsFragment.OnListFragmentInteractionListener;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Credentials} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyFriendRequestsRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendRequestsRecyclerViewAdapter.ViewHolder> {

    private final List<Credentials> mValues;
    private final OnListFragmentInteractionListener mListener;
    private DataUtilityControl duc;

    public MyFriendRequestsRecyclerViewAdapter(List<Credentials> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friendrequests, parent, false);
        this.duc = Constants.dataUtilityControl;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mValues != null) {
            holder.mCredentials = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getFirstName() + " " + mValues.get(position).getLastName());
            holder.mContentView.setText(mValues.get(position).getNickName());
        }
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Credentials mCredentials;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.textView_received_fullName);
            mContentView = (TextView) view.findViewById(R.id.textView_received_nickname);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}