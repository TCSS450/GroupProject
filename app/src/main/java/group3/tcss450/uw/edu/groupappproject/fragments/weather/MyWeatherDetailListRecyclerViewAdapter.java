package group3.tcss450.uw.edu.groupappproject.fragments.weather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import group3.tcss450.uw.edu.groupappproject.R;
import group3.tcss450.uw.edu.groupappproject.utility.Constants;
import group3.tcss450.uw.edu.groupappproject.utility.DataUtilityControl;


import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link WeatherDetails} and makes a call to the
 */
public class MyWeatherDetailListRecyclerViewAdapter extends RecyclerView.Adapter<MyWeatherDetailListRecyclerViewAdapter.ViewHolder> {

    private final List<WeatherDetails> mValues;
    private DataUtilityControl duc;

    public MyWeatherDetailListRecyclerViewAdapter(List<WeatherDetails> items) {
        mValues = items;
        duc = Constants.dataUtilityControl;




    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_weatherdetaillist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTemp.setText(mValues.get(position).temp);
        holder.mDes.setText(mValues.get(position).description);
        holder.mTime.setText(mValues.get(position).time);
        holder.mIcon.setImageResource(duc.getWeatherDrawable(holder.mView.getContext(),mValues.get(position).icon));



    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTime;
        public final TextView mTemp;
        public final TextView mDes;

        public final ImageView mIcon;
        public WeatherDetails mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTime = (TextView) view.findViewById(R.id.weather_timeInHour);
            mTemp = (TextView) view.findViewById(R.id.weather_temp);
            mDes = (TextView) view.findViewById(R.id.weather_description);

            mIcon = view.findViewById(R.id.weather_icon);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTemp.getText() + "'";
        }
    }
}
