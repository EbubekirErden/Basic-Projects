public class ParkingTree { // an AVL-Tree class

    ParkingLot root;
    ParkingHash<Integer, ParkingLot> parkingHash;

    ParkingTree() {
        this.root = null;
        this.parkingHash = new ParkingHash<>();
    }

    /**
     * Returns height of a node
     *
     * @param p parking lot (node)
     * @return height of p
     */
    int height(ParkingLot p) {
        return (p == null) ? 0 : p.height;
    }

    /**
     * Updates height of node according to left and right subtrees
     *
     * @param p parking lot (node)
     */
    void updateHeight(ParkingLot p) {
        p.height = Math.max(height(p.left), height(p.right)) + 1;
    }

    /**
     * Right rotation method for left imbalance
     *
     * @param p parking lot (root)
     * @return new root
     */
    ParkingLot rightRotate(ParkingLot p) {
        if (p == null || p.left == null) return p;

        ParkingLot l = p.left;
        ParkingLot lr = l.right;

        //swaps root and root.left
        l.right = p;
        p.left = lr;

        updateHeight(p);
        updateHeight(l);

        return l;
    }

    /**
     * Left rotation for right imbalance
     *
     * @param p parking lot (root)
     * @return new root
     */
    ParkingLot leftRotate(ParkingLot p) {
        if (p == null || p.right == null) return p;

        ParkingLot r = p.right;
        ParkingLot rl = r.left;

        //swap root and root.right
        r.left = p;
        p.right = rl;

        updateHeight(p);
        updateHeight(r);

        return r;
    }

    /**
     * Finds height difference of left and right subtrees
     *
     * @param p parking lot (node)
     * @return height difference
     */
    int getBalance(ParkingLot p) {
        return (p == null) ? 0 : height(p.left) - height(p.right);
    }

    /**
     * Inserts a new parking lot into the AVL tree and hash map.
     * @param p The parking lot to insert.
     */
    public void insert(ParkingLot p) {
        if (parkingHash.get(p.capacity) == null) { // Only insert if capacity is not already present
            parkingHash.put(p.capacity, p);
            root = insertHelper(root, p); // Insert into AVL tree
        }
    }

    private ParkingLot insertHelper(ParkingLot parent, ParkingLot p) {
        if (parent == null) return p;

        // Recursively insert to find the correct spot
        if (parent.compareTo(p) > 0) {
            parent.left = insertHelper(parent.left, p);
        } else if (parent.compareTo(p) < 0) {
            parent.right = insertHelper(parent.right, p);
        } else {
            return parent; // Duplicate capacity, no insertion
        }

        // Update height and balance the tree
        updateHeight(parent);
        int balance = getBalance(parent);

        // Perform rotations if necessary
        if (balance > 1 && parent.left.compareTo(p) > 0) {
            return rightRotate(parent); // Left-Left Case
        }
        if (balance > 1 && parent.left.compareTo(p) < 0) {
            parent.left = leftRotate(parent.left); // Left-Right Case
            return rightRotate(parent);
        }
        if (balance < -1 && parent.right.compareTo(p) < 0) {
            return leftRotate(parent); // Right-Right Case
        }
        if (balance < -1 && parent.right.compareTo(p) > 0) {
            parent.right = rightRotate(parent.right); // Right-Left Case
            return leftRotate(parent);
        }

        return parent;
    }

    /**
     * Deletes a parking lot by capacity from the AVL tree and hash map.
     * @param capacity The capacity of the parking lot to delete.
     */
    public void delete(int capacity) {
        ParkingLot targetLot = parkingHash.get(capacity);
        if (targetLot != null) {

            while (!targetLot.ready.isEmpty()) {
                Truck truck = targetLot.ready.dequeue();
                truck.load = 0; // Remove load from truck
                truck.updateCapacity(); // Update capacity if necessary
            }
            while (!targetLot.waiting.isEmpty()) {
                Truck truck = targetLot.waiting.dequeue();
                truck.load = 0; // Remove load from truck
                truck.updateCapacity(); // Update capacity if necessary
            }

            parkingHash.remove(capacity); // Remove from hash map
            root = deleteHelper(root, capacity); // Delete from AVL tree
        }
    }

    private ParkingLot deleteHelper(ParkingLot root, int capacity) {
        if (root == null) return root;

        if (capacity < root.capacity) {
            root.left = deleteHelper(root.left, capacity);
        } else if (capacity > root.capacity) {
            root.right = deleteHelper(root.right, capacity);
        } else {
            // Node found - perform deletion
            if (root.left == null || root.right == null) {
                root = (root.left != null) ? root.left : root.right;
            } else {
                ParkingLot successor = minValueLot(root.right);
                root.ready = successor.ready;
                root.waiting = successor.waiting;
                root.truckLimit = successor.truckLimit;
                root.capacity = successor.capacity;
                root.right = deleteHelper(root.right, successor.capacity);
            }
        }

        if (root == null) return root;

        // Update height and balance the tree
        updateHeight(root);
        int balance = getBalance(root);

        // Perform rotations if necessary
        if (balance > 1 && getBalance(root.left) >= 0) {
            return rightRotate(root); // Left-Left Case
        }
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left); // Left-Right Case
            return rightRotate(root);
        }
        if (balance < -1 && getBalance(root.right) <= 0) {
            return leftRotate(root); // Right-Right Case
        }
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right); // Right-Left Case
            return leftRotate(root);
        }

        return root;
    }

    private ParkingLot minValueLot(ParkingLot lot) {
        ParkingLot current = lot;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    /**
     * Retrieves a parking lot by capacity using the hash map for quick lookup.
     * @param capacity The capacity of the parking lot to retrieve.
     * @return The parking lot with the specified capacity, or null if not found.
     */
    public ParkingLot findLot(int capacity) {
        return parkingHash.get(capacity);
    }

    /**
     * Finds the parking lot with the largest capacity smaller than the given capacity.
     * @param capacity The capacity constraint.
     * @return The largest smaller parking lot, or null if none is found.
     */
    public ParkingLot findLargestSmaller(int capacity) {
        ParkingLot largestSmaller = null;
        ParkingLot current = root;
        while (current != null) {
            if (current.capacity < capacity) {
                largestSmaller = current;
                current = current.right;
            } else {
                current = current.left;
            }
        }
        return largestSmaller;
    }

    /**
     * Finds the parking lot with the smallest capacity larger than the given capacity.
     * @param capacity The capacity constraint.
     * @return The smallest larger parking lot, or null if none is found.
     */
    public ParkingLot findSmallestLarger(int capacity) {
        ParkingLot smallestLarger = null;
        ParkingLot current = root;
        while (current != null) {
            if (current.capacity > capacity) {
                smallestLarger = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return smallestLarger;
    }
    /**
     * Adds truck to waiting queue
     * @param truck truck to be added
     * @return capacity of truck according to its parking lot that placed in
     */
    String addTruck(Truck truck) {
        ParkingLot targetLot = findLot(truck.maxCapacity - truck.load);

        // If the exact node exists and has space in its queue, add number there
        if (targetLot != null && !targetLot.isFull()) {
            targetLot.waiting.enqueue(truck);
            truck.setLotCapacity(targetLot.capacity);
            return String.valueOf(truck.lotCapacity);
        } else {
            // If the exact node does not exist or queue is full, find largest smaller
            ParkingLot candidate = findLargestSmaller(truck.remaining);

            while (candidate != null) {
                if (!candidate.isFull()) {
                    candidate.waiting.enqueue(truck);
                    truck.setLotCapacity(candidate.capacity);
                    return String.valueOf(truck.lotCapacity);
                }
                candidate = findLargestSmaller(candidate.capacity);
            }
            // If all suitable nodes' queues are full, do nothing
        }
        return "-1";
    }

    /**
     * To move trucks from waiting queue to ready queue
     *
     * @param capacity capacity of parking lot that we want trucks to be moved
     * @return moved truck's id and the capacity of lot where moving happens
     */
    String ready(int capacity) {
        ParkingLot targetLot = findLot(capacity);
        Truck targetTruck;

        if (targetLot != null && !targetLot.waiting.isEmpty()) {
            // Dequeue from waiting and enqueue to ready in the target node
            targetTruck = targetLot.waiting.dequeue();
            targetLot.ready.enqueue(targetTruck);
            return targetTruck.id + " " + targetLot.capacity;
        } else {
            // Find the smallest lot with a capacity larger than the target capacity
            ParkingLot nextLot = findSmallestLarger(capacity);

            // Continue searching until we find a truck in the waiting queue
            while (nextLot != null) {
                if (!nextLot.waiting.isEmpty()) {
                    targetTruck = nextLot.waiting.dequeue();
                    nextLot.ready.enqueue(targetTruck);
                    return targetTruck.id + " " + nextLot.capacity;
                }
                nextLot = findSmallestLarger(nextLot.capacity);
            }
            // If no available truck is found, do nothing
        }

        return "-1";
    }

    /**
     * Method to distribute load among trucks in ready queue
     *
     * @param capacityConstraint Capacity of starting lot
     * @param loadAmount         load amount to be distributed
     * @return String message of lots that has taken load and remaining load step by step
     */
    String load(int capacityConstraint, int loadAmount) {
        StringBuilder result = new StringBuilder(); // result message
        ParkingLot targetLot = findLot(capacityConstraint); // find lot with capacity constraint

        if (targetLot == null || targetLot.ready.isEmpty()) {
            targetLot = findSmallestLarger(capacityConstraint);

            // Keep searching until a suitable lot is found
            while (targetLot != null && targetLot.ready.isEmpty()) {
                targetLot = findSmallestLarger(targetLot.capacity);
            }
        }

        if (targetLot == null || targetLot.ready.isEmpty()) return "-1"; // No truck available for loading

        int remainingLoad = loadAmount;

        // Distribute load across trucks in ready queue, starting with the target lot
        while (targetLot != null && remainingLoad > 0) {
            while (!targetLot.ready.isEmpty() && remainingLoad > 0) {
                Truck truck = targetLot.ready.dequeue();
                int loadForTruck = Math.min(truck.lotCapacity, remainingLoad);
                truck.load += loadForTruck;
                truck.updateCapacity(); // Update remaining capacity
                remainingLoad -= loadForTruck;

                if (truck.isFull()) {
                    truck.load = 0; // Unload if full
                    truck.updateCapacity();
                }

                String newConstraint = addTruck(truck); // Relocate truck based on remaining capacity

                result.append(truck.id).append(" ").append(newConstraint).append(" - ");
            }

            // Find the next lot with a larger capacity if load is still remaining
            if (remainingLoad > 0) {
                targetLot = findSmallestLarger(targetLot.capacity);
                while (targetLot != null && targetLot.ready.isEmpty()) {
                    targetLot = findSmallestLarger(targetLot.capacity);
                }
            }
        }

        return !result.isEmpty() ? result.substring(0, result.length() - 3) : "-1"; // Return load sequence
    }

    /**
     * Finds count of trucks in both line of each lot with capacity greater than target lot
     * @param capacity min capacity constraint
     * @return number of trucks in lots with given constraint
     */
    String count(int capacity) {
        return String.valueOf(countHelper(root, capacity));
    }

    int countHelper(ParkingLot target, int capacity) {
        if (target == null) return 0;

        int truckCount = 0;

        if (target.capacity > capacity) {
            truckCount += target.waiting.size() + target.ready.size();
            truckCount += countHelper(target.left, capacity);
        }

        truckCount += countHelper(target.right, capacity);

        return truckCount;
    }
}





