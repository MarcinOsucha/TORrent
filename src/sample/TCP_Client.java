package sample;

import java.io.*;
import java.net.Socket;

public class TCP_Client {

    Socket socket;
//    private PrintWriter writer;
//    private BufferedReader reader;

    public TCP_Client(int portNumber, File file){

        System.out.println("Sending TCP Client start.");

        try {
            socket = new Socket("127.0.0.1", portNumber);
//            writer = new PrintWriter(socket.getOutputStream(),true);
//            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Socket created on port " + portNumber + ".");
            sendFile(file);
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("TCP Client close.");
    }
    public void sendFile(File selectedFile) {
        try {
            //sending file name and size
//            writer.println(selectedFile.getName());
//            writer.println((int)selectedFile.length());
            OutputStream os = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(os);
            dataOutputStream.writeUTF(selectedFile.getName());
            dataOutputStream.writeUTF(String.valueOf(selectedFile.length()));

            byte [] bytearray = new byte [(int)selectedFile.length()];

            FileInputStream fis = new FileInputStream(selectedFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(bytearray,0,bytearray.length);

            long fileLength = selectedFile.length();
            long current = 0;

//            long start = System.nanoTime();

            //sending file
            while(current!=fileLength){
                int size = 10000;
                if(fileLength - current >= size)
                    current += size;
                else{
                    size = (int)(fileLength - current);
                    current = fileLength;
                }
                bis.read(bytearray, 0, size);
                os.write(bytearray);
                System.out.print("Sending file ... "+(current*100)/fileLength+"% complete!");
            }

            os.flush();
            socket.close();
            System.out.println("File sent succesfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}