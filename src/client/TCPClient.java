package client;

import java.io.*;
import java.net.*;

public class TCPClient {

    public static void main(String argv[]) throws Exception
    {
        String username;
        String connectionResponse;

        String payload;
        String response;

        try {
            // establish connection to server
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            Socket clientSocket = new Socket("127.0.0.1", 6789);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.print("Please enter a username to continue: ");
            while (clientSocket.isConnected()) {
                payload = inFromUser.readLine();
                outToServer.writeBytes(payload + '\n');
                response = inFromServer.readLine();
                System.out.println("SERVER RESPONSE: " + response);
                if (payload.toLowerCase().equals("close")) break;
            }
        }
        catch (IOException e) {
            System.out.println("Unable to connect to server. Please try again.");
        }
    }
}
