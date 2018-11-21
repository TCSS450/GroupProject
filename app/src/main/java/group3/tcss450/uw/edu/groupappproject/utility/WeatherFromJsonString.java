package group3.tcss450.uw.edu.groupappproject.utility;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Pass in a string representation of a weather querie return
 * and convert into a java object.
 */
public class WeatherFromJsonString {

    private JSONObject mWeatherJSON;

    private String mTemp;
    private String mDateTime;
    private String mWeatherIcon;
    private String mWeatherCode;
    private String mWeatherDescription;

    public WeatherFromJsonString(String weather) {
        try {
            mWeatherJSON = new JSONObject(weather);
        } catch (JSONException e) {
            Log.e("JSON parse", "error in constructor parsing JSON");
            e.printStackTrace();
        }
        parseWantedData();
        parseWeatherObject();
    }

    /**
     * Parse any wanted data from JSON string passed in.
     */
    private void parseWantedData() {
        try {
            mTemp = mWeatherJSON.getString("temp");
            mDateTime = mWeatherJSON.getString("datetime");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON parse", "error parsing weather JSON wanted data ");
        }
    }

    /**
     * Parse wanted fields of weather inner object.
     */
    private void parseWeatherObject() {
        try {
            if (mWeatherJSON.has("Weather")) {
                JSONObject weather = mWeatherJSON.getJSONObject("weather");
                mWeatherIcon = weather.getString("icon");
                mWeatherCode = String.valueOf(weather.getInt("code"));
                mWeatherDescription = weather.getString("description");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON parse", "error parsing inner weather JSON object");
        }
    }

    /* * GETTERS * * * * ********************************************** */

    public String getTemp() {
        return mTemp;
    }

    public String getWeatherDescription() {
        return mWeatherDescription;
    }

    public String getWeatherCode() {
        return mWeatherCode;
    }

    public String getWeatherIcon() {
        return mWeatherIcon;
    }

    public String getDateTime() {
        return mDateTime;
    }
}
