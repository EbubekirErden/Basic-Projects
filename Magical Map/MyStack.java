import java.util.Iterator;

public class MyStack implements Iterable<Node> { // Stack Class

    private Node head;
    private int size;

    public MyStack() {
        head = null;
        size = 0;
    }

    /**
     * @return true if stack is empty, false otherwise
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Add a node to the stack
     * @param x node to be added
     */
    public void push(Node x) {
        x.stackNext = head;
        head = x;
        size++;
    }

    /**
     * Removes and returns the top node of stack
     * @return top node
     */
    public Node pop() {
        if (isEmpty()) return null;
        Node x = head;
        head = x.stackNext;
        size--;
        return x;

    }

    /**
     * @return top node without removing
     */
    public Node peek() {
        if (isEmpty()) return null;
        return head;
    }

    /**
     * @return size of stack
     */
    public int size() {
        return size;
    }

    /**
     * Clears the stack
     */
    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * toString method for printing
     * @return String form of stack
     */
    @Override
    public String toString() {
        if (isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<Node> it = iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Iterator method for iterable stack
     * @return new iterable
     */
    @Override
    public Iterator<Node> iterator() {
        return new MyStackIterator();
    }

    /**
     * Iterator subclass
     */
    private class MyStackIterator implements Iterator<Node> {
        private Node current = head;

        @Override
        public boolean hasNext() { // check if node has next
            return current != null;
        }

        @Override
        public Node next() { // gets next node
            Node x = current;
            current = current.stackNext;
            return x;
        }
    }
}
