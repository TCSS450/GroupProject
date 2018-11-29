package group3.tcss450.uw.edu.groupappproject.fragments.Chats;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import group3.tcss450.uw.edu.groupappproject.R;

public class MyChats_Main extends Fragment {

    public MyChats_Main() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_chats__main, container, false);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        MyChatsFragment frag = new MyChatsFragment();
        loadFriendsFragment(frag);
    }

    private void loadFriendsFragment(Fragment frag) {
        FragmentTransaction transaction =
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FrameLayout_myChats_listFrame, frag)
                        .addToBackStack(null);
        transaction.commit();
    }
}
