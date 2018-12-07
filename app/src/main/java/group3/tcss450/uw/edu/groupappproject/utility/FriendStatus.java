package group3.tcss450.uw.edu.groupappproject.utility;

/**
 * Store the status of friends.
 */
public class FriendStatus {
    private Credentials cred;
    private int relationship;

    public FriendStatus (Credentials cred, int relationship) {
        this.cred = cred;
        this.relationship = relationship;
    }

    public Credentials getCred() { return this.cred; }

    public int getRelationship() { return  this.relationship; }


}
