package group3.tcss450.uw.edu.groupappproject.fragments.Chats;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.Chats.MyChatsFragment.OnListFragmentInteractionListener;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Credentials} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyMyChatsRecyclerViewAdapter extends RecyclerView.Adapter<MyMyChatsRecyclerViewAdapter.ViewHolder> {

    private final List<Integer> mChatIds;
    private final List<Credentials[]> mChatMembers;
    private final OnListFragmentInteractionListener mListener;
    private DataUtilityControl duc;

    public MyMyChatsRecyclerViewAdapter(ArrayList<Integer> chatId, ArrayList<Credentials[]> theMembers, OnListFragmentInteractionListener listener) {
        mChatIds = chatId;
        mChatMembers = theMembers;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_mychats, parent, false);
        this.duc = Constants.dataUtilityControl;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mChatMembers = mChatMembers.get(position);
        holder.mChatId = mChatIds.get(position);
        StringBuilder chatTitle = new StringBuilder();
        // First fencepost if my Credential isnt first in the array.
        if (mChatMembers.get(position)[0].getMemberId() != duc.getUserCreds().getMemberId()) {
            if (mChatMembers.get(position)[0].getDisplayPref() == 1) {
                chatTitle.append(mChatMembers.get(position)[0].getNickName());
            } else if (mChatMembers.get(position)[0].getDisplayPref() == 2) {
                chatTitle.append(mChatMembers.get(position)[0].getFullName());
            } else {
                chatTitle.append(mChatMembers.get(position)[0].getEmail());
            }
            // Do rest of "fence"
            if (mChatMembers.get(position).length > 1) {
                for (int i = 1; i < mChatMembers.get(position).length; i++) {
                    if (mChatMembers.get(position)[i].getMemberId() != duc.getUserCreds().getMemberId()) {
                        if (mChatMembers.get(position)[i].getDisplayPref() == 1) {
                            chatTitle.append(", " + mChatMembers.get(position)[i].getNickName());
                        } else if (mChatMembers.get(position)[i].getDisplayPref() == 2) {
                            chatTitle.append(", " + mChatMembers.get(position)[i].getFullName());
                        } else {
                            chatTitle.append(", " + mChatMembers.get(position)[i].getEmail());
                        }
                    }
                }
            }
        } else {
            if (mChatMembers.get(position).length > 1) {
                if (mChatMembers.get(position)[1].getDisplayPref() == 1) {
                    chatTitle.append(mChatMembers.get(position)[1].getNickName());
                } else if (mChatMembers.get(position)[1].getDisplayPref() == 2) {
                    chatTitle.append(mChatMembers.get(position)[1].getFullName());
                } else {
                    chatTitle.append(mChatMembers.get(position)[1].getEmail());
                }
                for (int i = 2; i < mChatMembers.get(position).length; i++) {
                    if (mChatMembers.get(position)[i].getDisplayPref() == 1) {
                        chatTitle.append(", " + mChatMembers.get(position)[i].getNickName());
                    } else if (mChatMembers.get(position)[i].getDisplayPref() == 2) {
                        chatTitle.append(", " + mChatMembers.get(position)[i].getFullName());
                    } else {
                        chatTitle.append(", " + mChatMembers.get(position)[i].getEmail());
                    }
                }
            }
        }
        if (chatTitle.length() > 40) {
            String titleString = chatTitle.toString().substring(0, 40) + "... ";
            holder.mChatTitle.setText(titleString);
        } else
            holder.mChatTitle.setText(chatTitle.toString());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onStartChatFragmentInteraction(holder.mChatId, holder.mChatMembers);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mChatIds != null) {
            return mChatIds.size();
        } else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mChatTitle;
        public int mChatId;
        public Credentials[] mChatMembers;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mChatTitle = (TextView) view.findViewById(R.id.chat_members);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mChatTitle.getText() + "'";
        }
    }
}
