//package sample;
//
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//import javafx.scene.text.TextFlow;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class Controller {
//
////    Thread tcp_server_thread;
////    TCP_Server tcp_server;
////    String tcp_server_port;
//
////    @FXML private Button sendButton, downloadButton, exitButton;
////    @FXML private Text portPartOne;
////    @FXML private Text portPartTwo;
//
//    private Button tcpProtocolButton, udpProtocolButton, sendButton, downloadButton, exitButton;
//    private Text portPartOne, portPartTwo;
//
//    @FXML
//    private void initialize() {
////        Scene scene = new Scene(root);
////        stage.setScene(scene);
////        stage.show();
////        tcp_server_thread = new Thread(() -> {
////            tcp_server = new TCP_Server();
////            tcp_server_port = Integer.toString(tcp_server.getPortNumber());
//////            TCP_Server.main(args);
////
////            portPartOne.setText("SERVER PORT\n");
////            portPartTwo.setText(tcp_server_port);
////            System.out.println(tcp_server.getPortNumber());
////        });
////        tcp_server_thread.start();
//
//    }
//
//    public void setToSendMode() throws IOException {
////        Stage stage = (Stage) exitButton.getScene().getWindow();
////        Parent root = FXMLLoader.load(getClass().getResource("sendScene.fxml"));
////
////        Scene scene = new Scene(root);
////        stage.setScene(scene);
////        stage.show();
//
//
//    }
//
//    public void setToDownloadScene() throws IOException {
//        Stage stage = (Stage) exitButton.getScene().getWindow();
//        Parent root = FXMLLoader.load(getClass().getResource("downloadScene.fxml"));
//
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    public void exit(){
////        if(tcp_server_thread.isAlive()){
////            tcp_server_thread.interrupt();
////        }
////        System.out.println(tcp_server_thread.isAlive());
//        Stage stage = (Stage) exitButton.getScene().getWindow();
//        stage.close();
//    }
//}
