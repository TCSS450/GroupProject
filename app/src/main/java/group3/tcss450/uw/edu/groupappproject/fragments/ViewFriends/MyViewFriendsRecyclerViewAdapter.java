package group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends.ViewFriends.OnListFragmentInteractionListener;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;


import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ViewFriends} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyViewFriendsRecyclerViewAdapter extends RecyclerView.Adapter<MyViewFriendsRecyclerViewAdapter.ViewHolder> {

    private final List<Credentials> mValues;
    private List<ImageButton> mDeleteButtons;
    private final OnListFragmentInteractionListener mListener;
    private List<TextView> mStatusTexts;
    private DataUtilityControl duc;
    private Integer mCurrentPosition;


    public MyViewFriendsRecyclerViewAdapter(List<Credentials> creds,
                                            OnListFragmentInteractionListener listener) {
        mValues = creds;
        mListener = listener;
        this.duc = Constants.dataUtilityControl;
        Constants.chatCheckBoxes = new ArrayList<>();
        mDeleteButtons = new ArrayList<>();
        mStatusTexts = new ArrayList<>();
        mCurrentPosition = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_viewfriends, parent, false);
        TextView status = view.findViewById(R.id.textView_viewFriend_DeleteFriend);
        mDeleteButtons.add(view.findViewById(R.id.imageButton_deleteFriend));
        mStatusTexts.add(status);
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
        mDeleteButtons.get(position).setOnClickListener(view -> onClickDeleteFriend(position));
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

    public void onClickDeleteFriend(int position) {
        Uri deleteFriendUri = this.duc.getAcceptFriendURI();
        JSONObject msg = new JSONObject();
        mCurrentPosition = position;
        try {
            msg.put("userAId", duc.getUserCreds().getMemberId());
            msg.put("userBId", mValues.get(position).getMemberId());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }

        new SendPostAsyncTask.Builder(deleteFriendUri.toString(), msg)
                .onPreExecute(this::handleOnPre)
                .onPostExecute(this::handleDeleteFriendOnPost)
                .build().execute();
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
        public final TextView mDeleteStatus;
        public final ImageButton mImageButton;
        public Credentials mCreds;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNickname = (TextView) view.findViewById(R.id.textView_viewfriends_nickname);
            mFullName = (TextView) view.findViewById(R.id.textView_viewfriends_fullname);
            mPhoneNumber = (TextView) view.findViewById(R.id.textView_viewfriends_phoneNumber);
            mEmail = (TextView) view.findViewById(R.id.textView_viewFriends_email);
            mCheckBox = (CheckBox) view.findViewById(R.id.checkBox_viewFriends_add);
            mImageButton = (ImageButton) view.findViewById(R.id.imageButton_deleteFriend);
            mDeleteStatus = (TextView) view.findViewById(R.id.textView_viewFriend_DeleteFriend);
            Constants.chatCheckBoxes.add(mCheckBox);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mFullName.getText() + "'";
        }
    }

    private void handleOnPre() {
        mDeleteButtons.get(mCurrentPosition).setClickable(false);
    }

    private void handleDeleteFriendOnPost(String result) {
        /*1- Delete Friend Successful
        2- Error
        */
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            if (status == 1) {
                mDeleteButtons.get(mCurrentPosition).setVisibility(View.INVISIBLE);
                mStatusTexts.get(mCurrentPosition).setText(R.string.viewFriends_deleteFriend_done);
                mStatusTexts.get(mCurrentPosition).setVisibility(View.VISIBLE);
            }  else {
                mDeleteButtons.get(mCurrentPosition).setVisibility(View.INVISIBLE);
                mStatusTexts.get(mCurrentPosition).setText(R.string.request_error);
                mStatusTexts.get(mCurrentPosition).setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }
}
