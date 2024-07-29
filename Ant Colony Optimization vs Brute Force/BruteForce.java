public class BruteForce {
    private static final double INF = Double.POSITIVE_INFINITY;
    private int numberOfNodes;
    private double[][] distances;
    private int[] shortestPath;
    private double shortestPathLength = INF;

    /**
     * Constructor
     * @param distances adjacency matrix for all distances
     */
    public BruteForce(double[][] distances) {
        this.distances = distances;
        numberOfNodes = distances.length; //for convenience
        this.shortestPath = new int[numberOfNodes];
    }

    /**
     * main method for finding the shortest path by calling permute method
     */
    public void findShortestPath() {
        int[] nodeIndices = new int[numberOfNodes - 1];
        for (int i = 1; i < numberOfNodes; i++) { //initializing starting path, without any permutation index order
            nodeIndices[i - 1] = i;
        }
        permute(nodeIndices, 0); //permutation function
    }

    /**
     * Permutes nodes' indices by backtracking and recursion
     * @param nodeIndices indices of nodes
     * @param start checker for starting condition
     */
    private void permute(int[] nodeIndices, int start) {
        if (start == numberOfNodes - 1) { //end condition - all changes are done
            double pathLength = calculatePathLength(nodeIndices); //calculating length of permutation
            if (pathLength < shortestPathLength) { //checks if permutation is shorter than current shortest path
                shortestPathLength = pathLength;
                shortestPath[0] = 0;
                System.arraycopy(nodeIndices, 0, shortestPath, 1, numberOfNodes - 1);
            }
        } else { //crates recursive permutation tree
            for (int i = start; i < numberOfNodes - 1; i++) {
                swap(nodeIndices, start, i);
                permute(nodeIndices, start + 1);
                swap(nodeIndices, start, i); // backtrack to not cut other branches
            }
        }
    }

    /**
     * Swaps two elements of an array
     * @param array contains node indices
     * @param i index of first element
     * @param j index of second element
     */
    private void swap(int[] array, int i, int j) {
        int temp = array[i]; //temporary variable
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Calculates one permutation output path length
     * @param nodes indices of nodes
     * @return length of given path
     */
    private double calculatePathLength(int[] nodes) {
        double pathLength = distances[0][nodes[0]];
        for (int i = 0; i < numberOfNodes - 2; i++) { //calculates distance between every two sequent element
            int sourceNode = nodes[i];
            int targetNode = nodes[i + 1];
            pathLength += distances[sourceNode][targetNode]; //adds to total length
        }
        pathLength += distances[nodes[numberOfNodes - 2]][0]; //adds the length from last node to first node to complete the cycle
        return pathLength;
    }

    /**
     * Getter method for shortestPath
     * @return shortestPath
     */
    public int[] getShortestPath() {
        return shortestPath;
    }

    /**
     * Getter method for shortestPathLength
     * @return shortestPathLength
     */
    public double getShortestPathLength() {
        return shortestPathLength;
    }
}
