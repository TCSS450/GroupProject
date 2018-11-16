package group3.tcss450.uw.edu.groupappproject.fragments.ViewFriends;

public class FriendsGenerator {

    public static final ViewFriendsItemContent[] FRIEND_LISTS;
    public static final int count = 5;

    static {
        FRIEND_LISTS = new ViewFriendsItemContent[count];
        for (int i = 0; i < FRIEND_LISTS.length; i++) {
            FRIEND_LISTS[i] = new ViewFriendsItemContent
                    .Builder("Johnny", "dude")
                    .addLastName("stiltson")
                    .build();
        }
    }

    private FriendsGenerator() { }
}



