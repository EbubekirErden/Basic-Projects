public class ParkingLot implements Comparable<ParkingLot>{
    int capacity;
    int truckLimit  ;
    TruckQueue waiting; // waiting queue
    TruckQueue ready; // ready queue
    ParkingLot left;
    ParkingLot right;
    ParkingLot next; // for stack
    int height;

    public ParkingLot(int capacity, int truckLimit) {
        this.capacity = capacity;
        this.truckLimit = truckLimit;
        this.height = -1;
        this.waiting = new TruckQueue();
        this.ready = new TruckQueue();
    }

    /**
     * Compares capacities of parking lots
     * @param o the object to be compared.
     * @return comparison
     */
    @Override
    public int compareTo(ParkingLot o) {
        if (this.capacity < o.capacity) {
            return -1;
        } else if (this.capacity > o.capacity) {
            return 1;
        }
        return 0;
    }

    /**
     * Checks if waiting and ready queues are full or not
     * @return True if full, false if not
     */
    boolean isFull() {
        return waiting.size() + ready.size() >= truckLimit;
    }
}
