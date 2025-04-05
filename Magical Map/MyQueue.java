public class MyQueue { // Queue Class
    private Node head;
    private Node tail;
    private int size;

    public MyQueue() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * @return true if queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @return size of entries
     */
    public int size() {
        return size;
    }

    /**
     * Adds node to the queue
     * @param node node to be added
     */
    public void enqueue(Node node) {
        node.queueNext = null; // Ensure no residual links
        if (tail == null) {
            head = tail = node;
        } else {
            tail.queueNext = node;
            tail = node;
        }
        size++;
    }

    /**
     * Removes node from the queue
     * @return node to be removed
     */
    public Node dequeue() {
        if (head == null) {
            return null; // Queue is empty
        }

        Node temp = head;
        head = head.queueNext;
        if (head == null) {
            tail = null; // Queue becomes empty
        }
        size--;
        return temp;
    }

    /**
     * Shows first element of queue
     * @return first element
     */
    public Node peek() {
        return head; // Returns the head without removing it
    }

    /**
     * Clears queue
     */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}
