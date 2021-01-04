package sample;

import javafx.application.Application;
import javafx.scene.layout.GridPane;

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
//        Alert alertBox = new Alert(Alert.AlertType.INFORMATION, "ok",
//                ButtonType.OK);
//        alertBox.setContentText("Are you sure you want to delete this?");
//        alertBox.initModality(Modality.APPLICATION_MODAL); /* *** */
//        alertBox.initOwner(scene.getWindow());                         /* *** */
//        alertBox.showAndWait();
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
//                    label.setText(progress);
//                    sendGridPane.getChildren().get(6).getId();
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

    //for download
    public TCP_Client(int port){
        try {
            socket = new Socket("127.0.0.1", port);
            System.out.println("TCP Client for download starts.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getFilesList() {
        String[] fileNames = new String[0];
        try {
            OutputStream os = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(os);

            //inform server that I want to download
            dataOutputStream.writeUTF("I download");
            System.out.println("Server informed");

            InputStream is = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(is);
            int countOfFiles = Integer.parseInt(dataInputStream.readUTF());

            fileNames = new String[countOfFiles];
            for (int i=0; i<countOfFiles; i++){
                fileNames[i] = dataInputStream.readUTF();
//                System.out.println(fileNames[i]);
            }
            System.out.println("I got files list from server");

//            dataInputStream.close();
//            dataOutputStream.flush();
//            dataOutputStream.close();
//            is.close();
//            os.close();
            System.out.println("Streams closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    public void downloadFile(String selectedFile, int myServerPort) {
        try {
            System.out.println("I try to create output stream from socket");
            OutputStream os = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(os);
            System.out.println("Output stream crated");

            //inform server I selected a file
            dataOutputStream.writeUTF("I selected");

            //inform server what file I want to download
            dataOutputStream.writeUTF(selectedFile);
            System.out.println("Server informed I want to download" + selectedFile);

            File receivedFile = new File(System.getProperty("user.home") + "\\Desktop\\TORrent\\TCP\\" + myServerPort + "\\" + selectedFile);
            InputStream is = socket.getInputStream();

            byte [] bytearray = new byte [1024];
            FileOutputStream fos = new FileOutputStream(receivedFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            System.out.println("I start saving file from server");
            int bytesRead = 0;
            while((bytesRead=is.read(bytearray))!=-1) {
                bos.write(bytearray, 0, bytesRead);
            }
            System.out.println("File saved");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}