import java.util.ArrayList;

public class App {
    UserHash users;
    PostHash posts;
    ArrayList<Post> postList = null;
    PostHeap postHeap;
    int newCreated = 0;

    App() {
        users = new UserHash(100003);
        posts = new PostHash(100003);
        postList = new ArrayList<>();
        postList.add(new Post(null));
    }

    String createUser(String userId) {
        User temp = users.get(userId);

        if (temp != null) {
            return "Some error occurred in create_user.";
        }

        users.put(new User(userId));
        return "Created user with Id " + userId + ".";
    }

    String followUser(String followerId, String followeeId) {
        User follower = users.get(followerId);
        User followee = users.get(followeeId);

        if (follower == null || followee == null) {
            return "Some error occurred in follow_user.";
        }

        if (follower.follows.exists(followeeId) || followerId.equals(followeeId)) {
            return "Some error occurred in follow_user.";
        }

        return follower.follow(followee);
    }

    String unfollowUser(String followerId, String followeeId) {
        User follower = users.get(followerId);
        User followee = users.get(followeeId);

        if (follower == null || followee == null) {
            return "Some error occurred in unfollow_user.";
        }

        if (!follower.follows.exists(followeeId)) {
            return "Some error occurred in unfollow_user.";
        }

        return follower.unfollow(followee);
    }

    String createPost(String userId, String postId) {
        User author = users.get(userId);

        if (author == null) {
            return "Some error occurred in create_post.";
        }

        if (posts.exists(postId)) {
            return "Some error occurred in create_post.";
        }

        Post post = new Post(author, postId);
        postList.add(post);
        posts.put(post);
        author.posts.add(post);
        newCreated = 1;
        return author.userID + " created a post with Id " + postId + ".";
    }

    String seePost(String userId, String postId) {
        Post post = posts.get(postId);
        User user = users.get(userId);

        if (post == null || user == null) {
            return "Some error occurred in see_post.";
        }

        user.saw(post);
        post.seen(user);
        return userId + " saw " + postId + ".";
    }

    String seeAllPosts(String viewerId, String viewedId) {
        User viewer = users.get(viewerId);
        User viewed = users.get(viewedId);

        if (viewer == null || viewed == null) {
            return "Some error occurred in see_all_posts_from_user.";
        }

        for (Post p : viewed.posts) {
            viewer.saw(p);
            p.seen(viewer);
        }

        return viewerId + " saw all posts of " + viewedId + ".";
    }

    String toggleLike(String userId, String postId) {
        User user = users.get(userId);
        Post post = posts.get(postId);

        if (user == null || post == null) {
            return "Some error occurred in toggle_like.";
        }

        if (!user.liked.exists(postId)) {
            if (!user.viewed.exists(postId))
                user.saw(post);
            user.like(post);
            post.likes.put(user);
            post.numLike++;
            return userId + " liked " + postId + ".";
        } else {
            user.unlike(post);
            post.likes.remove(user);
            post.numLike--;
            return userId + " unliked " + postId + ".";
        }
    }

    private void rebuildHeapIfNecessary() {
        if (postHeap == null || postHeap.currentSize == 0 || newCreated == 1) {
            postHeap = new PostHeap(postList.toArray(new Post[0]));
            newCreated = 0;
        }
    }

    String generateFeed(String userId, int count) {
        User user = users.get(userId);

        if (user == null) {
            return "Some error occurred in generate_feed.";
        }

        user.feed = new ArrayList<>();
        rebuildHeapIfNecessary();
        PostHeap feedHeap = new PostHeap(postHeap.heap.clone());

        String line = "Feed for " + userId + ":\n";
        StringBuilder sb = new StringBuilder();
        sb.append(line);

        while (count > 0 && !feedHeap.isEmpty()) {
            Post max = feedHeap.getMax();
            if (max != null && !user.viewed.exists(max.postID) && user.follows.exists(max.author.userID) && !max.author.userID.equals(userId)) {
                line = "Post ID: " + max.postID + ", Author: " + max.author.userID + ", Likes: " + max.numLike + "\n";
                sb.append(line);
                user.feed.add(max);
                count--;
            }
        }

        if (count > 0) {
            line = "No more posts available for " + userId + ".\n";
            sb.append(line);
        }

        return sb.substring(0, sb.length() - 1);
    }

    String scrollFeed(String userId, int num, int[] likes) {
        User user = users.get(userId);
        if (user == null) {
            return "Some error occurred in scroll_through_feed.";
        }

        String line = userId + " is scrolling through feed:\n";
        StringBuilder sb = new StringBuilder();
        sb.append(line);

        user.feed = new ArrayList<>();
        rebuildHeapIfNecessary();

        if (postHeap == null || postHeap.heap == null || postHeap.isEmpty()) {
            sb.append("No more posts in feed.\n");
            return sb.toString().trim();
        }

        PostHeap feedHeap = new PostHeap(postHeap.heap.clone());
        int count = 0;

        while (count < num && !feedHeap.isEmpty()) {
            Post max = feedHeap.getMax();
            if (max != null && !user.viewed.exists(max.postID) && user.follows.exists(max.author.userID)) {
                user.feed.add(max);
                count++;
            }
        }

        for (int i = 0; i < user.feed.size(); i++) {
            user.saw(user.feed.get(i));
            if (likes[i] == 0) {
                line = userId + " saw " + user.feed.get(i).postID + " while scrolling.\n";
            } else {
                toggleLike(userId, user.feed.get(i).postID);
                line = userId + " saw " + user.feed.get(i).postID + " while scrolling and clicked the like button.\n";
            }
            sb.append(line);
        }


        if (count < num) {
            line = "No more posts in feed.\n";
            sb.append(line);
        }

        return sb.toString().trim();
    }

    String sortPosts(String userId) {
        User user = users.get(userId);
        if (user == null) {
            return "Some error occurred in sort_posts.";
        }

        if (user.posts == null || user.posts.isEmpty()) {
            return "No posts from " + userId + ".";
        }

        String line = "Sorting " + userId + "'s posts:\n";
        StringBuilder sb = new StringBuilder();
        sb.append(line);

        Post[] posts = new Post[user.posts.size() + 1];
        for (int i = 0; i < user.posts.size(); i++) {
            posts[i+1] = user.posts.get(i);
        }
        PostHeap postHeap = new PostHeap(posts);
        Post[] sortedPosts = new Post[posts.length];

        for (int i = 1; i < posts.length; i++) {
            sortedPosts[i] = postHeap.getMax();
            line = sortedPosts[i].postID + ", Likes: " + sortedPosts[i].numLike + "\n";
            sb.append(line);
        }

        return sb.substring(0, sb.length() - 1);
    }
}
