/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import dao.DatabaseHandler;
import dao.LibraryAssistantUtil;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.LibraryStaff;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author user
 */
public class LoginController implements Initializable {

    private final static Logger LOGGER = LogManager.getLogger(LoginController.class.getName());

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    private Preferences preference;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preference = Preferences.getPreferences();
        dao.DatabaseHandler.getInstance().getPreferences();
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String uname = username.getText();
        String pword = password.getText();
        LibraryStaff lstStaff;

        lstStaff = DatabaseHandler.getInstance().returnUser(uname, pword);
        if (lstStaff != null) {
            closeStage();
            loadMain(lstStaff);
            LOGGER.log(Level.INFO, "User successfully logged in {}", uname);
        } else {
            username.getStyleClass().add("wrong-credentials");
            password.getStyleClass().add("wrong-credentials");
        }
        //}
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        System.exit(0);
    }

    private void closeStage() {
        ((Stage) username.getScene().getWindow()).close();
    }

    void loadMain(LibraryStaff staff) {
        try {
            System.out.println("Current Staff Password: " + staff.getPassword() + "\n");
            System.out.println("Current Staff Username: " + staff.getUsername() + "\n");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            Parent parent = loader.load();
            MainController controller = loader.getController();
            controller.currentStaff = staff;
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Library Assistant");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
    }
}
