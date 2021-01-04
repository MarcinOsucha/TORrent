package sample;

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
            OutputStream os = null;
            DataInputStream dataInputStream = new DataInputStream(is);
            DataOutputStream dataOutputStream = null;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;

            //reading transmission type
            String whatYouDoing = dataInputStream.readUTF();

            if (whatYouDoing.equals("I send")){
                System.out.println("Client informed me he is sending.");

                //getting file name
                String fileName = dataInputStream.readUTF();
                String userHomeFolder = System.getProperty("user.home") + "\\Desktop\\TORrent\\TCP\\" + serverPort + "\\";
                File receivedFile = new File(userHomeFolder + fileName);
                System.out.println("I've got a file name");

                //getting file size
                int fileSize = Integer.parseInt(dataInputStream.readUTF());
                System.out.println("I've got a file size.");

                //getting file hash
                String receivedHash = dataInputStream.readUTF();
                System.out.println("I've got a file hash.");

                //geting file
                byte [] bytearray = new byte [10000000];
                FileOutputStream fos = new FileOutputStream(receivedFile);
                bos = new BufferedOutputStream(fos);
                System.out.println("Output streams crated.");
                int bytesRead = 0, overall = 0, remain = -1;
                long before = System.currentTimeMillis(), start = System.currentTimeMillis();
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
                bos.flush();
                System.out.println("I've got a file.");

                bos.close();
                fos.close();
                dataInputStream.close();
                is.close();
                System.out.println("Streams closed.");

                String ownHash = getHash(receivedFile);
                System.out.println(receivedHash + " - received hash.");
                System.out.println(ownHash + " - own hash.");

                //I think it's not important to inform server if we downloaded correctly.
//                OutputStream os = socket.getOutputStream();
//                DataOutputStream dataOutputStream = new DataOutputStream(os);
//                dataOutputStream.writeUTF(ownHash);
                //Just kidding. At the beginning I've had some problems with socket or stream exception
                //and I've left it like that hehe
                if (receivedHash.equals(ownHash))
                    System.out.println("File saved successfully!");
                else
                    System.out.println("Fail! File saved not correctly");

            } else if (whatYouDoing.equals("I download")){
                System.out.println("Client informed me he want to download");
                os = socket.getOutputStream();

                String myFolder = System.getProperty("user.home") + "\\Desktop\\TORrent\\TCP\\" + serverPort;
                File dir = new File(myFolder);
                String[] fileNames = dir.list();


                //inform client how many files I have
                dataOutputStream = new DataOutputStream(os);
                dataOutputStream.writeUTF(Integer.toString(fileNames.length));

                //send file names
                for (int i=0; i<fileNames.length; i++){
                    dataOutputStream.writeUTF(fileNames[i]);
                }
                System.out.println("I sent to Client files list.");

                String fileName = dataInputStream.readUTF();
                System.out.println("Client want to download " + fileName);

                File fileToSend = new File(myFolder + "\\" + fileName);
                FileInputStream fis = new FileInputStream(fileToSend);
                bis = new BufferedInputStream(fis);

                //send file
                byte[] contents;
                long fileLength = fileToSend.length();
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
                    }
                }
                os.flush();
                dataOutputStream.flush();
                System.out.println("I sent a file.");

                os.close();
                System.out.println("Stream closed.");

            } else
                System.out.println("Illegal order sent to server.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getHash(File file) throws IOException {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int nread;
            while ((nread = fis.read(buffer)) != -1) {
                md.update(buffer, 0, nread);
            }
        }

        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }
}
