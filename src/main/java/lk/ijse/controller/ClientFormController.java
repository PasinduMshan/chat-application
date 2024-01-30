package lk.ijse.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ClientFormController implements Initializable {

    @FXML
    private Label lblUserName;

    @FXML
    private AnchorPane rootNode;

    @FXML
    private ScrollPane scrollPain;

    @FXML
    private JFXTextField txtMassage;

    @FXML
    private VBox vBox;

    private String userName;

    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    Socket socket;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(()->{
            try {
                socket = new Socket("localhost", 5000);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                ServerFormController.receiveMessage(userName + " Joined to the chat!");

                while (socket.isConnected()) {
                    String message = dataInputStream.readUTF();
                    receiveMessage(message, ClientFormController.this.vBox);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void receiveMessage(String message , VBox vBox) {
        String name = message.split("-")[0];
        String messageToServer = message.split("-")[1];
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        HBox hBoxName = new HBox();
        hBoxName.setAlignment(Pos.CENTER_LEFT);
        Text txtName = new Text(name);
        TextFlow txtFlowName = new TextFlow(txtName);
        hBoxName.getChildren().add(txtFlowName);

        Text text = new Text(messageToServer);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: bold; -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5,10,5,10));
        text.setFill(Color.color(0,0,0));

        hBox.getChildren().add(textFlow);

        Platform.runLater(()->{
            vBox.getChildren().add(hBoxName);
            vBox.getChildren().add(hBox);
        });

    }

    @FXML
    void btnCameraOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png","*.jpg","*.gif","*.bmp","*jpeg")
        );
        Stage stage = (Stage) rootNode.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String sendImage = file.toURI().toString();
            sendImageToClient(sendImage);
        }
    }

    private void sendImageToClient(String sendImage) {

    }

    @FXML
    void btnEmojiOnAction(ActionEvent event) {

    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        String msg = txtMassage.getText();

        if (!msg.isEmpty()) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5,5,0,10));

            Text text = new Text(msg);
            text.setStyle("-fx-font-size: 14");
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #0693e3; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(1,1,1));

            hBox.getChildren().add(textFlow);

            HBox hBoxTime = new HBox();
            hBoxTime.setAlignment(Pos.CENTER_RIGHT);
            hBoxTime.setPadding(new Insets(0,5,5,10));
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            Text txtTime = new Text(time);
            txtTime.setStyle("-fx-font-size: 8");

            hBoxTime.getChildren().add(txtTime);

            vBox.getChildren().add(hBox);
            vBox.getChildren().add(txtTime);

            try {
                dataOutputStream.writeUTF(userName + "-" + msg );
                dataOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            txtMassage.clear();
        }
    }

    public void setUserName(String name) {
        this.userName = name;
        lblUserName.setText(userName);
    }
}
