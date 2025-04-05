public class PostHash {

    private static class PostNode {
        Post post;
        PostNode next;

        PostNode(Post post) {
            this.post = post;
            this.next = null;
        }
    }

    int tableSize = 101;
    PostNode[] postTable;
    private int numPosts;
    private double loadFactor = 1.0;

    public PostHash() {
        this.postTable = new PostNode[tableSize];
        this.numPosts = 0;
    }

    public PostHash(int tableSize) {
        this.tableSize = tableSize;
        this.postTable = new PostNode[tableSize];
        this.numPosts = 0;
    }

    private int getIndex(Post post) {
        if (post == null) return -1;

        int hash = post.hash();
        int index = hash % tableSize;

        if (index < 0) {
            index += tableSize;
        }

        return index;
    }

    void put(Post post) {
        if ((double) numPosts / tableSize > loadFactor) {
            rehash(); // Trigger rehashing when load factor exceeds threshold
        }

        int index = getIndex(post);
        PostNode newNode = new PostNode(post);

        if (postTable[index] == null) {
            postTable[index] = newNode;
        } else {
            PostNode current = postTable[index];
            while (current.next != null) {
                if (current.post.postID.equals(post.postID)) {
                    current.post = post;
                    return;
                }
                current = current.next;
            }
            current.next = newNode;
        }
    }

    Post get(String postID) {
        int index = getIndex(new Post(postID));
        PostNode current = postTable[index];

        while (current != null) {
            if (current.post.postID.equals(postID)) {
                return current.post;
            }
            current = current.next;
        }

        return null;
    }

    void remove(Post post) {
        int index = getIndex(post);
        PostNode current = postTable[index];
        PostNode prev = null;

        while (current != null) {
            if (current.post.postID.equals(post.postID)) {
                if (prev == null) {
                    postTable[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                return;
            }

            prev = current;
            current = current.next;
        }
    }

    boolean exists(String postID) {
        return get(postID) != null;
    }

    void rehash() {
        int newTableSize = tableSize * 2; // Double the table size
        PostNode[] newTable = new PostNode[newTableSize];

        for (PostNode head : postTable) {
            while (head != null) {
                Post post = head.post;
                int newIndex = (post.hash() % newTableSize + newTableSize) % newTableSize;

                PostNode newNode = new PostNode(post);
                newNode.next = newTable[newIndex];
                newTable[newIndex] = newNode;

                head = head.next;
            }
        }

        this.postTable = newTable;
        this.tableSize = newTableSize;
    }
}
