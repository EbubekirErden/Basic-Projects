import java.io.*;

public class Main {

    /**
     * To process all commands in one file
     * @param inputFile file in which commands written
     * @param outputFile file in which outputs will be written
     */
    static void processCommands(String inputFile, String outputFile, App app) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) { // check input and output files opening without problem
            String line; // command line
            while ((line = reader.readLine()) != null) { // go commands one by one until no command left
                String result = processCommand(line, app); // helper method
                if (result != null) { // write results
                    writer.write(result);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String processCommand(String command, App app) {
        String[] parts = command.split(" ");
        switch (parts[0]) {
            case "create_user":
                String userId = parts[1];
                return app.createUser(userId);
            case "follow_user":
                String followerId = parts[1];
                String followeeId = parts[2];
                return app.followUser(followerId, followeeId);
            case "unfollow_user":
                String unfollowerId = parts[1];
                String unfolloweeId = parts[2];
                return app.unfollowUser(unfollowerId, unfolloweeId);
            case "create_post":
                String authorId = parts[1];
                String postId = parts[2];
                return app.createPost(authorId, postId);
            case "see_post":
                String seerId = parts[1];
                String seenId = parts[2];
                return app.seePost(seerId, seenId);
            case "see_all_posts_from_user":
                String viewerId = parts[1];
                String viewedId = parts[2];
                return app.seeAllPosts(viewerId, viewedId);
            case "toggle_like":
                String likerId = parts[1];
                String likedId = parts[2];
                return app.toggleLike(likerId, likedId);
            case "generate_feed":
                String feedUserId = parts[1];
                int count = Integer.parseInt(parts[2]);
                return app.generateFeed(feedUserId, count);
            case "scroll_through_feed":
                String scrollerId = parts[1];
                int num = Integer.parseInt(parts[2]);
                int[] likes = new int[num];
                for (int i = 0; i < num; i++) {
                    likes[i] = Integer.parseInt(parts[3 + i]);
                }
                return app.scrollFeed(scrollerId, num, likes);
            case "sort_posts":
                String sorterId = parts[1];
                return app.sortPosts(sorterId);
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        App app = new App();
        processCommands(args[0], args[1], app);
    }
}
