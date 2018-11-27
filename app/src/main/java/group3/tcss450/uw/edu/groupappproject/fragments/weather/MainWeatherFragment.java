package group3.tcss450.uw.edu.groupappproject.fragments.weather;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.activities.HomeActivity;
import group3.tcss450.uw.edu.groupappproject.activities.MapsActivity;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainWeatherFragment extends Fragment {

    public MainWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_weather, container, false);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.mainWeather_floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.MY_CURRENT_LOCATION == null) {
                    Snackbar.make(view, "Please wait for location to enable", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Intent i = new Intent(getActivity(), MapsActivity.class);
                    //pass the current location on to the MapActivity when it is loaded
                    i.putExtra("LOCATION", Constants.MY_CURRENT_LOCATION);
                    startActivity(i);
                }
            }
        });

        insertNestedFragment(R.id.mainWeather_frameLayout_container, new ViewWeatherFragment());

        return v;
    }

    private void insertNestedFragment(int container, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(container, fragment).commit();
    }
}
