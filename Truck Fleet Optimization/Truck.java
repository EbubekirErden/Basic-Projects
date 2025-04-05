public class Truck {
    int maxCapacity;
    int load;
    int id;
    int lotCapacity;
    int remaining;
    Truck next = null;

    public Truck(int id, int maxCapacity) {
        this.id = id;
        this.maxCapacity = maxCapacity;
        this.load = 0;
        updateCapacity();
    }

    /**
     * Updates remaining capacity as difference of max capacity and current load
     */
    void updateCapacity() {
        this.remaining = this.maxCapacity - this.load;
    }

    /**
     * checks if truck is full or not
     * @return true or false accordingly
     */
    boolean isFull() {
        return remaining <= 0;
    }

    void setLotCapacity(int currentCapacity) {
        this.lotCapacity = currentCapacity;
    }
}

