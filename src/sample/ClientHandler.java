package sample;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientHandler implements Runnable {

    private Socket socket;
    int serverPort;

    public ClientHandler(Socket socket, int serverPort) {
        this.socket = socket;
        this.serverPort = serverPort;
    }


    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(is);
            //reading two messages (file name and size)
            String fileName = dataInputStream.readUTF();
            String userHomeFolder = System.getProperty("user.home") + "\\Desktop\\TORrent\\TCP\\" + serverPort + "\\";
            File receivedFile = new File(userHomeFolder + fileName);
            int fileSize = Integer.parseInt(dataInputStream.readUTF());
            String receivedHash = dataInputStream.readUTF();

            //geting file
            byte [] bytearray = new byte [fileSize];
            FileOutputStream fos = new FileOutputStream(receivedFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            int bytesRead = 0;
            while((bytesRead=is.read(bytearray))!=-1) {
                bos.write(bytearray, 0, bytesRead);
            }

            bos.flush();
            bos.close();
            fos.close();
            dataInputStream.close();
            is.close();
            socket.close();

            MessageDigest complete = MessageDigest.getInstance("MD5");

            String hex = checksum(receivedFile, complete);
            System.out.println(receivedHash + " own");
            System.out.println(hex + " received");

            System.out.println("File saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String checksum(File file, MessageDigest md) throws IOException {

        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int nread;
            while ((nread = fis.read(buffer)) != -1) {
                md.update(buffer, 0, nread);
            }
        }

        // bytes to hex
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();

    }
}
