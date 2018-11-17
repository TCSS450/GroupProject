package group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group3.tcss450.uw.edu.groupappproject.R;


import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ViewFriendsItemContent} and makes a call to the
 * specified {@link ViewFriendsFragment.OnViewFriendsListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyViewFriendsRecyclerViewAdapter extends RecyclerView.Adapter<MyViewFriendsRecyclerViewAdapter.ViewHolder> {

    private final List<ViewFriendsItemContent> mValues;
    private final ViewFriendsFragment.OnViewFriendsListFragmentInteractionListener  mListener;

    public MyViewFriendsRecyclerViewAdapter(List<ViewFriendsItemContent> items,
                                            ViewFriendsFragment.OnViewFriendsListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_viewfriends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mViewFriendsItem = mValues.get(position);
        holder.mNickname.setText(mValues.get(position).getNickName());
        holder.mFirstName.setText(mValues.get(position).getFirstName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.viewFriendsListItemclicked(holder.mViewFriendsItem);
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
        public final TextView mNickname;
        public final TextView mFirstName;
        public ViewFriendsItemContent mViewFriendsItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNickname = (TextView) view.findViewById(R.id.viewfriends_item_nickname);
            mFirstName = (TextView) view.findViewById(R.id.viewfriends_item_fname);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mFirstName.getText() + "'";
        }
    }
}
