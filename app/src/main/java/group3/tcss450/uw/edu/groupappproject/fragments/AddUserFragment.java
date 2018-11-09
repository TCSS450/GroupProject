package group3.tcss450.uw.edu.groupappproject.fragments;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.Credentials;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddUserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DataUtilityControl duc;

    private RadioButton nickname;
    private RadioButton email;
    private RadioButton fullname;
    private RadioButton all;

    private EditText searchView;
    private Button searchbtn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AddUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddUserFragment newInstance(String param1, String param2) {
        AddUserFragment fragment = new AddUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_user, container, false);

        this.duc = Constants.dataUtilityControl;

        nickname = v.findViewById(R.id.nicknamebtn);
        email = v.findViewById(R.id.emailbtn);
        fullname = v.findViewById(R.id.fullnamebtn);
        all = v.findViewById(R.id.allbtn);

        searchView = v.findViewById(R.id.searchView);

        Button b = v.findViewById(R.id.searchbtn);
        b.setOnClickListener(view -> attemptSearch(searchView.getText().toString()));

        nickname.toggle();
        //loadFragment(duc.getFriendsFragment());




        return v;
    }

    private void attemptSearch(String input) {

        if (input.length() > 0) {
            int searchtype =  -1;

            if (nickname.isChecked()) {
                searchtype = 1;

            } else if (email.isChecked()) {
                searchtype = 3;

            } else  if (fullname.isChecked()) {
                searchtype = 2;

            } else {
                searchtype = 4;

            }
            AsyncTask<String, Void, String> task = null;

            task = new TestWebServiceTask();

            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .authority(Constants.BASE_END_POINT_URL)
                    .appendPath("search-members")
                    .appendQueryParameter("searchstring",input).appendQueryParameter("searchtype",searchtype + "")
                    .build();

            task.execute(uri.toString());
        }

    }
    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction =
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.framelayoutforlist, frag)
                        .addToBackStack(null);
        transaction.commit();
    }

    private class TestWebServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];
            try {
                URL urlObject = new URL(url);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
                response = "Unable to connect, Reason: "
                        + e.getMessage();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject resultsJSON = new JSONObject(result);

                JSONArray data = resultsJSON.getJSONArray("data");
                System.out.println(data);

                ArrayList<Credentials> searchResult = new ArrayList<>();

                for (int i = 0; i< data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);

                    Credentials cred = new Credentials.Builder("","")
                            .addFirstName(c.getString("firstName"))
                            .addLastName(c.getString("lastName"))
                            .addNickName(c.getString("nickname"))
                            .build();

                    searchResult.add(cred);

                }

                Constants.searchResults = searchResult;
                loadFragment(duc.getFriendsFragment());


            } catch (JSONException e) {
                e.printStackTrace();
            }

//            if (result.startsWith("Unable to")) {
//                ((EditText) findViewById(R.id.inputEditText)).setError(result);
//            } else {
//                mTextView.setText(result);
//            }
        }
    }

}
