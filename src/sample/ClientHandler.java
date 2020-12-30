package sample;

import java.io.*;
import java.net.Socket;

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
            int fileSize = Integer.parseInt(dataInputStream.readUTF());

            //geting file
            byte [] bytearray = new byte [fileSize];
            String userHomeFolder = System.getProperty("user.home") + "\\Desktop\\TORrent\\TCP\\" + serverPort + "\\";
            File receivedFile = new File(userHomeFolder + fileName);
            FileOutputStream fos = new FileOutputStream(receivedFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            //No of bytes read in one read() call
            int bytesRead = 0;

            while((bytesRead=is.read(bytearray))!=-1)
                bos.write(bytearray, 0, bytesRead);

            bos.flush();
            socket.close();

            System.out.println("File saved successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            int filesize=1022386;
//            int bytesRead;
//            int currentTot = 0;
//            byte [] bytearray = new byte [filesize];
//            InputStream is = socket.getInputStream();
//            FileOutputStream fos = new FileOutputStream("copy.doc");
//            BufferedOutputStream bos = new BufferedOutputStream(fos);
//            bytesRead = is.read(bytearray,0,bytearray.length);
//            currentTot = bytesRead;
//            do {
//                bytesRead = is.read(bytearray, currentTot, (bytearray.length-currentTot));
//                if(bytesRead >= 0)
//                    currentTot += bytesRead;
//            } while(bytesRead > -1);
//            bos.write(bytearray, 0 , currentTot);
//            bos.flush();
//            bos.close();
//            socket.close();
//
//
//        } catch (FileNotFoundException fileNotFoundException) {
//            fileNotFoundException.printStackTrace();
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
    }
}
