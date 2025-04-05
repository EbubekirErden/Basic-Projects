import java.util.Objects;

public class ParkingHash<K, V> {

    static class HashNode { // Hash node class
        Integer capacity;
        ParkingLot lot;
        HashNode next;

        public HashNode(Integer key, ParkingLot value) {
            this.capacity = key;
            this.lot = value;
        }
    }

    private static final int DEFAULT_CAPACITY = 100003;
    private HashNode[] table;
    private int size;


    ParkingHash() {
        this.table = new HashNode[DEFAULT_CAPACITY];
        this.size = 0;
    }

    /**
     * hash function
     * @param capacity variable that we put into hash function
     * @return result of hash function
     */
    private int hash(Integer capacity) {
        return Math.abs(capacity.hashCode()) % DEFAULT_CAPACITY;
    }

    /**
     * Put lot with given capacity to hash table
     * @param capacity capacity of lot
     * @param lot lot that we want to put
     */
    void put(Integer capacity, ParkingLot lot) {
//        if ((double) size >= table.length) { // takes too much time
//            resize();
//        }

        int hash = hash(capacity);
        HashNode head = table[hash];

        while (head != null) {
            if (Objects.equals(head.capacity, capacity)) {
                head.lot = lot;
                return;
            }
            head = head.next;
        }

        HashNode node = new HashNode(capacity, lot);
        node.next = table[hash];
        table[hash] = node;
        size++;
    }

    /**
     * Get lot with given capacity
     * @param capacity capacity of wanted lot
     * @return lot with given capacity
     */
    ParkingLot get(Integer capacity) {
        int hash = hash(capacity);
        HashNode head = table[hash];

        while (head != null) {
            if (head.capacity.equals(capacity)) {
                return head.lot;
            }
            head = head.next;
        }

        return null;
    }

    /**
     * Remove element from hash table
     * @param capacity capacity of lot that removed
     */
    void remove(Integer capacity) {
        int index = hash(capacity);
        HashNode head = table[index];
        HashNode prev = null;

        while (head != null) {
            if (head.capacity.equals(capacity)) {
                if (prev != null) {
                    prev.next = head.next;
                } else {
                    table[index] = head.next;
                }
                size--;
                return; // Return the removed value
            }
            prev = head;
            head = head.next;
        }

    }

    /**
     * resize method if necessary
     */
    private void resize() {
        int newCapacity = table.length * 2;
        HashNode[] newTable = new HashNode[newCapacity];

        for (HashNode head : table) {
            while (head != null) {
                HashNode next = head.next;

                // Rehash into new table
                int newHash = Math.abs(head.capacity.hashCode()) % newCapacity;
                head.next = newTable[newHash];
                newTable[newHash] = head;

                head = next;
            }
        }

        this.table = newTable;
    }

}
