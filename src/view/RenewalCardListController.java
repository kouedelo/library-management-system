/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.jfoenix.controls.JFXTextField;
import dao.DatabaseHandler;
import dao.LibraryAssistantUtil;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.RenewalCard;

/**
 * FXML Controller class
 *
 * @author Kouede Loic
 */
public class RenewalCardListController implements Initializable {

    /**
     * Initializes the controller class.
     */
    ObservableList<RenewalCard> list = FXCollections.observableArrayList();

    @FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private TableView<RenewalCard> tableView;

    @FXML
    private TableColumn<RenewalCard, String> ssnCol;

    @FXML
    private TableColumn<RenewalCard, String> StaffIDCol;

    @FXML
    private TableColumn<RenewalCard, String> cardNumberCol;

    @FXML
    private TableColumn<RenewalCard, Date> deliveryDateCol;

    @FXML
    private TableColumn<RenewalCard, Date> expiryDateCol;

    @FXML
    private JFXTextField searchBar;

    @FXML
    void searchRenewal(ActionEvent event) {
        ObservableList<RenewalCard> newlist = FXCollections.observableArrayList();
        loadData();
        for (RenewalCard card : list) {
            if (card.getSsn().toLowerCase().contains(searchBar.getText())) {
                newlist.add(card);
            }
        }
        tableView.setItems(newlist);
    }

    @FXML
    void closeStage(ActionEvent event) {
        getStage().close();
    }

    private Stage getStage() {
        return (Stage) tableView.getScene().getWindow();
    }

    private void initCol() {
        ssnCol.setCellValueFactory(new PropertyValueFactory<>("ssn"));
        StaffIDCol.setCellValueFactory(new PropertyValueFactory<>("staffID"));
        cardNumberCol.setCellValueFactory(new PropertyValueFactory<>("cardNumber"));
        deliveryDateCol.setCellValueFactory(new PropertyValueFactory<>("renewalDate"));
        expiryDateCol.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
    }

    private void loadData() {
        list.clear();
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM renewal_card";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String ssn = rs.getString("SSN");
                String staffId = rs.getString("Staff_id");
                String cardNumber = rs.getString("Card_number");
                Date renewalDate = rs.getDate("Delivery_date");
                Date expiryDate = rs.getDate("Expiry_date");
                list.add(new RenewalCard(ssn, staffId, cardNumber, renewalDate, expiryDate));

            }
        } catch (SQLException ex) {
            Logger.getLogger(RenewalCardListController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableView.setItems(list);
    }

    @FXML
    void exportAsPDF(ActionEvent event) {
        List<List> printData = new ArrayList<>();
        String[] headers = {" Member SSN   ", " Staff ID ", " Renewed Card Number  ", " Date Of Renewal ", " Card Expiry Date  "};
        printData.add(Arrays.asList(headers));
        for (RenewalCard renewedCard : list) {
            List<String> row = new ArrayList<>();
            row.add(renewedCard.getSsn());
            row.add(renewedCard.getStaffID());
            row.add(renewedCard.getCardNumber());
            row.add(String.valueOf(renewedCard.getRenewalDate()));
            row.add(String.valueOf(renewedCard.getExpiryDate()));
            printData.add(row);
        }
        LibraryAssistantUtil.initPDFExprot(rootPane, contentPane, getStage(), printData);
    }

    @FXML
    void handleMemberDelete(ActionEvent event) {

    }

    @FXML
    void handleMemberEdit(ActionEvent event) {

    }

    @FXML
    void handleRefresh(ActionEvent event) {
        loadData();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initCol();
        loadData();
    }

}
