import java.io.*;

public class Main {

    /**
     * To process all commands in one file
     * @param inputFile file in which commands written
     * @param outputFile file in which outputs will be written
     * @param tree parking lot AVL-Tree
     */
    static void processCommands(String inputFile, String outputFile, ParkingTree tree) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) { // check input and output files opening without problem
            String line; // command line
            while ((line = reader.readLine()) != null) { // go commands one by one until no command left
                String result = processCommand(line, tree); // helper method
                if (result != null) { // write results
                    writer.write(result);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * To process one command
     * @param line command line
     * @param tree parking lot AVL-Tree
     * @return command output
     */
    static String processCommand(String line, ParkingTree tree) {
        String[] parts = line.split(" "); // list of command name and parameters
        String method = parts[0]; // command name
        int capacity;

        // finds correct method to be executed according to command name
        switch (method) {
            case "create_parking_lot":
                capacity = Integer.parseInt(parts[1]);
                int truckLimit = Integer.parseInt(parts[2]);
                ParkingLot parkingLot = new ParkingLot(capacity, truckLimit);
                tree.insert(parkingLot);
                return null;
            case "delete_parking_lot":
                capacity = Integer.parseInt(parts[1]);
                tree.delete(capacity);
                return null;
            case "add_truck":
                int truckID = Integer.parseInt(parts[1]);
                capacity = Integer.parseInt(parts[2]);
                return tree.addTruck(new Truck(truckID, capacity));
            case "ready":
                capacity = Integer.parseInt(parts[1]);
                return tree.ready(capacity);
            case "load":
                capacity = Integer.parseInt(parts[1]);
                int load_amount = Integer.parseInt(parts[2]);
                return tree.load(capacity, load_amount);
            case "count":
                capacity = Integer.parseInt(parts[1]);
                return tree.count(capacity);
            default:
                return null;

        }
    }

    /**
     * Main method
     * @param args input and output files
     */
    public static void main(String[] args) {
         ParkingTree tree = new ParkingTree();
         processCommands(args[0], args[1], tree);

    }
}