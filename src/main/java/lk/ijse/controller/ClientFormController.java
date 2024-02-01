package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.emoji.EmojiPicker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
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
    private JFXButton btnEmoji;

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

        this.vBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                scrollPain.setVvalue((Double) t1);
            }
        });
    }

    public void receiveMessage(String message , VBox vBox) {
        if (message.matches(".*\\.(png|jpe?g|gif)$")) {
            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);
            Text txtName = new Text(message.split("[-]")[0]);
            TextFlow textFlow = new TextFlow(txtName);
            hBoxName.getChildren().add(textFlow);

            Image image = new Image(message.split("[-]")[1]);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            HBox hBoxImage = new HBox();
            hBoxImage.setAlignment(Pos.CENTER_LEFT);
            hBoxImage.setPadding(new Insets(5,5,5,10));
            hBoxImage.getChildren().add(imageView);

            Platform.runLater(()->{
                vBox.getChildren().add(hBoxName);
                vBox.getChildren().add(hBoxImage);
            });

        } else {

            String name = message.split("-")[0];
            String messageToServer = message.split("-")[1];
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5, 5, 5, 10));

            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);
            Text txtName = new Text(name);
            TextFlow txtFlowName = new TextFlow(txtName);
            hBoxName.getChildren().add(txtFlowName);

            Text text = new Text(messageToServer);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: bold; -fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5, 10, 5, 10));
            text.setFill(Color.color(0, 0, 0));

            hBox.getChildren().add(textFlow);

            Platform.runLater(() -> {
                vBox.getChildren().add(hBoxName);
                vBox.getChildren().add(hBox);
            });
        }
    }

    @FXML
    void btnCameraOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png","*.jpg","*.gif","*.bmp","*.jpeg")
        );
        Stage stage = (Stage) rootNode.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String sendImage = file.toURI().toString();
            sendImageToClient(sendImage);
        }
    }

    private void sendImageToClient(String sendImage) {
        HBox hBoxName = new HBox();
        hBoxName.setAlignment(Pos.CENTER_RIGHT);
        Text textName = new Text("Me");
        TextFlow textFlowName = new TextFlow(textName);
        hBoxName.getChildren().add(textFlowName);

        Image image = new Image(sendImage);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5,5,5,10));
        hBox.getChildren().add(imageView);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        vBox.getChildren().add(hBoxName);
        vBox.getChildren().add(hBox);

        try {
            dataOutputStream.writeUTF(userName + "-" + sendImage);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnEmojiOnAction(ActionEvent event) {
        EmojiPicker emojiPicker = new EmojiPicker();

        VBox vBox = new VBox(emojiPicker);
        vBox.setPrefSize(150,300);
        vBox.setLayoutX(30);
        vBox.setLayoutY(380);
        vBox.setStyle("-fx-font-size: 35");

        rootNode.getChildren().add(vBox);

        emojiPicker.setVisible(false);

        btnEmoji.setOnAction(mouseEvent ->{
            if (emojiPicker.isVisible()) {
                emojiPicker.setVisible(false);
            } else {
                emojiPicker.setVisible(true);
            }
        });

        emojiPicker.getEmojiListView().setOnMouseClicked(mouseEvent -> {
            String selectedEmoji = emojiPicker.getEmojiListView().getSelectionModel().getSelectedItem();
            if (selectedEmoji != null) {
                txtMassage.setText(txtMassage.getText()+selectedEmoji);
            }
            emojiPicker.setVisible(false);
        });
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        String msg = txtMassage.getText();

        if (!msg.isEmpty()) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5,5,5,10));


            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_RIGHT);
            Text textName = new Text("Me");
            TextFlow textFlowName = new TextFlow(textName);
            hBoxName.getChildren().add(textFlowName);

            Text text = new Text(msg);
            text.setStyle("-fx-font-size: 14");
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #0693e3; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(1,1,1));

            hBox.getChildren().add(textFlow);

            Platform.runLater(() ->{
                vBox.getChildren().add(hBoxName);
                vBox.getChildren().add(hBox);
            });

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

    public void shutdown() {
        ServerFormController.receiveMessage(userName + " left the chat.");
    }

    @FXML
    void txtMessageSendOnAction(ActionEvent event) {
        btnSendOnAction(event);
    }
}
