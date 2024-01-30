package lk.ijse.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private JFXTextField txtUserName;

    @FXML
    void btnLoginOnAction(ActionEvent event) throws IOException {
        String userName = txtUserName.getText();
        if (!userName.isEmpty() && userName.matches("[A-Za-z0-9]+")) {
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/Client_Form.fxml"));
            Parent rootNode = fxmlLoader.load();

            // Retrieve the controller instance associated with the loaded FXML
            ClientFormController controller = fxmlLoader.getController();
            controller.setUserName(userName);

            Scene scene = new Scene(rootNode);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            txtUserName.clear();
        } else {
            new Alert(Alert.AlertType.ERROR, "Please enter your name").show();
        }
    }

}
