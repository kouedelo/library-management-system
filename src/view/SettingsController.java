package view;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import dao.AlertMaker;
import dao.DataHelper;
import dao.DatabaseExporter;
import dao.DatabaseHandler;
import dao.LibraryAssistantUtil;
import java.io.File;
import java.net.URL;
import java.security.InvalidParameterException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.LibraryStaff;
import model.MailServerInfo;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SettingsController implements Initializable {

//    @FXML
//    private JFXTextField nDaysWithoutFine;
//    @FXML
//    private JFXTextField finePerDay;
    @FXML
    private JFXTextField username;
    
    @FXML
    private JFXTextField password;
             
    @FXML
    private JFXTextField serverName;
    @FXML
    private JFXTextField smtpPort;
    @FXML
    private JFXTextField emailAddress;

    private final static Logger LOGGER = LogManager.getLogger(DatabaseHandler.class.getName());
    @FXML
    private JFXPasswordField emailPassword;
    @FXML
    private JFXCheckBox sslCheckbox;
    @FXML
    private JFXSpinner progressSpinner;
    
    @FXML
    private JFXTextField fname;

    @FXML
    private JFXTextField lname;

    @FXML
    private JFXDatePicker bdate;

    @FXML
    private JFXTextField stafID;

    @FXML
    private JFXComboBox<String> stafType;
    
    private LibraryStaff currentStaff;

    public LibraryStaff getCurrentStaff() {
        return currentStaff;
    }

    public void setCurrentStaff(LibraryStaff currentStaff) {
        this.currentStaff = currentStaff;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDefaultValues();
        stafType.setItems(FXCollections.observableArrayList(getSType()));
    }
    
    private ArrayList<String> getSType() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Chief Librarian");
        list.add("Departmental Associate Librarian");
        list.add("Reference Librarian");
        list.add("Check-out Staff");
        list.add("Library Assistant");
        return list;
    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
//        int ndays = Integer.parseInt(nDaysWithoutFine.getText());
//        float fine = Float.parseFloat(finePerDay.getText());
//        String uname = username.getText();
//        String pass = password.getText();
//
//        Preferences preferences = Preferences.getPreferences();
//        preferences.setnDaysWithoutFine(ndays);
//        preferences.setFinePerDay(fine);
//        preferences.setUsername(uname);
//        preferences.setPassword(pass);
//
//        Preferences.writePreferenceToFile(preferences);

          currentStaff.setFName(fname.getText());
          currentStaff.setLNname(lname.getText());
          currentStaff.setBirthDate(Date.valueOf(bdate.getValue()));
          currentStaff.setStaffID(stafID.getText());
          currentStaff.setUsername(username.getText());
          currentStaff.setStaffType(stafType.getSelectionModel().getSelectedItem());
          currentStaff.setPassword(password.getText());
          DatabaseHandler.getInstance().updateLibraryStaff(currentStaff);
    }

    private Stage getStage() {
        return ((Stage) fname.getScene().getWindow());
    }

    private void initDefaultValues() {
        Preferences preferences = Preferences.getPreferences();currentStaff = MainController.currentStaff;
        if(currentStaff == null){
            System.out.println("Current Staff is null!");
        }
        fname.setText(currentStaff.getFName());
        lname.setText(currentStaff.getLNname());
        bdate.setValue(LocalDate.parse(String.valueOf(currentStaff.getBirthDate())));
        stafID.setText(currentStaff.getStaffID());
        username.setText(currentStaff.getUsername());
        stafType.getSelectionModel().select(currentStaff.getStaffType());
        password.setText(currentStaff.getPassword());
        //String passHash = String.valueOf(preferences.getPassword());
        //password.setText(passHash.substring(0, Math.min(passHash.length(), 10)));
        loadMailServerConfigurations();
    }

    @FXML
    private void handleTestMailAction(ActionEvent event) {
        MailServerInfo mailServerInfo = readMailSererInfo();
        if (mailServerInfo != null) {
            TestMailController controller = (TestMailController) LibraryAssistantUtil.loadWindow(getClass().getResource("/view/test_mail.fxml"), "Test Email", null);
            controller.setMailServerInfo(mailServerInfo);
        }
    }

    @FXML
    private void saveMailServerConfuration(ActionEvent event) {
        MailServerInfo mailServerInfo = readMailSererInfo();
        if (mailServerInfo != null) {
            if (DataHelper.updateMailServerInfo(mailServerInfo)) {
                AlertMaker.showSimpleAlert("Success", "Saved successfully!");
            } else {
                AlertMaker.showErrorMessage("Failed", "Something went wrong!");
            }
        }
    }

    private MailServerInfo readMailSererInfo() {
        try {
            MailServerInfo mailServerInfo
                    = new MailServerInfo(serverName.getText(), Integer.parseInt(smtpPort.getText()), emailAddress.getText(), emailPassword.getText(), sslCheckbox.isSelected());
            if (!mailServerInfo.validate() || !LibraryAssistantUtil.validateEmailAddress(emailAddress.getText())) {
                throw new InvalidParameterException();
            }
            return mailServerInfo;
        } catch (Exception exp) {
            AlertMaker.showErrorMessage("Invalid Entries Found", "Correct input and try again");
            LOGGER.log(Level.WARN, exp);
        }
        return null;
    }

    private void loadMailServerConfigurations() {
        MailServerInfo mailServerInfo = DataHelper.loadMailServerInfo();
        if (mailServerInfo != null) {
            LOGGER.log(Level.INFO, "Mail server info loaded from DB");
            serverName.setText(mailServerInfo.getMailServer());
            smtpPort.setText(String.valueOf(mailServerInfo.getPort()));
            emailAddress.setText(mailServerInfo.getEmailID());
            emailPassword.setText(mailServerInfo.getPassword());
            sslCheckbox.setSelected(mailServerInfo.getSslEnabled());
        }
    }

    @FXML
    private void handleDatabaseExportAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Location to Create Backup");
        File selectedDirectory = directoryChooser.showDialog(getStage());
        if (selectedDirectory == null) {
            AlertMaker.showErrorMessage("Export cancelled", "No Valid Directory Found");
        } else {
            DatabaseExporter databaseExporter = new DatabaseExporter(selectedDirectory);
            progressSpinner.visibleProperty().bind(databaseExporter.runningProperty());
            new Thread(databaseExporter).start();
        }
    }
}
