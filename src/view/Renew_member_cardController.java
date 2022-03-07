/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import dao.AlertMaker;
import dao.DataHelper;
import dao.DatabaseHandler;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.RenewalCard;

/**
 * FXML Controller class
 *
 * @author Kouede Loic
 */
public class Renew_member_cardController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane mainContainer;

    @FXML
    private JFXComboBox<String> memberSsnComboBox;

    @FXML
    private JFXComboBox<String> staffIDComboBox;

    @FXML
    private JFXTextField cardNumberTxtField;

    @FXML
    private JFXDatePicker deliveryDate;

    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXButton cancelButton;

    private DatabaseHandler databaseHandler;

    ObservableList<String> memberIDList = FXCollections.observableArrayList();

    ObservableList<String> staffIDList = FXCollections.observableArrayList();

    @FXML
    void renewMemberCard(ActionEvent event) {
        String cardNumber = cardNumberTxtField.getText();

        if (memberSsnComboBox.getSelectionModel().getSelectedItem() == null || staffIDComboBox.getSelectionModel().getSelectedItem() == null || cardNumber.isEmpty() || deliveryDate.getValue() == null) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Insufficient Data", "Please enter data in all fields.");
            return;
        }
        String ssn = memberSsnComboBox.getSelectionModel().getSelectedItem();
        String staffID = staffIDComboBox.getSelectionModel().getSelectedItem();
        Date delivDate = Date.valueOf(deliveryDate.getValue());
        java.util.Date  date = new java.util.Date(delivDate.getTime());
        Timestamp returnDate = new Timestamp(date.getYear(), date.getMonth(), date.getDate() + 1460, date.getHours(), date.getMinutes(), date.getSeconds(), 0);
        Date expiryDate = new Date(returnDate.getTime());

        RenewalCard card = new RenewalCard(ssn, staffID, cardNumber, delivDate, expiryDate);
        boolean result = DataHelper.insertRenewalCard(card);
        if (result) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Card Renewal Successful", "The card with card number " + card.getCardNumber() + " has been added");
            clearEntries();
            refreshComboBoxies();
        } else {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to renew card", "Check all the entries and try again");
            refreshComboBoxies();
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void refreshComboBoxies() {
        memberSsnComboBox.setItems(FXCollections.observableArrayList(DataHelper.getSSNList()));
        staffIDComboBox.setItems(FXCollections.observableArrayList(DataHelper.getStaffIdList()));
    }

    private ObservableList<String> loadMemberList() {
        memberIDList.clear();

        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT SSN FROM MEMBERS";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String ssn = rs.getString("SSN");

                memberIDList.add(ssn);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Renew_member_cardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return memberIDList;

    }

    private ObservableList<String> loadStaffList() {
        staffIDList.clear();

        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM BOOK";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String staffID = rs.getString("staff_id");

                staffIDList.add(staffID);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Renew_member_cardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return staffIDList;

    }

    private void clearEntries() {
        cardNumberTxtField.clear();
        memberSsnComboBox.getItems().clear();
        staffIDComboBox.getItems().clear();
        deliveryDate.getEditor().clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        databaseHandler = DatabaseHandler.getInstance();
        memberSsnComboBox.setItems(FXCollections.observableArrayList(DataHelper.getSSNList()));
        staffIDComboBox.setItems(FXCollections.observableArrayList(DataHelper.getStaffIdList()));

    }

}
