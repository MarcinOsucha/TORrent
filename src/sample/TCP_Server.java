package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

public class TCP_Server {

    private ServerSocket serverSocket;
    Socket socket;
    int serverPort = 10000 + (int)(Math.random() * 50000);
    String userHomeFolder = System.getProperty("user.home");

    public TCP_Server() throws IOException {
        serverSocket = new ServerSocket(serverPort);
        Paths.get(userHomeFolder, "Desktop", "TORrent\\TCP\\" + serverPort).toFile().mkdir();
        System.out.println("MultiThreaded TCP Server starts work on port " + serverPort + ".");
    }

    public int getPortNumber(){
        return serverSocket.getLocalPort();
    }

    public void waitForClients() throws IOException {
        while (true){
            socket = serverSocket.accept();
            new Thread(new ClientHandler(socket, serverPort)).start();
        }
    }

    public void closeServer() throws IOException {
        serverSocket.close();
        socket.close();
    }
}