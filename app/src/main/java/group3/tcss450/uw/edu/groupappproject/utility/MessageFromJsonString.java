package group3.tcss450.uw.edu.groupappproject.utility;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageFromJsonString {

    private JSONObject mJsonMessage;

    private String mNickname;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mMessage;
    private String mTimeStamp;
    private String mDisplayType;

    public MessageFromJsonString(String jsonMessage) {
        try {
            mJsonMessage = new JSONObject(jsonMessage);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Message from json", "failed parsing object");
        }
        parseFields();
    }

    private void parseFields() {
        try {
            mNickname = mJsonMessage.getString("nickname");
            mFirstName = mJsonMessage.getString("firstname");
            mLastName = mJsonMessage.getString("lastname");
            mEmail = mJsonMessage.getString("email");
            mMessage = mJsonMessage.getString("message");
            mTimeStamp = formatDateTime(mJsonMessage.getString("timestamp"));
            mDisplayType = mJsonMessage.getString("display_type");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Message from json", "failed parsing object fields");
        }
    }

    /**
     * Convert string type
     * @param dateTime the original string in 2018-11-19 15:39:21.257496 format
     * @return formatted string in 2018-11-19 15:39
     */
    private static String formatDateTime(String dateTime) {

        return dateTime.substring(0, 16);
    }


    public String getNickname() {
        return mNickname;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public String getDisplayType() {
        return mDisplayType;
    }

    @Override
    public String toString() {
        return mJsonMessage.toString();
    }
}
