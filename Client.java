import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 9000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner in = new Scanner(socket.getInputStream());
             Scanner keyboard = new Scanner(System.in)) {

            System.out.println("Connected to Server. Type 'TAX' to begin or 'END' to quit.");

            while (true) {
                String userInput = keyboard.nextLine();
                out.println(userInput);
                out.flush();
                if ("END".equals(userInput)) {
                    System.exit(0);                    
                }

                String serverResponse = in.nextLine();
                System.out.println("Server says: " + serverResponse);
            }


        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
}
