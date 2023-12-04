package com.smojify.smojify;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class OAuthServer {

    private static final String TAG = "OAuthServer";
    private static final int PORT = 8888;
    private ServerSocket serverSocket;

    public void start() {
        Log.e(TAG, "Starting AUth Server");
        try {
            serverSocket = new ServerSocket(PORT);
            Log.d(TAG, "Server started at port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Log.d(TAG, "Client connected: " + clientSocket);

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                StringBuilder requestContent = new StringBuilder(); // To store request content

                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    requestContent.append(line).append("\n");
                }

                String request = requestContent.toString();
                Log.d(TAG, "Received request:\n" + request); // Print the received request content

                // Rest of your code to process the request remains unchanged...
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
