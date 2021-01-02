package sample;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TCP_Client {

    Socket socket;

    public TCP_Client(int portNumber, File file) {

        System.out.println("Sending TCP Client start.");

        try {
            socket = new Socket("127.0.0.1", portNumber);

            System.out.println("Socket created on port " + portNumber + ".");
            sendFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("TCP Client close.");
    }

    public void sendFile(File selectedFile) {
        try {
            OutputStream os = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(selectedFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataOutputStream dataOutputStream = new DataOutputStream(os);

            //sending file name and size
            dataOutputStream.writeUTF(selectedFile.getName());
            dataOutputStream.writeUTF(String.valueOf(selectedFile.length()));

            //sending MD5 hash
            MessageDigest complete = MessageDigest.getInstance("MD5");
            String result = checksum(selectedFile, complete);
            dataOutputStream.writeUTF(result);
            System.out.println(result);

            //sending file
            byte[] bytearray = new byte[(int) selectedFile.length()];
            bis.read(bytearray, 0, bytearray.length);
            os.write(bytearray, 0, bytearray.length);

            String hex = checksum(selectedFile, complete);
            System.out.println(hex);

            os.flush();
            os.close();
            fis.close();
            bis.close();
            dataOutputStream.close();
            socket.close();
            System.out.println("File sent succesfully!");
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