public class LotStack { // a stack that stores parking lots for preorder traversal
    private ParkingLot top;

    public LotStack() {
        this.top = null;
    }

    /**
     * Add method
     * @param lot lot to be added
     */
    public void push(ParkingLot lot) {
        lot.next = top;
        top = lot;
    }

    /**
     * Remove method
     * @return removed element (top one)
     */
    public ParkingLot pop() {
        if (!isEmpty()) {
            ParkingLot lot = top;
            top = top.next;
            return lot;
        }
        return null;
    }

    /**
     * Check top element
     * @return top element
     */
    public ParkingLot peek() {
        if (!isEmpty()) {
            return top;
        }
        return null;
    }

    /**
     * Check for emptiness
     * @return True if empty, false if not
     */
    public boolean isEmpty() {
        return top == null;
    }
}
