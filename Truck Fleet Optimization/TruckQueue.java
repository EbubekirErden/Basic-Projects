public class TruckQueue { // linked list implementation of queue
    Truck front, rear;
    private int size;

    TruckQueue() {
        front = rear = null;
        size = 0;
    }

    /**
     * Add method
     * @param truck to be added
     */
    public void enqueue(Truck truck) {
        if (rear != null) {
            rear.next = truck;
        }
        rear = truck;
        if (front == null) {
            front = rear;
        }
        size++; // increase size
    }

    /**
     * Remove method
     * @return removed element
     */
    public Truck dequeue() {
        Truck temp = null;
        if (!isEmpty()) {
            temp = front;
            front = front.next;
            if (front == null) { // If the queue becomes empty
                rear = null;
            }
            size--;
        }

        temp.next = null;
        return temp;
    }

    /**
     * Checks emptiness
     * @return true if empty, false if not
     */
    public boolean isEmpty() {
        return front == null;
    }

    /**
     * Returns size of queue
     * @return size
     */
    public int size() {
        return size;
    }
}