public class PostMap {

    private static class Node {
        final Post key;
        int value;
        Node next;

        public Node(Post key, int value) {
            this.key = key;
            this.value = value;
        }

        public Node(Post key, int value, Node next) {
            this(key, value);
            this.next = next;
        }
    }

    private static final int DEFAULT_CAPACITY = 1101;
    private static final float LOAD_FACTOR = 0.85f;

    private Node[] table;
    private int size;
    private int threshold;

    public PostMap() {
        table = new Node[DEFAULT_CAPACITY];
        threshold = (int) (LOAD_FACTOR * DEFAULT_CAPACITY);
    }

    private int hash(Post key) {
        return (key == null) ? 0 : Math.abs(key.hash() % table.length);
    }

    public int get(Post key) {
        int index = hash(key);
        Node node = table[index];
        while (node != null) {
            if (node.key.postID.equals(key.postID))
                return node.value;
            node = node.next;
        }

        return -1;
    }

    public boolean contains(Post key) {
        return get(key) != -1;
    }

    public void put(Post key, int value) {
        int index = hash(key);
        Node node = table[index];
        while (node != null) {
            if (node.key.postID.equals(key.postID)) {
                node.value = value;
                return;
            }
            node = node.next;
        }
        table[index] = new Node(key, value, table[index]);
        if (++size > threshold) {
            resize();
        }
    }

    public void remove(Post key) {
        int index = hash(key);
        Node node = table[index];
        Node prev = null;

        while (node != null) {
            if (node.key.postID.equals(key.postID)) {
                if (prev == null)
                    table[index] = node.next; // Remove first node in chain
                else
                    prev.next = node.next; // Remove subsequent node
                size--;
                return;
            }
            prev = node;
            node = node.next;
        }
    }

    private void resize() {
        Node[] oldTable = table;
        table = new Node[oldTable.length * 2];
        threshold = (int) (table.length * LOAD_FACTOR);
        size = 0;

        for (Node node : oldTable) {
            while (node != null) {
                put(node.key, node.value); // Rehash and add to new table
                node = node.next;
            }
        }
    }
}
