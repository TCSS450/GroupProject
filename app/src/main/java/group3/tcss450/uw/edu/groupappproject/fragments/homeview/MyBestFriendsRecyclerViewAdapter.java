package group3.tcss450.uw.edu.groupappproject.fragments.homeview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.homeview.BestFriendsFragment.OnBestFriendInteractionListener;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Credentials} and makes a call to the
 * specified {@link OnBestFriendInteractionListener}.
 *
 *
 */
public class MyBestFriendsRecyclerViewAdapter extends RecyclerView.Adapter<MyBestFriendsRecyclerViewAdapter.ViewHolder> {

    private final List<Credentials> mValues;
    private final OnBestFriendInteractionListener mListener;

    public MyBestFriendsRecyclerViewAdapter(List<Credentials> items, OnBestFriendInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_bestfriends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItemCredentials = mValues.get(position);
        holder.mNickName.setText(mValues.get(position).getNickName());
        holder.mFirstName.setText(mValues.get(position).getFirstName());
        holder.mLastName.setText(mValues.get(position).getLastName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
//                    mListener.onWeatherListFragmentInteraction(holder.mItemCredentials);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNickName;
        public final TextView mFirstName;
        public final TextView mLastName;
        public Credentials mItemCredentials;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNickName = (TextView) view.findViewById(R.id.item_BestFriend_NickName);
            mFirstName = (TextView) view.findViewById(R.id.item_bestFriend_FName);
            mLastName = (TextView) view.findViewById(R.id.item_bestFriend_LName);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNickName.getText() + "'";
        }
    }
}
