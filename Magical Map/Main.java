import java.io.*;
import java.util.*;

@SuppressWarnings("t")
public class Main {

    /**
     * Calculates Euclidean distance between node1 and node2
     * @param n1 node1
     * @param n2 node2
     * @return Euclidean distance between nodes
     */
    private static double euclideanDistance(Node n1, Node n2) {
        return Math.sqrt(Math.pow(n1.x - n2.x, 2) + Math.pow(n1.y - n2.y, 2));
    }

    /**
     * Calculates Manhattan distance between nodes
     * @param n1 node1
     * @param n2 node2
     * @return  Manhattan distance between nodes
     */
    private static double manhattanDistance(Node n1, Node n2) {
        return Math.abs(n2.y - n1.y) + Math.abs(n2.x - n1.x);
    }

    /**
    * Finds shortest path from start to goal node
    * @param start starting node
    * @param goal goal node
    * @param graph coordinated graph
    * @param impassable set of impassable nodes
    * @return nodes in the shortest path
    */
    public static MyStack aStar(Node start, Node goal, LandGraph graph,
                                   MyHashSet<Node> impassable) {
        MyPriorityQueue openSet = new MyPriorityQueue(); // unvisited nodes prioritised by their f-scores
        MyHashSet<Node> closedSet = new MyHashSet<>(); // set of visited nodes
//        closedSet.clear(); // just to make sure set is empty at the beginning
        resetNodeScores(graph); // since f-score and g-score are instances of node class we need to reset them

        // start node has g-score (real distance we came) 0 and f-score (distance we get + heuristic) as Euclidean distance
        start.gScore = 0.0;
        start.fScore = manhattanDistance(start, goal);
        openSet.add(start);

        while (!openSet.isEmpty()) { // while there still a node in queue
            Node current = openSet.poll();

            if (current.equals(goal)) { // if we reached the goal
                return constructPath(current);
            }

            closedSet.add(current); // add visited nodes to set to not check them again

            for (Edge edge : graph.getNeighbors(current)) { // check all neighbors for shortest path
                Node neighbor = edge.to;
                if (impassable.contains(neighbor) || closedSet.contains(neighbor)) continue; // Skip impassable or already passed nodes

                double tentativeGScore = current.gScore + edge.weight;

                // if current g-score is smaller than neighbor's g-score it is in the shorter path, so we change our variables
                if (tentativeGScore < neighbor.gScore) {
                    neighbor.cameFrom = current;
                    neighbor.gScore = tentativeGScore;
                    neighbor.fScore = tentativeGScore + manhattanDistance(neighbor, goal);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    } else {
                        openSet.update(neighbor); // if already in the queue it changes its f-score
                    }
                }
            }
        }

