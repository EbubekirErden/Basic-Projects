import java.util.ArrayList;
import java.util.Objects;

public class User {
    ArrayList<Post> posts;
    UserHash followers;
    UserHash follows;
    PostHash viewed;
    PostHash liked;
    ArrayList<Post> feed;

    String userID;
    int hashCode = -1;
    static final int PRIME1 = 31;
    static final int PRIME2 = 37;

    User(String userID) {
        this.userID = userID;
        followers = new UserHash();
        follows = new UserHash();
        viewed = new PostHash();
        liked = new PostHash();
        posts = new ArrayList<>();
    }

    int hash() {
        if (hashCode != -1) return hashCode;
//        if (this.userID == null || this.userID.isEmpty()) return 0;
//
//        int hash = 17;
//        for (int i = 0; i < this.userID.length(); i++) {
//            char ch = this.userID.charAt(i);
//            hash = hash * PRIME1 + ch;
//            hash = hash ^ (hash / PRIME2);
//        }
//
        this.hashCode = Objects.hashCode(userID);
        return hashCode;

    }

    String follow(User user) {
        this.follows.put(user);
        user.followers.put(this);
        return this.userID + " followed " + user.userID + ".";
    }

    String unfollow(User user) {
        this.follows.remove(user);
        user.followers.remove(this);
        return this.userID + " unfollowed " + user.userID + ".";
    }

    void saw(Post post) {
        viewed.put(post);
    }

    void like(Post post) {
        liked.put(post);
    }

    void unlike(Post post) {
        liked.remove(post);
    }
}
