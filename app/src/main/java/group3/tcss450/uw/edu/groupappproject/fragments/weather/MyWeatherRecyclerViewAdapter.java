package group3.tcss450.uw.edu.groupappproject.fragments.weather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.fragments.weather.WeatherFragment.OnWeatherListFragmentInteractionListener;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Weather} and makes a call to the
 * specified {@link OnWeatherListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyWeatherRecyclerViewAdapter extends RecyclerView.Adapter<MyWeatherRecyclerViewAdapter.ViewHolder> {

    private final List<Weather> mValues;
    private final OnWeatherListFragmentInteractionListener mListener;
    private DataUtilityControl duc;

    public MyWeatherRecyclerViewAdapter(List<Weather> items, OnWeatherListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        duc = Constants.dataUtilityControl;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);

        holder.mTemp.setText(mValues.get(position).temp);
        holder.mDate.setText(mValues.get(position).date);
        holder.mDes.setText(mValues.get(position).description);
        holder.mHumidity.setText(mValues.get(position).humidity);
        holder.mWind.setText(mValues.get(position).wind);
        holder.mPressure.setText(mValues.get(position).pressure);



        holder.mIcon.setImageResource(duc.getWeatherDrawable(holder.mView.getContext(),mValues.get(position).icon));



//
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onWeatherListFragmentInteraction(holder.mItem);
////                    System.out.println("DATEEEEE "+ holder.mItem.date);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTemp;
        public final TextView mDate;
        public final TextView mDes;
        public final TextView mWind;
        public final TextView mPressure;
        public final TextView mHumidity;
        public final ImageView mIcon;

        public Weather mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTemp = view.findViewById(R.id.weather_temp);
            mDate = view.findViewById(R.id.weather_date);
            mDes = view.findViewById(R.id.weather_description);
            mWind = view.findViewById(R.id.weather_wind);
            mPressure = view.findViewById(R.id.weather_pressure);
            mHumidity = view.findViewById(R.id.weather_humidity);
            mIcon = view.findViewById(R.id.weather_icon);


        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTemp.getText() + "'";
        }
    }
}
