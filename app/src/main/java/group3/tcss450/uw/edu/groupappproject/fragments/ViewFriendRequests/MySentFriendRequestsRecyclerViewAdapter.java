package group3.tcss450.uw.edu.groupappproject.fragments.ViewFriendRequests;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriendRequests.SentFriendRequestsFragment.OnListFragmentInteractionListener;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Credentials} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MySentFriendRequestsRecyclerViewAdapter extends RecyclerView.Adapter<MySentFriendRequestsRecyclerViewAdapter.ViewHolder> {

    private final List<Credentials> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MySentFriendRequestsRecyclerViewAdapter(List<Credentials> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sentfriendrequests, parent, false);
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
            mIdView = (TextView) view.findViewById(R.id.textView_sent_fullName);
            mContentView = (TextView) view.findViewById(R.id.textView_sent_nickname);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