        return null;
    }

    /**
     * reset nodes' g, f scores and parents for A* algorithm to work correctly
     * @param graph coordinated graph
     */
    private static void resetNodeScores(LandGraph graph) {
        for (Node node : graph.getAllNodes()) {
            node.gScore = Double.POSITIVE_INFINITY;
            node.fScore = Double.POSITIVE_INFINITY;
            node.cameFrom = null;
        }
    }

    /**
     * Updates nodes in line of sight to check whether they are impassable or not
     * @param current center node
     * @param graph coordinated graph
     * @param radius line of sight distance
     * @param impassable set of impassable nodes
     */
    private static void updateLineOfSight(Node current, LandGraph graph, int radius, MyHashSet<Node> impassable) {
        MyQueue queue = new MyQueue();
        MyHashSet<Node> visited = new MyHashSet<>(); // to not get back to visited nodes
        visited.add(current);
        queue.enqueue(current);

        while (!queue.isEmpty()) { // breadth-first search
            Node node = queue.dequeue();
            for (Edge edge : graph.getNeighbors(node)) {
                Node neighbor = edge.to;

                if (!visited.contains(neighbor) && euclideanDistance(neighbor, current) <= radius) {
                    visited.add(neighbor);
                    queue.enqueue(neighbor);

                    if (neighbor.type >= 2) { // if we find out they are impassable add to set
                        impassable.add(neighbor);
                    }
                }
            }
        }
    }

    /**
     * Checks for best choice between wizards' options
     * @param start starting node
     * @param goal goal node
     * @param graph coordinated graph
     * @param impassable set of impassable nodes
     * @param wizardOptions list of options
     * @return best option
     */
    private static int findBestWizardOption(Node start, Node goal, LandGraph graph, MyHashSet<Node> impassable,
                                            List<Integer> wizardOptions, MyHashSet<Integer> chosen) {
        double bestTime = Double.POSITIVE_INFINITY;
        int bestOption = -1;

        for (int option : wizardOptions) {
            MyHashSet<Node> impassableClone = impassable.clone(); // clone to not alter real data while simulating
            MyHashMap<Node, Integer> originalTypes = new MyHashMap<>();

            for (Node node : graph.getAllNodes()) { // changes all nodes' types same with chosen option to 0 to simulate
                if (node.type == option) {
                    originalTypes.put(node, node.type);
                    impassableClone.remove(node);
                    node.type = 0;
                }
            }

            MyStack path = aStar(start, goal, graph, impassableClone); // simulated path

            if (path != null) { // if path is better change variables
                double travelTime = calculateTravelTime(path);
                if (travelTime < bestTime && !chosen.contains(option)) {
                    bestTime = travelTime;
                    bestOption = option;
                }
            }

            for (Node node : originalTypes.keySet()) { // converts all nodes' types to original state
                node.type = originalTypes.get(node);
            }
        }

        return bestOption;
    }

    /**
     * Calculates travel time of a path for simulation
     * @param path path containing nodes
     * @return travel time
     */
    private static double calculateTravelTime(MyStack path) {
        double travelTime = 0.0;

        if (path.isEmpty()) return travelTime;

        Node prev = path.pop();
        while (!path.isEmpty()) {
            Node current = path.pop();
            travelTime += prev.neighbors.get(current);
            prev = current;
        }

        return travelTime;
    }

    /**
     * Constructs path when goal is reached in A*
     * @param current goal node
     * @return path implemented by stack
     */
    private static MyStack constructPath(Node current) {
        MyStack path = new MyStack();
        while (current != null) {
            path.push(current);
            current = current.cameFrom;
        }

        return path;
    }

    /**
     * Reads node file and adds all nodes with x, y coordinates and types to graph
     * @param fileName name of node file
     * @param impassable set of impassable nodes
     * @return hash map of nodes with keys as their string coordinates
     * @throws IOException when file with filename does not exist
     */
    public static MyHashMap<String, Node> loadNodes(String fileName, MyHashSet<Node> impassable) throws IOException {
        MyHashMap<String, Node> nodes = new MyHashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        reader.readLine(); // Skip first line (boundary)
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int type = Integer.parseInt(parts[2]);
            Node node = new Node(x, y, type);

            if (type == 1) impassable.add(node); // if type 1 we know its impassable from the start
            nodes.put(x + "," + y, node); // Store node by its coordinates as a string key
        }
        reader.close();
        return nodes;
    }

    /**
     * Reads file and adds all edges between nodes
     * @param fileName name of edge file
     * @param graph coordinated graph
     * @param nodes map of nodes
     * @throws IOException when file with filename does not exist
     */
    public static void loadEdges(String fileName, LandGraph graph, MyHashMap<String, Node> nodes) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            String[] coords = parts[0].split(",");
            String[] node1Coords = coords[0].split("-");
            String[] node2Coords = coords[1].split("-");
            double travelTime = Double.parseDouble(parts[1]);

            // Retrieve nodes by coordinates
            Node node1 = nodes.get(node1Coords[0] + "," + node1Coords[1]);
            Node node2 = nodes.get(node2Coords[0] + "," + node2Coords[1]);

            if (node1 != null && node2 != null) { // add edges
                graph.addEdge(node1, node2, travelTime);
                node1.addNeighbor(node2, travelTime);
                node2.addNeighbor(node1, travelTime);
            }
        }
        reader.close();
    }

    /**
     * Main method to run objectives
     * @param args names of files
     * @throws IOException when files with given names does not exist
     */
    public static void main(String[] args) throws IOException {
        String nodesFile = args[0];
        String edgesFile = args[1];
        String missionFile = args[2];
        String logFile = args[3];

        MyHashSet<Node> impassable = new MyHashSet<>();
        List<String> log = new ArrayList<>();

        MyHashMap<String, Node> nodes = loadNodes(nodesFile, impassable); // add nodes
        LandGraph graph = new LandGraph();
        loadEdges(edgesFile, graph, nodes); // add edges
        int objectiveCount = 1;
        MyHashSet<Integer> chosenOptions = new MyHashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(missionFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
            int lineOfSight = Integer.parseInt(reader.readLine());
            String[] parts = reader.readLine().split(" ");
            Node start = nodes.get(parts[0] + "," + parts[1]);
            updateLineOfSight(start, graph, lineOfSight, impassable); // start with seeing around

            String line;
            List<Integer> wizardOptions = new ArrayList<>();
            while ((line = reader.readLine()) != null) { // while there are still objectives
                parts = line.split(" ");
                Node goal = nodes.get(parts[0] + "," + parts[1]);

                if (!wizardOptions.isEmpty()) { // if we have options from above line in file
                    int bestType = findBestWizardOption(start, goal, graph, impassable, wizardOptions, chosenOptions);
                    chosenOptions.add(bestType);
                    log.add("Number " + bestType + " is chosen!");

                    for (Node node : graph.getAllNodes()) {
                        if (node.type == bestType) {
                            node.type = 0; // Change type to passable
                            impassable.remove(node);
                        }
                    }
                }

                wizardOptions = new ArrayList<>(); // reset options

                if (parts.length > 2) { // add new options if exist
                    for (int i = 2; i < parts.length; i++) {
                        wizardOptions.add(Integer.parseInt(parts[i]));
                    }
                }

                boolean reachedGoal = false;
                while (!reachedGoal) {
                    MyStack path = aStar(start, goal, graph, impassable); // find the shortest path
                    if (path==null) {
                        log.add("Path is impassable!");
                        break;
                    }

                    boolean pathClear = true;
                    while (!path.isEmpty()) {
                        Node currentStep = path.pop();

                        if (!currentStep.equals(start)) log.add("Moving to " + currentStep.x + "-" + currentStep.y);
                        updateLineOfSight(currentStep, graph, lineOfSight, impassable); // update impassable nodes with every move

                        for (Node node : path) { // check the nodes remaining if they become impassable
                            if (impassable.contains(node)) {
                                log.add("Path is impassable!");
                                start = currentStep; // Retry from the last valid step
                                pathClear = false;
                                break;
                            }
                        }
                        if (!pathClear) break;

                    }

                    if (pathClear) { // if we reached our objective
                        log.add("Objective " + objectiveCount + " reached!");
                        start = goal;
                        objectiveCount++;
                        reachedGoal = true;
                    }
                }
            }

            for (String entry : log) { // write all logs to output file
                writer.write(entry + "\n");
            }
        }
    }
}
