package sample;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

public class TCP_Server {

    private ServerSocket serverSocket;
    private Socket socket;
    int serverPort = 10000 + (int)(Math.random() * 50000);


    public TCP_Server() throws IOException {
        String userHomeFolder = System.getProperty("user.home");
        Paths.get(userHomeFolder, "Desktop", "TORrent\\TCP\\" + serverPort).toFile().mkdir();
        serverSocket = new ServerSocket(serverPort);

        System.out.println("MultiThreaded TCP Server starts work on port " + serverPort + ".");
    }

//    public TCP_Server(int serverNumber){
//        try {
//            socket = new Socket("127.0.0.1", serverNumber);
//            System.out.println("Simple TCP Server starts work for send on port " + serverPort + ".");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public int getPortNumber(){
        return serverSocket.getLocalPort();
    }

    public void waitForClients() throws IOException {
        while (true){
//            System.out.println("We have new sending client.");
            Socket socket = serverSocket.accept();
            new Thread(new ClientHandler(socket, serverPort)).start();
        }
    }

    public void closeServer() throws IOException {
        serverSocket.close();
    }

//    public void sendFile(File selectedFile) {
//        try {
//            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
//            FileInputStream fis = new FileInputStream(selectedFile);
//            byte[] buffer = new byte[4096];
//
//            while (fis.read(buffer) > 0) {
//                dos.write(buffer);
//            }
//
//            fis.close();
//            dos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }


}