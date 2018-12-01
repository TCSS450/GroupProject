package group3.tcss450.uw.edu.groupappproject.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;
import group3.tcss450.uw.edu.groupappproject.utility.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChangeDisplayName.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChangeDisplayName#} factory method to
 * create an instance of this fragment.
 */
public class ChangeDisplayName extends Fragment {


    private OnFragmentInteractionListener mListener;
    private RadioButton mNickNameButton;
    private RadioButton mFullNameButton;
    private RadioButton mEmailButton;
    private DataUtilityControl duc;
    private int mDisplayPref;

    public ChangeDisplayName() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_display_name, container, false);
        duc = Constants.dataUtilityControl;
        mNickNameButton = v.findViewById(R.id.radioButton_ChangeDisplayPref_NickName);
        mFullNameButton = v.findViewById(R.id.radioButton_ChangeDisplay_FullName);
        mEmailButton = v.findViewById(R.id.radioButton_ChangeDisplay_email);
        if (duc.getUserCreds().getDisplayPref() == 1) {
            mNickNameButton.setChecked(true);
        } else if (duc.getUserCreds().getDisplayPref() == 2) {
            mFullNameButton.setChecked(true);
        } else {
            mEmailButton.setChecked(true);
        }
        Button b = v.findViewById(R.id.button_changeDisplay_apply);
        b.setOnClickListener(this::onApply);
        return v;
    }

    public void onApply(View view) {
        Uri changeDisplayPrefUri = duc.getChangeDisplayTypeURI();
        JSONObject msg = new JSONObject();
        System.out.println("MY DISPLAY TYPE = " + duc.getUserCreds().getDisplayPref());
        try {
            msg.put("memberid", duc.getUserCreds().getMemberId());
            if (mNickNameButton.isChecked()) {
                mDisplayPref = 1;
                msg.put("displayType", 1);
            } else if (mFullNameButton.isChecked()) {
                mDisplayPref = 2;
                msg.put("displayType", 2);
            } else {
                mDisplayPref = 3;
                msg.put("displayType", 3);
            }
        } catch (JSONException e) {
            Log.e("ERROR", e.toString());
        }
        new SendPostAsyncTask.Builder(changeDisplayPrefUri.toString(), msg)
                .onPreExecute(this::handleOnPre)
                .onPostExecute(this::handleChangeDisplayTypeOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR",  result);
    }

    private void handleOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    private void handleChangeDisplayTypeOnPost(String result) {
        try {
            Log.d("JSON result", result);
            JSONObject resultsJSON = new JSONObject(result);
            int status = resultsJSON.getInt("status");
            if (status == 1) {
                duc.getUserCreds().setDisplayPref(mDisplayPref);
                System.out.println("MY NEW DISPLAY TYPE = " + duc.getUserCreds().getDisplayPref());
                mListener.onWaitFragmentInteractionHide();
                mListener.onGoHomeFragmentInteraction();
            } else {
                mListener.onWaitFragmentInteractionHide();
                duc.makeToast(getActivity(), getString(R.string.request_error));
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            duc.makeToast(getActivity(), getString(R.string.request_error));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener extends WaitFragment.OnWaitFragmentInteractionListener {
        void onGoHomeFragmentInteraction();
    }
}
