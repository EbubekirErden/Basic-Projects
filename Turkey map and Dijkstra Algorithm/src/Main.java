import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Program takes source and destination cities, finds the shortest path between them and draws with StdDraw library
 * @author Ebubekir Siddik Erden, Student ID: 2022400024
 * @since 24.03.2024
 */

public class Main {
    /**
     * Takes two city objects abd if they are connected with each other, returns distance between them
     * @param a First City object
     * @param b Second City object
     * @return Distance between cities
     * @throws FileNotFoundException addCities() method exception
     */
    public static double distance(City a, City b) throws FileNotFoundException {

        ArrayList<City> cities = addCities(); //by addCities() method creating cities ArrayList
        //checks connection of cities by areConnected() method and if they are connected returns the distance
        // value of x and y coordinate difference, else returns Double.MAX_VALUE meaning no connection
        if (areConnected(findCityIndex(a.cityName, cities), findCityIndex(b.cityName, cities)))
            return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
        else
            return Double.MAX_VALUE;
    }

    /**
     * Takes a string city name and arrayList of cities and finds index of city with given name in City arrayList
     * @param s City name
     * @param cities ArrayList that contains City objects
     * @return Index of city with given name in ArrayList
     */
    public static int findCityIndex(String s, ArrayList<City> cities) {

        //iterates over cities ArrayList and gets cityNames by getCityName() method to find the cityName equal to
        // given string and if method can't find the city, returns -1
        for (int i = 0; i < cities.size(); i++) {
            if (cities.get(i).cityName.equalsIgnoreCase(s)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Reads city coordinates file, converts lines to City objects and gives ArrayList of City objects
     * @return ArrayList of all City objects
     * @throws FileNotFoundException In case of given path name could not be found
     */
    public static ArrayList<City> addCities() throws FileNotFoundException {

        ArrayList<City> cities = new ArrayList<>(); //ArrayList that will contain City objects
        File file = new File("city_coordinates.txt"); //opens file that contains information of cities
        Scanner fileScan = new Scanner(file); //Scanner object to iterate over file
        while (fileScan.hasNextLine()) {
            String[] line = fileScan.nextLine().split(","); //Takes line and splits them from commas to obtain data fields of City object
            City city = new City(line[0], Integer.parseInt(line[1].trim()), Integer.parseInt(line[2].trim())); //Takes strings from line array and converts them to City data fields
            cities.add(city); //Adds converted City object to ArrayList
        }
        fileScan.close(); //closes the file
        return cities;
    }

    /**
     * Takes two indices of City objects and checks if they are connected or not
     * @param i1 Index of first city
     * @param i2 Index of second city
     * @return True if cities are connected, else false
     * @throws FileNotFoundException addCities() method exception
     */
    public static boolean areConnected(int i1, int i2) throws FileNotFoundException {

        File file = new File("city_connections.txt"); //opens file that contains information of city connections
        Scanner fileScan = new Scanner(file); //Scanner object to iterate over file
        ArrayList<City> indexedCities = addCities(); //ArrayList that contains City objects
        City c1 = indexedCities.get(i1); //Gets first city by its index in ArrayList
        City c2 = indexedCities.get(i2); //Gets second city by its index in ArrayList
        String c1Name = c1.cityName; //Gets name of the first city
        String c2Name = c2.cityName; //Gets name of the second city
        String line1 = c1Name + "," + c2Name; //Case 1: City names are written as "city1,city2" in file
        String line2 = c2Name + "," + c1Name; //Case 2: City names are written as "city2,city1" in file
        //Takes both cases and compares them with the lines in the file. If anyone of them matches it returns true, else false
        while (fileScan.hasNextLine()) {
            String fileLine = fileScan.nextLine();
            if (fileLine.equals(line1) || fileLine.equals(line2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the shortest path between source city and all others
     * @param cities ArrayList that contains all City objects
     * @param sourceIndex Source city index
     * @param connections Matrix that contains data of distances between cities
     * @param track Integer array that keeps track of passed cities in the shortest path by changing indices of passed cities
     * @return ArrayList of doubles that represents shortest distances between source city and other cities that have indices that are convenient with cities arrayList
     */
    public static ArrayList<Double> shortestPath(ArrayList<City> cities, int sourceIndex, double[][] connections, int[] track) {

        int length = cities.size(); //Gets length of cities ArrayList for convenience
        ArrayList<Double> distances = new ArrayList<>(); //ArrayList that will hold distances from source city to others
        ArrayList<Boolean> visited = new ArrayList<>(); //ArrayList that will keep track of visited cities

        for (int i = 0; i < length; i++) {
            distances.add(Double.MAX_VALUE); //Initializes all distances to the max double value to get minimum step by step
            visited.add(false); //Initializes all values to false, meaning not visited yet
            track[i] = -1; //Initializes all values to -1, again meaning not visited yet
        }

        distances.set(sourceIndex, 0.0); //Initializes distance from source city to source city is zero
        track[sourceIndex] = sourceIndex; //Takes index of source city to get first visited city index

        //iterates over distances arrayList twice to find the shortest path
        for (int i = 0; i < length; i++) {
            int currCity = -1; //Index of current city we are on the path, it initialized as -1 to not take any city at the beginning
            double minDist = Double.MAX_VALUE;
            //first iteration over distances arrayList to find the closest city to current city
            for (int j = 0; j < length; j++) {
                //if the city, which is referred by its index, not visited and its distance from current city is smaller than minDist variable
                // changes minDistance value to new distance and sets current city index to new city, meaning new city is the closest one to current city
                if (!visited.get(j) && distances.get(j) < minDist) {
                    currCity = j;
                    minDist = distances.get(j);
                }
            }

            //Current city can be negative if all cities are unreachable.
            if (currCity > -1)  {
                visited.set(currCity, true); //Sets the new current city, which was closest to previous current city, as visited

                //iterates over arrayList second time to find out closest neighbor from new current city. Then it checks if this new closest city is
                //closer to previous current city by a city in between than a direct route.
                //if former is true it sets the distance value as distance we passed plus the distance from new current city to new neighbor
                for (int j = 0; j < length; j++) {
                    if (connections[currCity][j] != Double.MAX_VALUE && !visited.get(j)) {
                        if (distances.get(currCity) + connections[currCity][j] < distances.get(j)) {
                            distances.set(j, distances.get(currCity) + connections[currCity][j]);
                            track[j] = currCity; //keeps track of index of the city we are found as the closest on the path
                        }
                    }
                }
            }

        }
        return distances;
    }

    /**
     * Takes array of integers that represents the city indices and by back tracking it finds the target path
     * @param track Array of integers that keeps the data of from which city the path came to the indexed city
     * @param destIndex Index of destination city
     * @return ArrayList of integers that only contains indices of passed cities
     */
    public static ArrayList<Integer> getPathIndices(int[] track, int destIndex) {

        ArrayList<Integer> reversePath = new ArrayList<>(); //ArrayList that will contain only indices of visited cities in reverse order
        int currCityIndex = destIndex; //Because it is reverse we start with destination city index by setting current city index to destination city index

        //track array is as long as city number. Therefore, in shortestPath() method it took value of -1 for every index of it.
        //While the method was running it changed visited cities' indices with its -1 values respectively to the which city index the path has come from
        //Hence, to get the path which we want, we passed -1 indexed cities and go backwards from destination index. If current city index is equals
        // to value of the same index in array, meaning it is source city, it stops.
        while (track[currCityIndex] != -1 && currCityIndex != track[currCityIndex]) {
            reversePath.add(currCityIndex);
            currCityIndex = track[currCityIndex];
        }

        //Because array was keeping track of where we came from, it was in reverse order. So, we need to reverse it to find correct path
        reversePath.add(currCityIndex);
        ArrayList<Integer> path = new ArrayList<>(reversePath.size());
        for (int i = 0; i < reversePath.size(); i++) {
            path.add(reversePath.get(reversePath.size() - 1 - i));
        }
        return path;
    }

    /**
     * Uses ArrayList that contains indices of passed cities in the shortest path and first converts indices to related city than to String that shows the path
     * @param passedCities ArrayList of integers that keeps indices of passed cities
     * @return String that shows the shortest path with cities between source and destination cities
     * @throws FileNotFoundException addCities() method exception
     */
    public static String pathToString(ArrayList<Integer> passedCities) throws FileNotFoundException {

        ArrayList<City> cities = addCities(); //Creating ArrayList that contains City objects
        String outString = ""; //Initializing output string

        //To output the expected message it iterates over City objects array and by comparing their indices with the indices
        // we found in getPathIndices() method it adds name of the city to output string
        for (int i = 0; i < passedCities.size(); i++) {
            outString += cities.get(passedCities.get(i)).cityName;
            //if not destination city it adds '->" between cities
            if (i != passedCities.size() - 1)
                outString += " -> ";
        }
        return outString;
    }

    /**
     * Draws the map that shortest path highlighted with StdDraw library
     * @param path ArrayList of integers that represents the indices of cities that have passed
     * @throws FileNotFoundException addCities() method exception
     */
    public static void drawMap(ArrayList<Integer> path) throws FileNotFoundException {

        StdDraw.setCanvasSize(1800, 800); //Sets canvas size
        StdDraw.setXscale(0,2377); //Sets x scale
        StdDraw.setYscale(0,1055); //Sets y scale
        StdDraw.picture(1188.5,527.5,"map.png", 2377, 1055); //Opens map picture with scaled to canvas
        ArrayList<City> cityDraws = addCities(); //ArrayList that contains cities
        StdDraw.enableDoubleBuffering();

        //draws initial map by adding cities and all connections
        for (int i = 0; i < cityDraws.size(); i++) {
            int x = cityDraws.get(i).x; //takes x component of first city
            int y = cityDraws.get(i).y; //takes y component of first city
            String name = cityDraws.get(i).cityName; //takes name of first city
            StdDraw.setPenColor(StdDraw.GRAY); //sets pen color
            StdDraw.filledCircle(x, y, 4); //draws small filled circles that represent city coordinates
            StdDraw.text(x, y + 12, name); //writes name of the city just above the filled circle

            //Check if other cities has connection with the first city, and if they do draws line between cities, else nothing
            for (int j = 0; j < cityDraws.size(); j++) {
                if (areConnected(i, j)) {
                    StdDraw.setPenRadius(0.001);
                    StdDraw.line(x, y, cityDraws.get(j).x, cityDraws.get(j).y); //draws line
                }
            }
        }

        //Draws blue line that represents shortest path between given cities passing from visited cities
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE); //sets pen color
        StdDraw.setPenRadius(0.01); //sets pen radius
        if (path.size() == 1) {
            City c1 = cityDraws.get(path.get(0)); //gets first city in the shortest path
            StdDraw.text(c1.x, c1.y + 12, c1.cityName); //writes name of the passed city just above the filled circle
            StdDraw.filledCircle(c1.x, c1.y, 4); //draws small circle that represent passed city's coordinate
        } else {
            for (int i = 0; i < path.size() - 1; i++) {
                City c1 = cityDraws.get(path.get(i)); //gets first city in the shortest path
                City c2 = cityDraws.get(path.get(i + 1)); //gets second city in the shortest path
                StdDraw.line(c1.x, c1.y, c2.x, c2.y); //draws the blue line between cities
                StdDraw.filledCircle(c1.x, c1.y, 4); //draws small circle that represent passed city's coordinate
                StdDraw.text(c1.x, c1.y + 12, c1.cityName); //writes name of the passed city just above the filled circle
                StdDraw.filledCircle(c2.x, c2.y, 4); //draws small circle that represent passed city's coordinate
                StdDraw.text(c2.x, c2.y + 12, c2.cityName); //writes name of the passed city just above the filled circle
            }
        }
    }

    /**
     * Main method that takes input of source of destination cities from user and handles them step by step
     * to obtain map with the shortest distance between source and destination cities highlighted
     * @param args Main input arguments are not used
     * @throws FileNotFoundException addCities() method exception
     */
    public static void main(String[] args) throws FileNotFoundException {

        ArrayList<City> cities = addCities(); //Creating ArrayList that contains City objects
        double[][] intervals = new double[cities.size()][cities.size()]; //Creating matrix that contains distances between all cities

        //Initializing intervals matrix with distance method
        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < cities.size(); j++) {
                intervals[i][j] = distance(cities.get(i), cities.get(j));
            }
        }

        int sourceIndex; int destIndex; //Initializing source and destination index variables
        Scanner input = new Scanner(System.in); //Scanner object to take input
        System.out.print("Enter starting city: "); //prints line to take source city input
        String source = input.next(); //Initializing source city name as given by client


        //While loop to check if client entered valid source city, which has a name in cities file
        //If it is invalid, code asks again to input new city until valid source city name is given.
        while (true) {
            if (findCityIndex(source, cities) == -1) {
                System.out.printf("City named '%s' not found. Please enter a valid city name. \n", source);
                System.out.print("Enter starting city: ");
                source = input.next();
            } else {
                sourceIndex = findCityIndex(source, cities);
                break;
            }
        }

        System.out.print("Enter destination city: "); //prints line to take destination city input
        String dest = input.next(); //Initializing source city name as given by client

        //While loop to check if client entered valid destination city, which has a name in cities file
        //If it is invalid, code asks again to input new city until valid destination city name is given.
        while (true) {
            if (findCityIndex(dest, cities) == -1) {
                System.out.printf("City named '%s' not found. Please enter a valid city name. \n", dest);
                System.out.print("Enter destination city: ");
                dest = input.next();
            } else {
                destIndex = findCityIndex(dest, cities);
                break;
            }
        }

        int[] track = new int[cities.size()]; //Initializing array that keeps track of visited cities' indices
        //using shortestPath() method to find all distances between source city to others and writing over track array
        ArrayList<Double> paths = shortestPath(cities, sourceIndex, intervals, track);
        double totalDistance = paths.get(destIndex); //from all distances, we get the distance from source to destination city

        //checks if there is a valid path in between given cities. If there is not, prints no path found message
        //if there is, prints total distance plus path, which is modified by pathToString() method
        if (totalDistance == Double.MAX_VALUE) {
            System.out.print("No path could be found.");
        } else {
            System.out.printf("Total distance: %.2f. Path: %s\n", totalDistance, pathToString(getPathIndices(track, destIndex)));
            drawMap(getPathIndices(track, destIndex)); //finally draws map as all variables have been found
            StdDraw.show(); //shows the drawn map to client
        }
    }
}
