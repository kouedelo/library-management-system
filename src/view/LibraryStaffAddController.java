/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import dao.AlertMaker;
import dao.DataHelper;
import dao.DatabaseHandler;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.LibraryStaff;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * FXML Controller class
 *
 * @author user
 */
public class LibraryStaffAddController implements Initializable {
@FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane mainContainer;

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

    @FXML
    private JFXTextField usrname;

    @FXML
    private JFXPasswordField pasword;

    
    private DatabaseHandler databaseHandler;
    
    private Boolean isInEditMode = Boolean.FALSE;

    @FXML
    void addLibraryStaff(ActionEvent event) {
        String libfName = fname.getText();
        String liblName = lname.getText();
        String libStafID = stafID.getText();
        String libUsrname = usrname.getText();
        String libPasword = pasword.getText();//DigestUtils.shaHex(pasword.getText());
        String libStafType = stafType.getSelectionModel().getSelectedItem();

        if (libfName.isEmpty() || liblName.isEmpty() || libStafID.isEmpty() || libUsrname.isEmpty() || libPasword.isEmpty() || bdate.getValue() == null || libStafType == null) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Insufficient Data", "Please enter data in all fields.");
            return;
        }
        Date libBdate = java.sql.Date.valueOf(bdate.getValue());
        
        if (isInEditMode) {
            handleEditOperation();
            return;
        }

        if (DataHelper.isLibraryStaffExists(libStafID)) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Duplicate staff id", "Library staff with same Staff ID exists.\nPlease use new ID");
            return;
        }
        LibraryStaff libraryStaff= new LibraryStaff(libfName, liblName, libBdate, libStafID, libStafType, libUsrname, libPasword);
        boolean result = DataHelper.insertNewLibraryStaff(libraryStaff);
        if (result) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "New library staff added",libUsrname + " has been added");
            clearEntries();
            refreshStype();
        } else {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to add new book", "Check all the entries and try again");
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        stafType.setItems(FXCollections.observableArrayList(getSType()));
    }    
    
    private void refreshStype(){
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
    
    private void checkData() {
        String qu = "SELECT staffID FROM Library_staff";
        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while (rs.next()) {
                String titlex = rs.getString("staffID");
                System.out.println(titlex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(LibraryStaffAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void inflateUI(LibraryStaff libraryStaff) {
        fname.setText(libraryStaff.getFName());
        lname.setText(libraryStaff.getLNname());
        bdate.setValue(libraryStaff.getBirthDate().toLocalDate());
        stafID.setText(libraryStaff.getStaffID());
        stafType.getSelectionModel().select(libraryStaff.getStaffType());
        usrname.setText(libraryStaff.getUsername());
        pasword.setText(libraryStaff.getPassword());
        stafID.setEditable(false);
        //bookType.setValue(EBookType.can_be_lent);
        isInEditMode = Boolean.TRUE;
    }

    private void clearEntries() {
        fname.clear();
        lname.clear();
        bdate.getEditor().clear();
        stafID.clear();
        stafType.getItems().clear();
        usrname.clear();
        pasword.clear();
    }

    private void handleEditOperation() {
        LibraryStaff libraryStaff = new LibraryStaff(fname.getText(), lname.getText(), java.sql.Date.valueOf(bdate.getValue()), stafID.getText(), stafType.getSelectionModel().getSelectedItem(), usrname.getText(), pasword.getText().toString());
        if (databaseHandler.updateLibraryStaff(libraryStaff)) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Success", "Update complete");
            clearEntries();
        } else {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed", "Could not update data");
        }
    }
}
