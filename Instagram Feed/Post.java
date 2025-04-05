import java.util.Objects;

public class Post {
    User author;
    String postID;
//    String content;
    UserHash viewers;
    UserHash likes;
    int numLike = 0;
    int index;
    int hashCode = -1;
    static final int PRIME1 = 31;
    static final int PRIME2 = 37;

    Post (User author, String postID) {
        this(postID);
        this.author = author;
    }

    Post(String postID) {
        this.postID = postID;
        this.viewers = new UserHash();
        this.likes = new UserHash();
    }

    int hash() {
        if (hashCode != -1) return hashCode;
//        if (this.postID == null || this.postID.isEmpty()) return 0;
//
//        int hash = 17;
//        for (int i = 0; i < this.postID.length(); i++) {
//            char ch = this.postID.charAt(i);
//            hash = hash * PRIME1 + ch;
//            hash = hash ^ (hash / PRIME2);
//        }
//
        this.hashCode = Objects.hashCode(postID);
        return hashCode;
    }

    void seen(User u) {
        viewers.put(u);
    }
}
