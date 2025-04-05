public class MyPriorityQueue { // PriorityQueue Class
    private final MyHashMap<Node, Integer> nodeIndices; // to keep track of indices of nodes faster
    private Node[] heap;
    private int size;
    private final int DEFAULT_CAPACITY = 100;

    public MyPriorityQueue() {
        this.nodeIndices = new MyHashMap<>();
        this.heap = new Node[DEFAULT_CAPACITY + 1];
        this.size = 0;
    }

    /**
     * Adds node to the heap
     * @param node node to be added
     */
    public void add(Node node) {
        if (size + 1 >= heap.length) enlargeHeap();
        heap[++size] = node;
//        nodeIndices.put(node, size);
        percolateUp(size);
    }

    /**
     * Gets and removes the min element from the heap
     * @return min element
     */
    public Node poll() {
        if (isEmpty()) return null;
        Node min = heap[1];
        heap[1] = heap[size--];
        if (!isEmpty()) {
//            nodeIndices.put(heap[1], 1);
            percolateDown(1);
        }
        nodeIndices.remove(min);
        return min;
    }

    /**
     * Updates node if its priority is changed
     * @param node node to be updated
     */
    public void update(Node node) {
        Integer index = nodeIndices.get(node);
        if (index != null) {
            percolateUp(index);
            percolateDown(index);
        }
    }

    /**
     * @return true if queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Checks if node exist in heap
     * @param node node to be checked
     * @return true if exists, false otherwise
     */
    public boolean contains(Node node) {
        return nodeIndices.containsKey(node);
    }

    /**
     * Percolates up the node at given index
     * @param index index of node that we want to percolate up
     */
    private void percolateUp(int index) {
        Node node = heap[index];
        while (index > 1 && heap[index / 2].fScore > node.fScore) {
            heap[index] = heap[index / 2];
            nodeIndices.put(heap[index], index);
            index /= 2;
        }
        heap[index] = node;
        nodeIndices.put(node, index);
    }

    /**
     * Percolates down the node at given index
     * @param index index of node that we want to percolate down
     */
    private void percolateDown(int index) {
        Node node = heap[index];
        while (2 * index <= size) {
            int left = 2 * index;
            int right = left + 1;
            int child = left;

            if (right <= size && heap[right].fScore < heap[left].fScore) {
                child = right;
            }

            if (heap[child].fScore >= node.fScore) break;

            heap[index] = heap[child];
            nodeIndices.put(heap[index], index);
            index = child;
        }
        heap[index] = node;
        nodeIndices.put(node, index);
    }

    /**
     * Resizes heap
     */
    private void enlargeHeap() {
        Node[] newHeap = new Node[heap.length * 2];
        System.arraycopy(heap, 0, newHeap, 0, heap.length);
        heap = newHeap;
    }

    /**
     * @return size of entries
     */
    public int size() {
        return size;
    }

    /**
     * Clears heap
     */
    public void clear() {
        nodeIndices.clear();
        heap = new Node[DEFAULT_CAPACITY + 1];
        size = 0;
    }
}
