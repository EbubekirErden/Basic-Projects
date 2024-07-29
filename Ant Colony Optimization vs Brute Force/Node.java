public class Node {
    private double x; //x coordinate
    private double y; //y coordinate

    /**
     * Constructor
     * @param x x coordinate
     * @param y y coordinate
     */
    public Node(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter method for x coordinate
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * Getter method for y coordinate
     * @return y
     */
    public double getY() {
        return y;
    }
}
