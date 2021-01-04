package sample;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    private static String actualProtocol = "TCP";
    private int serverPort = 0;
    private TCP_Server tcp_server;

    private Button tcpProtocolButton, udpProtocolButton, exitButton;
    private Text portTextTwo;
    private TextFlow portFlow;
    private VBox vBox;
    private GridPane mainGridPane;


    @Override
    public void start(Stage primaryStage){
        //staring TCP Server
        startTCPserver();

        //creating main vBox containing everything
        vBox = new VBox();
        vBox.setSpacing(30);

        mainGridPane = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(20);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(60);
        col2.setHalignment(HPos.CENTER);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(20);
        mainGridPane.getColumnConstraints().addAll(col1,col2,col3);

        //protocol buttons
        tcpProtocolButton = new Button("TCP");
        tcpProtocolButton.getStyleClass().add("protocolButton");
        tcpProtocolButton.setStyle(
                "-fx-background-color: #00ecff;\n" +
                        "-fx-text-fill: #161616;");
//        tcpProtocolButton.setOnAction(e -> changeProtocol("TCP"));
        mainGridPane.add(tcpProtocolButton,0,0);

        udpProtocolButton = new Button("UDP");
        udpProtocolButton.getStyleClass().add("protocolButton");
        udpProtocolButton.getStyleClass().add("protocolButton");
//        udpProtocolButton.setOnAction(e -> changeProtocol("UDP"));
        udpProtocolButton.setOnAction(e -> {
            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "This is upcoming feature. I'll add it soon.",
                    ButtonType.OK);
            alert.setTitle("Host " + serverPort);
            alert.setHeaderText("Information");
            alert.showAndWait();
        });
        mainGridPane.add(udpProtocolButton,0,1);

        //application name
        Text applicationNameTextOne = new Text("TOR");
        applicationNameTextOne.setId("namePartOne");
        Text applicationNameTextTwo = new Text("rent");
        applicationNameTextTwo.setId("namePartTwo");
        TextFlow applicationNameFlow = new TextFlow();
        applicationNameFlow.getChildren().add(applicationNameTextOne);
        applicationNameFlow.getChildren().add(applicationNameTextTwo);
        applicationNameFlow.setId("applicationNameFlow");
        mainGridPane.add(applicationNameFlow,1,0,1,2);

        //port number
        Text portTextOne = new Text("YOUR PORT\n");
        portTextOne.setId("portTextOne");
        portTextTwo = new Text(Integer.toString(serverPort));
        portTextTwo.setId("portTextTwo");
        portFlow = new TextFlow();
        portFlow.getChildren().add(portTextOne);
        portFlow.getChildren().add(portTextTwo);
        portFlow.setId("portFlow");
        mainGridPane.add(portFlow,2,0,1,2);

        //student number
        Label studentNumber = new Label("s21041");
        studentNumber.setId("studentNumberLabel");
        mainGridPane.add(studentNumber,1,2);

        //action buttons
        Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("actionButton");
        sendButton.setOnAction(e -> changeToSendScene());
        mainGridPane.setMargin(sendButton, new Insets(50, 0, 20, 0));
        mainGridPane.add(sendButton,1,3);

        Button downloadButton = new Button("Download");
        downloadButton.getStyleClass().add("actionButton");
        downloadButton.setOnAction(e -> changeToDownloadScene());
        mainGridPane.setMargin(downloadButton, new Insets(10, 0, 20, 0));
        mainGridPane.add(downloadButton,1,4);

        exitButton = new Button("Exit");
        exitButton.getStyleClass().add("actionButton");
        exitButton.setOnAction(e -> exitAction());
        mainGridPane.setMargin(exitButton, new Insets(40, 0, 10, 0));
        mainGridPane.add(exitButton,1,5);

        vBox.getChildren().add(mainGridPane);
        Scene scene = new Scene(vBox, 700, 550);
        primaryStage.getIcons().add(new Image("icon.jpg"));
        primaryStage.setScene(scene);

        primaryStage.setTitle("TORrent - host " + serverPort);
        primaryStage.setMinHeight(550);
        primaryStage.setMinWidth(700);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                System.exit(0);
            }
        });

        primaryStage.show();

        //loading CSS
        scene.getStylesheets().add("style.css");
    }

    private void setMainMenuPane(){
        vBox.getChildren().clear();
        vBox.getChildren().add(mainGridPane);

    }

    private void startTCPserver() {
        try {
            tcp_server = new TCP_Server();
            serverPort = tcp_server.getPortNumber();
//            portFlow.getChildren().remove(portTextTwo);
            if (portFlow != null){
                portFlow.getChildren().remove(portTextTwo);
                portTextTwo = new Text(Integer.toString(serverPort));
                portTextTwo.setStyle("-fx-font-weight: 400;\n" +
                        "    -fx-font-size: 30px;\n" +
                        "    -fx-fill: #c3c3c3;");
                portFlow.getChildren().add(portTextTwo);
            }
            new Thread(() -> {
                try {
                    tcp_server.waitForClients();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //upcoming feature
    private void changeProtocol(String selectedProtocol){
        if ((selectedProtocol.equals("TCP")) && (!actualProtocol.equals(selectedProtocol))){
            startTCPserver();
            actualProtocol = "TCP";
            tcpProtocolButton.setStyle(
                    "-fx-background-color: #00ecff;\n" +
                    "-fx-text-fill: #161616;");
            udpProtocolButton.setStyle(
                    "-fx-text-fill: #cbcbcb;" +
                    "-fx-background-color: rgba(255, 255, 255, 0.1);");
        } else if ((selectedProtocol.equals("UDP")) && (!actualProtocol.equals(selectedProtocol))){
            try {
                tcp_server.closeServer();
                System.out.println("TCP Server closed.");
                actualProtocol = "UDP";

                udpProtocolButton.setStyle(
                        "-fx-background-color: #00ecff;\n" +
                                "-fx-text-fill: #161616;");
                tcpProtocolButton.setStyle(
                        "-fx-text-fill: #cbcbcb;" +
                                "-fx-background-color: rgba(255, 255, 255, 0.1);");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void changeToSendScene() {
        vBox.getChildren().clear();

        GridPane sendGridPane = new GridPane();
        sendGridPane.setVgap(20);
        sendGridPane.setHgap(20);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        sendGridPane.getColumnConstraints().addAll(col,col);

        TextField receiverPort = new TextField();
        receiverPort.setPromptText("(reveiver's port)");
        receiverPort.setFocusTraversable(false);
        sendGridPane.add(receiverPort, 0,0);

        Button acceptButton = new Button("Send");
        acceptButton.setStyle("-fx-background-color: #00ecff; -fx-font-size: 25; -fx-text-fill: white;");
        acceptButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        sendGridPane.add(acceptButton,1,0,1,2);

        Label label = new Label("Choose a file You want to send");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 18;");
        sendGridPane.add(label, 0,1);

        TableView<File> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<File, String> column = new TableColumn<>("Name");
        column.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.getColumns().add(column);

        String pathToFiles;
        if (actualProtocol.equals("TCP")){
            pathToFiles = System.getProperty("user.home")+ "\\Desktop\\TORrent\\TCP\\" + tcp_server.getPortNumber();
        } else {
            pathToFiles = "";
        }
        File dir = new File(pathToFiles);
        File[] files = dir.listFiles();
        table.getItems().addAll(files);
        sendGridPane.add(table,0,2,2,1);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> setMainMenuPane());
        sendGridPane.add(backButton,0,3);

        acceptButton.setOnAction(e -> {
            File selectedFile = table.getSelectionModel().getSelectedItem();
            String typedPort = receiverPort.getText();
            Pattern pattern = Pattern.compile("^[0-9]{5}$");
            Matcher matcher = pattern.matcher(typedPort);
            if ((matcher.find()) && (selectedFile != null)){
                sendFile(Integer.parseInt(typedPort),selectedFile);
            }
            else {
                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        "Wrong receiver's port number or file not selected.\n" +
                                "Type legal port and select file from list.",
                        ButtonType.OK);
                alert.setTitle("Host " + serverPort);
                alert.setHeaderText("Error!");
                alert.showAndWait();
            }
        });
        vBox.getChildren().add(sendGridPane);
    }

    private void changeToDownloadScene() {
        vBox.getChildren().clear();

        GridPane downloadGridPane = new GridPane();
        downloadGridPane.setVgap(20);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(33);
        downloadGridPane.getColumnConstraints().addAll(col,col,col);

        TextField serverPort = new TextField();
        serverPort.setPromptText("(server port)");
        serverPort.setFocusTraversable(false);
        downloadGridPane.add(serverPort, 1,0);

        Button connectButton = new Button("Connect");
        connectButton.setStyle("-fx-background-color: #00ecff; -fx-font-size: 25; -fx-text-fill: white;");
        connectButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        downloadGridPane.add(connectButton,1,1);
        connectButton.setOnAction(e -> {
            String typedPort = serverPort.getText();
            Pattern pattern = Pattern.compile("^[0-9]{5}$");
            Matcher matcher = pattern.matcher(typedPort);
            if (matcher.find()){
                connetToServer(Integer.parseInt(typedPort));
            }
            else {
                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        "Wrong server port format.\n",
                        ButtonType.OK);
                alert.setTitle("Host " + this.serverPort);
                alert.setHeaderText("Error!");
                alert.showAndWait();
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> setMainMenuPane());
        downloadGridPane.add(backButton,0,0);

        vBox.getChildren().add(downloadGridPane);
    }

    TCP_Client tcpClientForDownload;
    private void connetToServer(int serverPort) {
        tcpClientForDownload = new TCP_Client(serverPort);

        //checking if any server is working on this port
        if (tcpClientForDownload.portCorrect){
            String[] namesList = tcpClientForDownload.getFilesList();
            showDownloadTable(namesList);
        } else{
            Alert alert = new Alert(
                    Alert.AlertType.ERROR,
                    "No server is working on this port.",
                    ButtonType.OK);
            alert.setTitle("Host " + this.serverPort);
            alert.setHeaderText("Error!");
            alert.showAndWait();
        }
    }

    private void showDownloadTable(String[] namesList) {
        vBox.getChildren().clear();

        GridPane downloadTableGridPane = new GridPane();
        downloadTableGridPane.setVgap(20);
        downloadTableGridPane.setHgap(20);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        downloadTableGridPane.getColumnConstraints().addAll(col,col);

        Label label = new Label("Choose a file You want to download");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 18;");
        downloadTableGridPane.add(label, 0,0);

        Button acceptButton = new Button("Download");
        acceptButton.setStyle("-fx-background-color: #00ecff; -fx-font-size: 25; -fx-text-fill: white;");
        acceptButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        downloadTableGridPane.add(acceptButton,1,0);

        TableView<String> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<String, String> column = new TableColumn<>("Name");
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<String, String> data) {
                return new ReadOnlyStringWrapper(data.getValue());
            }
        });
        table.getColumns().add(column);
        table.getItems().addAll(namesList);
        downloadTableGridPane.add(table,0,1,2,1);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> setMainMenuPane());
        downloadTableGridPane.add(backButton,0,2);

        acceptButton.setOnAction(e -> {
            String selectedFile = table.getSelectionModel().getSelectedItem();
            if (selectedFile != null){
                System.out.println("!= null");
                tcpClientForDownload.downloadFile(selectedFile, serverPort);
            } else {
                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        "Select a file!",
                        ButtonType.OK);
                alert.setTitle("Host " + serverPort);
                alert.showAndWait();
            }
        });

        vBox.getChildren().add(downloadTableGridPane);
    }

    private void sendFile(int typedPort, File selectedFile) {
        if(actualProtocol == "TCP"){
            TCP_Client client = new TCP_Client(typedPort, "Sending");
            if (client.portCorrect){
                boolean isFileSent = client.sendFile(selectedFile);
                if (isFileSent){
                    Alert alert = new Alert(
                            Alert.AlertType.INFORMATION,
                            "File sent.",
                            ButtonType.OK);
                    alert.setTitle("Host " + serverPort);
                    alert.setHeaderText("Success!");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        "File transfer filed. Check if typed port is correct.",
                        ButtonType.OK);
                alert.setTitle("Host " + serverPort);
                alert.setHeaderText("Error!");
                alert.showAndWait();
            }
        } else {
            //place for UDP
        }
    }

    private void exitAction() {

        //maybe I'll find way to correctly close all streams and sockets
        //also it can be a good idea to delete own folder after exit

        System.exit(0);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
