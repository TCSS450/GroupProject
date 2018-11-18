package group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends.ViewFriends.OnListFragmentInteractionListener;


import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ViewFriends} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyViewFriendsRecyclerViewAdapter extends RecyclerView.Adapter<MyViewFriendsRecyclerViewAdapter.ViewHolder> {

    private final List<Credentials> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyViewFriendsRecyclerViewAdapter(List<Credentials> creds,
                                            OnListFragmentInteractionListener listener) {
        mValues = creds;
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
        holder.mCreds = mValues.get(position);
        holder.mNickname.setText(mValues.get(position).getNickName());
        holder.mFullName.setText(mValues.get(position).getFirstName());
        holder.mPhoneNumber.setText(mValues.get(position).getPhoneNumber());
        holder.mEmail.setText(mValues.get(position).getEmail());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mCreds);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mValues != null) return mValues.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNickname;
        public final TextView mFullName;
        public final TextView mPhoneNumber;
        public final TextView mEmail;
        public Credentials mCreds;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNickname = (TextView) view.findViewById(R.id.textView_viewfriends_nickname);
            mFullName = (TextView) view.findViewById(R.id.textView_viewfriends_fullname);
            mPhoneNumber = (TextView) view.findViewById(R.id.textView_viewfriends_phoneNumber);
            mEmail = (TextView) view.findViewById(R.id.textView_viewfriends_email);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mFullName.getText() + "'";
        }
    }
}
