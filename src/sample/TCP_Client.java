package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TCP_Client {

    Socket socket;
    boolean portCorrect = false;
    int progressMaster = 0;

    //for send
    public TCP_Client(int portNumber, String s) {

        System.out.println(s+ " TCP Client start.");

        try {
            socket = new Socket("127.0.0.1", portNumber);
            portCorrect = true;
            System.out.println("Socket created on port " + portNumber + ".");
        } catch (ConnectException e){
//            e.printStackTrace();
            System.out.println("No server is working on this port.");
            portCorrect = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendFile(File selectedFile) {
        System.out.println("Send file method start.");
        boolean fileSent = false;
        try {
            OutputStream os = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(selectedFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataOutputStream dataOutputStream = new DataOutputStream(os);

            System.out.println("Streams created.");

            //inform server that I'm sending file now
            dataOutputStream.writeUTF("I send");
            System.out.println("Server informed I'm sending.");

            //sending file name and size
            dataOutputStream.writeUTF(selectedFile.getName());
            dataOutputStream.writeUTF(String.valueOf(selectedFile.length()));
            System.out.println("File name and size sent.");

            //sending MD5 ownHash
            MessageDigest complete = MessageDigest.getInstance("MD5");
            String ownHash = checksum(selectedFile, complete);
            dataOutputStream.writeUTF(ownHash);
            System.out.println("Hash sent.");

//            Parent root = new Parent() {};
//            VBox vBox = new VBox();
//            vBox.setStyle("-fx-background-color: #fd1b41");

//            Stage stage = new Stage();
////            stage.setTitle("My New Stage Title");
//            stage.setScene(new Scene(vBox, 500, 500));
//            stage.show();


//            ProgressBar pb = new ProgressBar();
//            sendGridPane.add(pb,0,4,2,1);


            //sending file
            byte[] contents;
            long fileLength = selectedFile.length();
            long current = 0;
            long start = System.nanoTime();
            String progress = "";
            while(current!=fileLength){
                int size = 10000;
                if(fileLength - current >= size)
                    current += size;
                else{
                    size = (int)(fileLength - current);
                    current = fileLength;
                }
                contents = new byte[size];
                bis.read(contents, 0, size);
                os.write(contents);
                if (!progress.equals(Long.toString((current*100)/fileLength))){
                    progress = Long.toString((current*100)/fileLength);
                    System.out.println("Sending file ... " + progress + "% complete!");
                    progressMaster = Integer.parseInt(progress);
                }
            }
//            stage.close();

//            os.flush();

//            InputStream is = socket.getInputStream();
//            DataInputStream dataInputStream = new DataInputStream(is);
//            String receivedHash = dataInputStream.readUTF();
//            System.out.println("Hash gotten.");
//
//            if (receivedHash.equals(ownHash))
//                fileSent = true;

            os.flush();
            os.close();
            fis.close();
            bis.close();
            dataOutputStream.close();
            socket.close();
            System.out.println("File sent succesfully!");
            fileSent = true;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return fileSent;
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

    OutputStream os = null;
    DataOutputStream dataOutputStream = null;
    InputStream is = null;
    DataInputStream dataInputStream = null;

    //for download
    public TCP_Client(int port){
        try {
            socket = new Socket("127.0.0.1", port);
            System.out.println("TCP Client for download starts.");

            os = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(os);
            is = socket.getInputStream();
            dataInputStream = new DataInputStream(is);
            portCorrect = true;
        } catch (ConnectException e){
//            e.printStackTrace();
            System.out.println("No server is working on this port.");
            portCorrect = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getFilesList() {
        String[] fileNames = new String[0];
        try {
            //inform server that I want to download
            dataOutputStream.writeUTF("I download");
            System.out.println("Server informed.");

            //get number of files on server
            int countOfFiles = Integer.parseInt(dataInputStream.readUTF());

            //get file names
            fileNames = new String[countOfFiles];
            for (int i=0; i<countOfFiles; i++){
                fileNames[i] = dataInputStream.readUTF();
            }
            System.out.println("I've got files list from server.");

            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    public void downloadFile(String selectedFile, int myServerPort) {
        try {
            //inform server what file I want to download
            dataOutputStream.writeUTF(selectedFile);
            System.out.println("Server informed I want to download " + selectedFile + ".");
            File receivedFile = new File(System.getProperty("user.home") + "\\Desktop\\TORrent\\TCP\\" + myServerPort + "\\" + selectedFile);

            //start downloading
            byte [] bytearray = new byte [1048576];
            FileOutputStream fos = new FileOutputStream(receivedFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead = 0, overall = 0, remain = -1;
            long before = System.currentTimeMillis(), start = System.currentTimeMillis();
            System.out.println("I start downloading file from server...");
            bos.flush();
            while((bytesRead=is.read(bytearray)) != -1) {
                before = System.nanoTime();
                bos.write(bytearray, 0, bytesRead);
                long after = System.nanoTime();
                overall += bytesRead;
                double speed = (bytesRead / (after-before)) * 1000000000;
                float sec = (System.currentTimeMillis() - start) / 1000F;
                if ((remain != (int)(speed/overall)) && (remain != 0) && (sec >= 0.5)) {
                    System.out.println("Remain " + remain + "s.");
                    start = System.currentTimeMillis();
                }
                remain = (int) (speed / overall);
            }
            fos.flush();
            fos.close();
            System.out.println("File saved");

            dataInputStream.close();
            is.close();
            System.out.println("Streams closed.");

            Alert alertBox = new Alert(
                    Alert.AlertType.INFORMATION,
                    "File " + receivedFile.getName() + " downloaded correctly.",
                    ButtonType.OK);
            alertBox.setTitle("Host " + myServerPort);
//            alertBox.initModality(Modality.APPLICATION_MODAL); /* *** */
//            alertBox.initOwner(scene.getWindow());                         /* *** */
            alertBox.setHeaderText("Success!");
            alertBox.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}