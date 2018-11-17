package group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends;

import java.io.Serializable;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ViewFriendsItemContent implements Serializable {

    private final String mNickName;
    private final String mFirstName;

    private final String mLastName;

    /**
     * Helper class using the builder pattern.
     */
    public static class Builder {
        private final String mNickName;
        private final String mFirstName;

        private String mLastName = "";

        /**
         *  Construct the builder.
         */
        public Builder(String nickName, String firstName) {
            this.mNickName = nickName;
            this.mFirstName = firstName;
        }

        public Builder addLastName(final String lastName) {
            mLastName = lastName;
            return this;
        }

        /**
         * Construct the SetListPost object.
         * @return fully initialized
         */
        public ViewFriendsItemContent build() {
            return new ViewFriendsItemContent(this);
        }
    }

    /**
     * Private to prevent outer instantiation.
     * @param builder
     */
    private ViewFriendsItemContent(final Builder builder) {
        this.mNickName = builder.mNickName;
        this.mFirstName = builder.mFirstName;
        this.mLastName = builder.mLastName;
    }


    /** Getters */

    public String getNickName() { return mNickName; }

    public String getFirstName() { return mFirstName; }

    public String getLastName() { return mLastName; }

    @Override
    public String toString() {
        return this.getFirstName() + ' ' + this.getLastName() + ' ' + this.getNickName();
    }
}
