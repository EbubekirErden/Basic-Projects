public class Node implements Comparable<Node>, Cloneable{
    int x, y; // coordinates
    int type; // 0: free, 1: impassable we know, 2: impassable we don't know
    MyHashMap<Node, Double> neighbors;
    double gScore = Double.POSITIVE_INFINITY; // real cost until this node
    double fScore = Double.POSITIVE_INFINITY; // estimated cost remaining to the goal
    Node cameFrom = null; // parent node to keep track of path
    boolean passable = false;
    Node stackNext; // next variable for stack (in path-finding)
    Node queueNext; // next variable for queue (in neighbor travelling)
    int hash = -1;


    public Node(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        neighbors = new MyHashMap<>();
    }

    /**
     * CLone method
     * @return cloned node
     */
    @Override
    public Node clone() {
        Node clone = new Node(x, y, type);
        clone.cameFrom = cameFrom;
        clone.passable = passable;
        clone.stackNext = stackNext;
        clone.queueNext = queueNext;
        clone.gScore = gScore;
        clone.fScore = fScore;
        clone.neighbors = neighbors.clone();
        return clone;
    }

    /**
     * @return String form of node for printing
     */
    @Override
    public String toString() {
        return x + "-" + y;
    }

    /**
     * @return hash code according to string form of node
     */
    @Override
    public int hashCode() {
        if (hash == -1) {
            String key = x + "-" + y;
            hash = key.hashCode();
        }
        return hash;
    }

    /**
     * Equals method that checks equality according to its coordinates
     * @param obj node to be checked
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false; 
        Node other = (Node) obj;
        return this.x == other.x && this.y == other.y; // Compare coordinates
    }

    /**
     * Adds neighbor to the node
     * @param neighbor neighbor to be added
     * @param travelTime edge weight
     */
    void addNeighbor(Node neighbor, double travelTime) {
        if (!neighbors.containsKey(neighbor)) neighbors.put(neighbor, travelTime);
    }

    /**
     * @param o the object to be compared.
     * @return compares f scores for priority queue
     */
    @Override
    public int compareTo(Node o) {
        return Double.compare(this.fScore, o.fScore);
    }

}
