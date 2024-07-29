import java.util.Arrays;
import java.util.Random;

public class AntColonyOptimization {

    //constants
    public double INITIAL_PHEROMONE;
    public int NUMBER_OF_ANTS;
    public int NUMBER_OF_ITERATIONS;
    public double DECAY_FACTOR;
    public double ALPHA;
    public double BETA;
    public double Q;

    private double[][] pheromones; //pheromone level adjacency matrix
    private double[][] distances; //distances adjacency matrix
    private Random random = new Random();
    private int[] bestRoute; //array that will hold best route of each iteration
    private double bestDistance = Double.MAX_VALUE; //value that will hold best distance of each iteration
    private double[] bestDistances; //array that will hold best distances of every iteration

    /**
     * Constructor
     * @param distances input adjacency distance matrix
     * @param INITIAL_PHEROMONE initial pheromone level
     * @param NUMBER_OF_ANTS number of ants in one iteration
     * @param NUMBER_OF_ITERATIONS number of iterations in one runtime
     * @param DECAY_FACTOR pheromone degradation constant
     * @param ALPHA pheromone update constant
     * @param BETA pheromone update constant
     * @param Q pheromone evaporation constant
     */
    public AntColonyOptimization(double[][] distances, double INITIAL_PHEROMONE, int NUMBER_OF_ANTS, int NUMBER_OF_ITERATIONS,
                                 double DECAY_FACTOR, double ALPHA, double BETA, double Q) {
        this.distances = distances;
        this.pheromones = new double[distances.length][distances.length];
        this.INITIAL_PHEROMONE = INITIAL_PHEROMONE;
        this.NUMBER_OF_ANTS = NUMBER_OF_ANTS;
        this.NUMBER_OF_ITERATIONS = NUMBER_OF_ITERATIONS;
        this.DECAY_FACTOR = DECAY_FACTOR;
        this.ALPHA = ALPHA;
        this.BETA = BETA;
        this.Q = Q;
        this.bestDistances = new double[NUMBER_OF_ITERATIONS];

        initializePheromones(); //initialization of pheromones matrix
    }

    /**
     * Initializes every value in pheromones matrix as the value given by initial pheromone constant
     */
    private void initializePheromones() {
        for (double[] row : pheromones) {
            Arrays.fill(row, INITIAL_PHEROMONE);
        }
    }

    /**
     * Main optimization method, finds bests
     */
    public void optimize() {
        for (int iteration = 0; iteration < NUMBER_OF_ITERATIONS; iteration++) {
            int[][] allTours = new int[NUMBER_OF_ANTS][];
            double[] lengths = new double[NUMBER_OF_ANTS];

            //for every ant builds tour and calculates its length
            for (int ant = 0; ant < NUMBER_OF_ANTS; ant++) {
                allTours[ant] = buildTour();
                lengths[ant] = calculateTourLength(allTours[ant]);
            }

            //from the all tours and distances finds the shortest route and distance if it is better
            // than best distance and route. It updates them
            for (int i = 0; i < lengths.length; i++) {
                if (lengths[i] < bestDistance) {
                    bestDistance = lengths[i];
                    bestRoute = allTours[i].clone();
                }
            }

            int shortestIndex = findIndexOfMin(lengths); //finds the index that holds shortest distance
            double shortestLength = lengths[shortestIndex]; //finds the shortest distance of current iteration
            int[] shortestTour = allTours[shortestIndex]; //finds the shortest tour of current iteration
            bestDistances[iteration] = shortestLength; //stores the shortest length of current iteration

            evaporatePheromones(); //evaporate pheromones
            updatePheromones(shortestTour, shortestLength); //updates pheromones
        }

        reorderBestRoute();
    }

