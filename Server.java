import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Server {
    private static LinkedList < TaxInfo > taxInfoList = new LinkedList < > ();

    public static void main(String[] args) {

        // Add some example TaxInfo ranges to the TreeSet
        // addToSortedList(new TaxInfo(0, 10000, 0, 0));
        // addToSortedList( new TaxInfo(10001, 20000, 100, 0));
        // addOrUpdateTaxInfo(new TaxInfo(9001, 18000, 0, 0));
        // addOrUpdateTaxInfo(new TaxInfo(1001, 19000, 0, 0));


        // addToSortedList(new TaxInfo(1000, 2000, 0, 0));
        // addToSortedList(new TaxInfo(3000, 4000, 0, 0));
        // addOrUpdateTaxInfo(new TaxInfo(1500, 3500, 0, 0));
        start(9000);
    }
    public static void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TaxServer started. Waiting for client connection...");

            try (Socket clientSocket = serverSocket.accept(); PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); Scanner in = new Scanner(clientSocket.getInputStream())) {

                System.out.println("Client connected.");
                while (true) {
                    String userInput = in .nextLine();
                    System.out.println("Client says: " + userInput);

                    MessageType type;
                    try {
                        type = MessageType.valueOf(userInput);
                    } catch (IllegalArgumentException e) {
                        out.println("Invalid command");
                        continue;
                    }
                    if (type == MessageType.TAX) {
                        out.println("TAX: OK");
                        while (true) {
                            userInput = in .nextLine().toString();
                            String[] message = userInput.split("\\\\n");
                            try {
                                type = MessageType.valueOf(message[0]);
                            } catch (IllegalArgumentException e) {
                                out.println("Invalid command");
                                continue;
                            }
                            switch (type) {
                                case UPDATE:
                                    try {
                                        Double startIncome = Double.parseDouble(message[1].trim());
                                        Double endIncome = Double.parseDouble(message[2].trim());
                                        Double baseTax = Double.parseDouble(message[3].trim());
                                        Double taxRate = Double.parseDouble(message[4].trim());
                                        TaxInfo newTaxInfo = new TaxInfo(startIncome, endIncome, baseTax, taxRate);
                                        addOrUpdateTaxInfo(newTaxInfo);
                                        out.println("UPDATE: OK");

                                    } catch (NumberFormatException e) {
                                        out.println("Invalid number format.");
                                    } catch (NoSuchElementException e) {
                                        out.println("Incomplete input. Resetting to initial state.");
                                        break; // This will break the inner while loop, but remain in the outer while loop
                                    }
                                    break;

                                case QUERY:
                                    // Handle QUERY logic here
                                    break;

                                case BYE:
                                    // Handle BYE logic here
                                    break;

                                case END:
                                    System.out.println("Received END command. Stopping the server.");
                                    System.exit(0);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    public static void addOrUpdateTaxInfo(TaxInfo newTaxInfo) {
        boolean sameRange = false;
        // if same range
        for (int i = 0; i < taxInfoList.size(); i++) {
            TaxInfo taxInfo = taxInfoList.get(i);
            if (newTaxInfo.getStartIncome() == taxInfo.getStartIncome() && newTaxInfo.getEndIncome() == taxInfo.getEndIncome()) {
                taxInfoList.set(i, newTaxInfo);
                sameRange = true;
                break;
            }
        }

        // if inside range of pre-existing range
        if (!sameRange) {

            for (int i = 0; i < taxInfoList.size(); i++) {
                TaxInfo taxInfo = taxInfoList.get(i);
                if (newTaxInfo.getStartIncome() > taxInfo.getStartIncome()) {
                    // if range is completely inside current range, invalid input
                    if (newTaxInfo.getEndIncome() < taxInfo.getEndIncome()) {
                        System.out.println("Invalid range");
                        return;
                    } else {
                        // if new range has a larger endIncome than current endIncome
                        //set the currents endIncome to the new startIncome - 1
                        taxInfoList.get(i).setEndIncome(newTaxInfo.getStartIncome() - 1);

                        // if there are larger ranges saved
                        if (i + 1 < taxInfoList.size()) {
                            LinkedList < Integer > rangesToBeSubsumed = new LinkedList < > ();
                            for (int j = i + 1; j < taxInfoList.size(); j++) {
                                TaxInfo largerRange = taxInfoList.get(j);
                                // if first larger range does not intersect with new range nothing needs to be done
                                if (j == 0 && largerRange.getStartIncome() > newTaxInfo.getEndIncome()) {
                                    break;
                                }
                                // if new range subsumes current larger range
                                else if (largerRange.getEndIncome() < newTaxInfo.getEndIncome()) {
                                    rangesToBeSubsumed.add(j);
                                }
                                // if new range intersects with larger range shorten the larger range
                                else if (largerRange.getEndIncome() > newTaxInfo.getEndIncome()) {
                                    taxInfoList.get(j).setStartIncome(newTaxInfo.getEndIncome() + 1);
                                    break;
                                }
                            }
                            // if ranges were subsumed remove from list
                            if (rangesToBeSubsumed.size() > 0) {
                                for (int index: rangesToBeSubsumed) {
                                    taxInfoList.remove(index);
                                }
                            }
                        }
                    }
                    break;
                }
            }
            addToSortedList(newTaxInfo);
        }
    }

    public static void addToSortedList(TaxInfo newTaxInfo) {
        if (taxInfoList.isEmpty()) {
            taxInfoList.add(newTaxInfo);
            return;
        }

        boolean added = false;
        // If startIncome is less than current 
        for (int i = 0; i < taxInfoList.size(); i++) {
            if (newTaxInfo.getStartIncome() < taxInfoList.get(i).getStartIncome()) {
                taxInfoList.add(i, newTaxInfo);
                added = true;
                break;
            }
        }
        // If it's greater than all saved ranges, add to end
        if (!added) {
            taxInfoList.add(newTaxInfo);
        }
    }
    public static String taxInfoListToString(LinkedList < TaxInfo > list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (TaxInfo taxInfo: list) {
            sb.append("{");
            sb.append("Start Income: ").append(taxInfo.getStartIncome()).append(", ");
            sb.append("End Income: ").append(taxInfo.getEndIncome()).append(", ");
            sb.append("Tax Rate: ").append(taxInfo.getTaxRate()).append(", ");
            sb.append("Fixed Tax: ").append(taxInfo.getFixedTax());
            sb.append("}, ");
        }
        // Remove the last comma and space
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");
        return sb.toString();
    }
}