import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Ant colony optimization and Brute force finding the shortest path algorithm implementation main code
 * @since 03.05.2024
 * @author Ebubekir Siddik Erden
 */
public class Main {

    /**
     * Reads input files and creates Node array list
     * @param fileName name of the input file
     * @return ArrayList of Node objects
     * @throws FileNotFoundException in case of file does not exist
     */
    public static ArrayList<Node> fileReader(String fileName) throws FileNotFoundException {
        ArrayList<Node> nodes = new ArrayList<>();
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);

        //reading file line by line, creating node objects and adding tem to list
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            Node node = new Node(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
            nodes.add(node);
        }
        scanner.close();
        return nodes;
    }

    /**
     * Finding distance from source location to target location
     * @param firstLocation source location, Node object
     * @param secondLocation target location, Node object
     * @return distance between locations
     */
    public static double edgeDistance(Node firstLocation, Node secondLocation) {
        return Math.sqrt(Math.pow(firstLocation.getX() - secondLocation.getX(), 2) + Math.pow(firstLocation.getY() - secondLocation.getY(), 2));
    }

    /**
     * StdDraw method for brute force algorithm
     * @param bestRoute the shortest route that have found after permutation calculations
     * @param nodes Array list that contains Node objects
     */
    public static void bestPathDraw(int[] bestRoute, ArrayList<Node> nodes) {
        //Canvas initialization
        int canvasWidth = 800;
        int canvasHeight = 800;
        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.clear(StdDraw.WHITE);

        double circleRadius = 0.02;
        StdDraw.setPenColor(StdDraw.DARK_GRAY); //line colors
        StdDraw.setPenRadius(0.005);
        Node firstLocation = nodes.getFirst(); //first node
        double locationX = firstLocation.getX(); //first location x coordinate
        double locationY = firstLocation.getY(); //first location y coordinate

        //Drawing edge lines one by one between nodes
        for (int idx : bestRoute) {
            Node nextLocation = nodes.get(idx);
            double nextLocationX = nextLocation.getX();
            double nextLocationY = nextLocation.getY();
            StdDraw.line(locationX, locationY, nextLocationX, nextLocationY);
            locationX = nextLocationX;
            locationY = nextLocationY;
        }
        StdDraw.line(locationX, locationY, firstLocation.getX(), firstLocation.getY()); //line that completes cycle

        //Drawing nodes and writing numbers over them. Starting node's color is different from others.
        for (int i = 0; i < nodes.size(); i++) {
            Node location = nodes.get(i);
            if (i == 0) {
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE); //starting node color
            } else {
                StdDraw.setPenColor(StdDraw.LIGHT_GRAY); //other nodes' color
            }
            StdDraw.filledCircle(location.getX(), location.getY(), circleRadius); //nodes
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(location.getX(), location.getY(), Integer.toString(i + 1)); //node numbers
        }
        StdDraw.show();
    }

    /**
     * Creating adjacency matrix for all nodes
     * @param nodes Array List of nodes
     * @return adjacency list of distances between nodes
     */
    public static double[][] distances(ArrayList<Node> nodes) {
        double[][] distances = new double[nodes.size()][nodes.size()]; //initializing matrix
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                distances[i][j] = edgeDistance(nodes.get(i), nodes.get(j)); //calculating distance between chosen two node
            }
        }
        return distances;
    }

    /**
     * Draws pheromone intensity map with StdDraw library
     * @param pheromoneIntensity adjacency matrix that holds pheromone levels between every two node
     * @param nodes Array list of nodes
     */
    public static void pheromoneIntensityMap(double[][] pheromoneIntensity, ArrayList<Node> nodes) {
        //canvas initialization
        int canvasWidth = 800;
        int canvasHeight = 800;
        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.WHITE);

        double circleRadius = 0.02;
        StdDraw.setPenColor(StdDraw.DARK_GRAY); //line color
        //Drawing lines between every two node
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                double penRadius = pheromoneIntensity[i][j];
                if (nodes.size() > 15)
                    StdDraw.setPenRadius(penRadius*300);
                else
                    StdDraw.setPenRadius(penRadius*200);
                StdDraw.line(nodes.get(i).getX(), nodes.get(i).getY(), nodes.get(j).getX(), nodes.get(j).getY());
            }
        }

        //Drawing nodes
        for (int i = 0; i < nodes.size(); i++) {
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY); // node color
            StdDraw.filledCircle(nodes.get(i).getX(), nodes.get(i).getY(), circleRadius);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(nodes.get(i).getX(), nodes.get(i).getY(), Integer.toString(i + 1)); //to complete cycle
        }
        StdDraw.show();
    }

    /**
     * Main method for using brute force and ant colony optimization
     * @param args String arguments
     * @throws FileNotFoundException in case of file name invalid
     */
    public static void main(String[] args) throws FileNotFoundException {
        //constants
        double INITIAL_PHEROMONE = 0.1;
        int NUMBER_OF_ANTS = 100;
        int NUMBER_OF_ITERATIONS = 100;
        double DECAY_FACTOR = 0.85;
        double ALPHA = 0.86;
        double BETA = 1.42;
        double Q = 0.0001;

        int chosenMethod = 2; //to chose between two methods
        long startTime = System.currentTimeMillis(); //Code execution starting time
        String fileName = "input04.txt";
        ArrayList<Node> nodes = fileReader(fileName); //reading file and creating array list of nodes

        //Brute force method
        if (chosenMethod == 1) {
            BruteForce bruteForce = new BruteForce(distances(nodes));
            bruteForce.findShortestPath();
            bestPathDraw(bruteForce.getShortestPath(), nodes);

            //if the shortest route exists
            if (bruteForce.getShortestPath() != null) {
                int[] sourceShortestRoute = new int[bruteForce.getShortestPath().length + 1];
                System.arraycopy(bruteForce.getShortestPath(), 0, sourceShortestRoute, 0, bruteForce.getShortestPath().length);
                sourceShortestRoute[sourceShortestRoute.length - 1] = 0; //adding first element to last to complete the cycle
                String bestRouteText = Arrays.toString(Arrays.stream(sourceShortestRoute).map(i -> i + 1).toArray()); //incrementing every value by one for drawing purposes
                System.out.println("Method: Brute Force Method");
                System.out.printf("Shortest Distance %.5f \n", bruteForce.getShortestPathLength()); //printing min distances in a formatted way
                System.out.println("Shortest Path: " + bestRouteText); //printing the shortest route
            }

            bestPathDraw(bruteForce.getShortestPath(), nodes);
        }

        //Ant Colony Optimization method
        else if (chosenMethod == 2) {
            AntColonyOptimization aco = new AntColonyOptimization(distances(nodes), INITIAL_PHEROMONE, NUMBER_OF_ANTS, NUMBER_OF_ITERATIONS, DECAY_FACTOR, ALPHA, BETA, Q);
            aco.optimize(); //optimize method to find best route and distance
            int[] bestRoute = aco.getBestRoute(); //getting best route
            double bestDistance = aco.getBestDistance(); //getting best distance

            String bestRouteText = Arrays.toString(Arrays.stream(bestRoute).map(i -> i + 1).toArray()); //incrementing all values by one for drawing purposes
            System.out.println("Method: Ant Colony Optimization");
            System.out.printf("Shortest Distance %.5f \n", bestDistance); //printing min distance
            System.out.println("Shortest Path: " + bestRouteText); //printing best route

            int chosenGraphic = 0; //to choose between map options
            if (chosenGraphic == 0)
                pheromoneIntensityMap(aco.getPheromones(), nodes); //drawing pheromone intensity map
            else
                bestPathDraw(aco.getBestRoute(), nodes); //drawing the shortest path that found
        }

        long endTime = System.currentTimeMillis(); //Code work end time
        double duration = (endTime - startTime); //Code work total time
        System.out.println("Time it takes to find the shortest path: " + duration/1000  + " seconds"); //printing total time
    }
}