    /**
     * Finds index of the lowest valued element in given array
     * @param array lengths array
     * @return index of minimum value
     */
    public int findIndexOfMin(double[] array) {
        int minIndex = 0;
        double minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    /**
     * Reorders array as it started from starting node and ended there
     */
    private void reorderBestRoute() {
        int startNodeIndex = -1;
        //finding starting node index which is zero
        for (int i = 0; i < bestRoute.length; i++) {
            if (bestRoute[i] == 0) {
                startNodeIndex = i;
                break;
            }
        }

        //Reorder
        if (startNodeIndex > 0) {
            int[] newBestRoute = new int[bestRoute.length + 1];
            System.arraycopy(bestRoute, startNodeIndex, newBestRoute, 0, bestRoute.length - startNodeIndex);
            System.arraycopy(bestRoute, 0, newBestRoute, bestRoute.length - startNodeIndex, startNodeIndex);
            newBestRoute[newBestRoute.length - 1] = bestRoute[startNodeIndex]; //Adding the starting index at the end to complete the cycle
            bestRoute = newBestRoute;
        }
    }

    private double calculateTourLength(int[] tour) {
        double length = 0;
        for (int i = 0; i < tour.length - 1; i++) {
            length += distances[tour[i]][tour[i + 1]];
        }
        length += distances[tour[tour.length - 1]][tour[0]]; // Return to start
        return length;
    }

    /**
     * Evaporates every pheromone level in pheromones matrix according to decay factor
     */
    private void evaporatePheromones() {
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[i].length; j++) {
                pheromones[i][j] *= (1 - DECAY_FACTOR);
            }
        }
    }

    /**
     * Builds one tour for one ant
     * @return indices of passed nodes in one ant's tour
     */
    private int[] buildTour() {
        int[] tour = new int[distances.length];
        boolean[] visited = new boolean[distances.length]; //to check whether the node is visited or not
        tour[0] = random.nextInt(distances.length); //random starting node for better optimization
        visited[tour[0]] = true; //starting node is visited

        //Until ant reaches back to starting point selects next node and marks this node's index as true in visited array
        for (int i = 1; i < distances.length; i++) {
            int lastCity = tour[i - 1];
            tour[i] = selectNextNode(lastCity, visited);
            visited[tour[i]] = true;
        }
        return tour;
    }

    /**
     * Selects next node for the ant with respect to roads' probabilities
     * @param currentNode current node where ant resides
     * @param visited array to check whether the nodes are visited or not
     * @return index of selected next node
     */
    private int selectNextNode(int currentNode, boolean[] visited) {
        double[] probabilities = calculateProbabilities(currentNode, visited); //probabilities of next nodes calculated by another method
        double r = random.nextDouble();
        double total = 0;
        //To make probabilistic a random number between 0 and 1 is generated and for every value in probabilities array it adds the value to total sum
        // if its probability is high its probability to be higher than the random number is high too. Even one probability is not enough to pass random
        // number. Loop adds every possibility until it reaches 1, which is limit of random number. The index that have value that passes random number
        // returned as a next node index
        for (int i = 0; i < probabilities.length; i++) {
            total += probabilities[i];
            if (total >= r) {
                return i;
            }
        }
        //for some floating point errors if above condition did not work. It finds the first index that have value other than 0 and returns that index
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] != 0)
                return i;
        }
        return -1; //Should not happen
    }

    /**
     * Calculates probabilities of going from one node to others according to edge weights
     * @param currentNode current node where ant resides
     * @param visited array to check whether the nodes are visited or not
     * @return probabilities of going to every node from current node
     */
    private double[] calculateProbabilities(int currentNode, boolean[] visited) {
        double[] probabilities = new double[distances.length]; //because every node is connected
        double sum = 0.0;
        //It creates initial probabilities according to edge weight formula for every node and sum every probability
        for (int i = 0; i < distances.length; i++) {
            if (!visited[i]) {
                probabilities[i] = Math.pow(pheromones[currentNode][i], ALPHA) *
                        Math.pow(1.0 / distances[currentNode][i], BETA);
                sum += probabilities[i];
            }
        }
        //For probability to work every probability value should be between 0 and 1. Therefore, loop divides every
        // probability value to total sum to find a realistic probability value between 0 and 1
        for (int i = 0; i < distances.length; i++) {
            if (!visited[i]) {
                probabilities[i] /= sum;
            }
        }
        return probabilities;
    }


    /**
     * Increases pheromone levels in the given tour inversely proportional to total length of the tour
     * @param tour the route that will be updated
     * @param length total length of the given tour
     */
    private void updatePheromones(int[] tour, double length) {
        double contribution = Q / length; //increase level inversely proportional to length
        for (int i = 0; i < tour.length - 1; i++) {
            pheromones[tour[i]][tour[i + 1]] += contribution;
        }
        pheromones[tour[tour.length - 1]][tour[0]] += contribution; //the pheromone level of last node to start node to complete cycle
    }

    /**
     * Getter method for best route
     * @return best route
     */
    public int[] getBestRoute() {
        return bestRoute;
    }

    /**
     * Getter method for best distance
     * @return best distance
     */
    public double getBestDistance() {
        return bestDistance;
    }

    /**
     * Getter method for pheromones matrix
     * @return pheromones matrix
     */
    public double[][] getPheromones() {
        return pheromones;
    }

    /**
     * Getter method for best distances array
     * @return best distances
     */
    public double[] getBestDistances() {
        return bestDistances;
    }
}
