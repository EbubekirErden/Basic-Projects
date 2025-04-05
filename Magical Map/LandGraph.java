import java.util.*;

public class LandGraph implements Cloneable{

    private MyHashMap<Node, List<Edge>> adjacencyList;

    public LandGraph() {
        this.adjacencyList = new MyHashMap<>();
    }

    /**
     * Adds edge between two nodes
     * @param node1 start node
     * @param node2 end node
     * @param travelTime edge weight
     */
    public void addEdge(Node node1, Node node2, double travelTime) {
        adjacencyList.putIfAbsent(node1, new ArrayList<>());
        adjacencyList.putIfAbsent(node2, new ArrayList<>());
        adjacencyList.get(node1).add(new Edge(node2, travelTime));
        adjacencyList.get(node2).add(new Edge(node1, travelTime)); // Assuming undirected graph
    }

    /**
     * get neighbors of a node
     * @param node Node to be checked
     * @return list of neighbor nodes or empty array list if no neighbor
     */
    public List<Edge> getNeighbors(Node node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    /**
     * Get all nodes
     * @return set of nodes
     */
    public MyHashSet<Node> getAllNodes() {
        return adjacencyList.keySet();
    }

    /**
     * Clone method
     * @return cloned graph
     */
    @Override
    public LandGraph clone() {
        LandGraph graph = new LandGraph();
        graph.adjacencyList = this.adjacencyList.clone();
        return graph;
    }
}
