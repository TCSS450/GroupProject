package group3.tcss450.uw.edu.groupappproject.fragments.ViewFriendRequests;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.ViewFriendRequests.FriendRequestsFragment.OnListFragmentInteractionListener;
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
public class MyFriendRequestsRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendRequestsRecyclerViewAdapter.ViewHolder> {

    private final List<Credentials> mValues;

    private final OnListFragmentInteractionListener mListener;
    private DataUtilityControl duc;
    private List<ImageButton> mAcceptButtons;
    private List<ImageButton> mRejectButtons;
    private List<TextView> mStatusTexts;
    private Integer mCurrentPosition;

    public MyFriendRequestsRecyclerViewAdapter(List<Credentials> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.duc = Constants.dataUtilityControl;
        mAcceptButtons = new ArrayList<>();
        mRejectButtons = new ArrayList<>();
        mStatusTexts = new ArrayList<>();
        mCurrentPosition = 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friendrequests, parent, false);
        ImageButton acceptButton = view.findViewById(R.id.imageButton_accept);
        ImageButton denyButton = view.findViewById(R.id.imageButton_deny);
        TextView mFriendStatusText = view.findViewById(R.id.textView_requests_status);
        mAcceptButtons.add(acceptButton);
        mRejectButtons.add(denyButton);
        mFriendStatusText.setVisibility(View.INVISIBLE);
        mStatusTexts.add(mFriendStatusText);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mValues != null) {
            holder.mCredentials = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getFirstName() + " " + mValues.get(position).getLastName());
            holder.mContentView.setText(mValues.get(position).getNickName());
            mAcceptButtons.get(position).setOnClickListener(view -> onClickAccept(position));
            mRejectButtons.get(position).setOnClickListener(view -> onClickDeny(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        }
        return 0;
    }

    public void onClickAccept(int position) {
        Uri acceptFriendUri = this.duc.getAcceptFriendURI();
        JSONObject msg = new JSONObject();
        mCurrentPosition = position;
        try {
            msg.put("userAId", duc.getUserCreds().getMemberId());
            msg.put("userBId", mValues.get(position).getMemberId());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }

        new SendPostAsyncTask.Builder(acceptFriendUri.toString(), msg)
                .onPreExecute(this::handleOnPre)
                .onPostExecute(this::handleAcceptOnPost)
                .build().execute();
    }

    public void onClickDeny(int position) {
        Uri denyFriendUri = this.duc.getRejectFriendURI();
        JSONObject msg = new JSONObject();
        mCurrentPosition = position;
        try {
            msg.put("userAId", duc.getUserCreds().getMemberId());
            msg.put("userBId", mValues.get(position).getMemberId());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }

        new SendPostAsyncTask.Builder(denyFriendUri.toString(), msg)
                .onPreExecute(this::handleOnPre)
                .onPostExecute(this::handleDenyOnPost)
                .build().execute();
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

    private void handleOnPre() {
        mAcceptButtons.get(mCurrentPosition).setClickable(false);
        mRejectButtons.get(mCurrentPosition).setClickable(false);
    }

    private void handleDenyOnPost(String result) {
        /*1- Rejection Successful
        2- Error
        */
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            if (status == 1) {
                mAcceptButtons.get(mCurrentPosition).setVisibility(View.INVISIBLE);
                mRejectButtons.get(mCurrentPosition).setVisibility(View.INVISIBLE);
                mStatusTexts.get(mCurrentPosition).setText(R.string.request_denied);
                mStatusTexts.get(mCurrentPosition).setVisibility(View.VISIBLE);
            }  else {
                mAcceptButtons.get(mCurrentPosition).setVisibility(View.INVISIBLE);
                mRejectButtons.get(mCurrentPosition).setVisibility(View.INVISIBLE);
                mStatusTexts.get(mCurrentPosition).setText(R.string.request_error);
                mStatusTexts.get(mCurrentPosition).setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    private void handleAcceptOnPost(String result) {
        /*  1- Accept Successful
            2- Error   */
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            if (status == 1) {
                mAcceptButtons.get(mCurrentPosition).setVisibility(View.INVISIBLE);
                mRejectButtons.get(mCurrentPosition).setVisibility(View.INVISIBLE);
                mStatusTexts.get(mCurrentPosition).setText(R.string.request_accepted);
                mStatusTexts.get(mCurrentPosition).setVisibility(View.VISIBLE);
                // SEND MESSAGE VIA FIREBASE THAT SAYS REQUEST ACCEPTED
            }  else {
                mAcceptButtons.get(mCurrentPosition).setVisibility(View.INVISIBLE);
                mRejectButtons.get(mCurrentPosition).setVisibility(View.INVISIBLE);
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
