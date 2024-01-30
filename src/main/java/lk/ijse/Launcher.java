package lk.ijse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Server_Form.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();

        Parent rootNode = FXMLLoader.load(this.getClass().getResource("/view/Login_Form.fxml"));
        Scene scene1 = new Scene(rootNode);
        Stage stage1 = new Stage();
        stage1.setScene(scene1);
        stage1.centerOnScreen();
        stage1.setResizable(false);
        stage1.show();
    }
}
