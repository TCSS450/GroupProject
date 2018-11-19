package group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends.ViewFriends.OnListFragmentInteractionListener;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;


import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ViewFriends} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyViewFriendsRecyclerViewAdapter extends RecyclerView.Adapter<MyViewFriendsRecyclerViewAdapter.ViewHolder> {

    private final List<Credentials> mValues;
    private final OnListFragmentInteractionListener mListener;
    private DataUtilityControl duc;


    public MyViewFriendsRecyclerViewAdapter(List<Credentials> creds,
                                            OnListFragmentInteractionListener listener) {
        mValues = creds;
        mListener = listener;
        Constants.chatCheckBoxes = new ArrayList<>();
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
        String s = holder.mNickname.getText().toString() + ": " + mValues.get(position).getNickName();
        holder.mNickname.setText(s);
        s = holder.mFullName.getText().toString() + ": " + mValues.get(position).getFirstName()
                                                   + " " + mValues.get(position).getLastName();
        holder.mFullName.setText(s);
        holder.mPhoneNumber.setText(mValues.get(position).getPhoneNumber());
        holder.mEmail.setText(mValues.get(position).getEmail());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onFriendListFragmentInteraction(holder.mCreds);
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
        public final CheckBox mCheckBox;
        public Credentials mCreds;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNickname = (TextView) view.findViewById(R.id.textView_viewfriends_nickname);
            mFullName = (TextView) view.findViewById(R.id.textView_viewfriends_fullname);
            mPhoneNumber = (TextView) view.findViewById(R.id.textView_viewfriends_phoneNumber);
            mEmail = (TextView) view.findViewById(R.id.textView_viewFriends_email);
            mCheckBox = (CheckBox) view.findViewById(R.id.checkBox_viewFriends_add);
            Constants.chatCheckBoxes.add(mCheckBox);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mFullName.getText() + "'";
        }
    }
}
