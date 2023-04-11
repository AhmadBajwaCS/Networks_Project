package server;

import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String argv[]) throws Exception
    {
        String username;
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while(true) {
            Socket connectionSocket = welcomeSocket.accept();

            BufferedReader inFromClient =
                    new BufferedReader(new
                            InputStreamReader(connectionSocket.getInputStream()));

            DataOutputStream  outToClient =
                    new DataOutputStream(connectionSocket.getOutputStream());

            Thread clientThread = new ServerThread(connectionSocket, inFromClient, outToClient);
            clientThread.start();
        }
    }
}
