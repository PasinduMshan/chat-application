package lk.ijse.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lk.ijse.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ServerFormController implements Initializable {

    @FXML
    private Label lblUserName;

    @FXML
    private ScrollPane scrollPain;

    @FXML
    private VBox vBox;

    Socket socket;

    ServerSocket serverSocket;

    private static VBox vBoxes;

    private List<ClientHandler> client = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        vBoxes = vBox;
        new Thread(()->{
            try {
                receiveMessage("Server Starting...");

                serverSocket = new ServerSocket(3001);

                while (!serverSocket.isClosed()){
                    try{
                        socket = serverSocket.accept();
                        ClientHandler clientHandler = new ClientHandler(socket,client);
                        client.add(clientHandler);
                        System.out.println("client socket accepted " + socket.toString());
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static void receiveMessage(String message) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: bold; -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(0,0,0));

        hBox.getChildren().add(textFlow);

        Platform.runLater(()-> {
            vBoxes.getChildren().add(hBox);
        });
    }


}