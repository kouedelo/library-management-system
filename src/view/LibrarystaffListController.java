/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import dao.AlertMaker;
import dao.DatabaseHandler;
import dao.LibraryAssistantUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.LibraryStaff;

/**
 * FXML Controller class
 *
 * @author user
 */
public class LibrarystaffListController implements Initializable {

    ObservableList<LibraryStaff> list = FXCollections.observableArrayList();
    @FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private TableView<LibraryStaff> tableView;

    @FXML
    private TableColumn<LibraryStaff, String> fnameCol;

    @FXML
    private TableColumn<LibraryStaff, String> lnameCol;

    @FXML
    private TableColumn<LibraryStaff, Date> bdateCol;

    @FXML
    private TableColumn<LibraryStaff, String> staffIDCol;

    @FXML
    private TableColumn<LibraryStaff, String> staffTypeCol;

    @FXML
    private TableColumn<LibraryStaff, String> usernameCol;
    
    @FXML
    private TableColumn<LibraryStaff, String> passwordCol;

    @FXML
    void closeStage(ActionEvent event) {
        getStage().close();
    }

    private Stage getStage() {
        return (Stage) tableView.getScene().getWindow();
    }

    private void initCol() {
        fnameCol.setCellValueFactory(new PropertyValueFactory<>("fName"));
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("lNname"));
        bdateCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        staffIDCol.setCellValueFactory(new PropertyValueFactory<>("staffID"));
        staffTypeCol.setCellValueFactory(new PropertyValueFactory<>("staffType"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
    }

    private void loadData() {
        list.clear();
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM library_staff";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String fnamex = rs.getString("Fname");
                String lnamex = rs.getString("Lname");
                Date bdate = rs.getDate("Birth_date");
                String stafID = rs.getString("Staff_id");
                String stafType = rs.getString("Staff_type");
                String usrname = rs.getString("Username");
                String pasWord = rs.getString("Password");
                list.add(new LibraryStaff(fnamex, lnamex, bdate, stafID, stafType, usrname, pasWord));

            }
        } catch (SQLException ex) {
            Logger.getLogger(LibrarystaffListController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableView.setItems(list);
    }

    @FXML
    void exportAsPDF(ActionEvent event) {
        List<List> printData = new ArrayList<>();
        String[] headers = {" First Name   ", " Last Name ", "  Birthdate  ", "  Staff ID ", " Staff Type  ", "  Username  ", "  Password  "};
        printData.add(Arrays.asList(headers));
        for (LibraryStaff libraryStaff : list) {
            List<String> row = new ArrayList<>();
            row.add(libraryStaff.getFName());
            row.add(libraryStaff.getLNname());
            row.add(String.valueOf(libraryStaff.getBirthDate()));
            row.add(libraryStaff.getStaffID());
            row.add(libraryStaff.getStaffType());
            row.add(libraryStaff.getUsername());
            row.add(libraryStaff.getPassword());
            printData.add(row);
        }
        LibraryAssistantUtil.initPDFExprot(rootPane, contentPane, getStage(), printData);
    }

    @FXML
    void handleLibraryStaffDeleteOption(ActionEvent event) {
        //Fetch the selected row
        LibraryStaff selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if (selectedForDeletion == null) {
            AlertMaker.showErrorMessage("No Library Staff selected", "Please select a Library Staff for deletion.");
            return;
        }
//        if (DatabaseHandler.getInstance().isLibraryStaffHasAnyBooks(selectedForDeletion)) {
//            AlertMaker.showErrorMessage("Cant be deleted", "This Library Staff has some books.");
//            return;
//        }
        Optional<ButtonType> answer = AlertMaker.showConfirmationMessage("Deleting Library Staff", "Are you sure want to delete " + selectedForDeletion.getFName()+ " " + selectedForDeletion.getLNname()+ " ?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            Boolean result = DatabaseHandler.getInstance().deleteLibraryStaff(selectedForDeletion);
            if (result) {
                AlertMaker.showSimpleAlert("Library Staff deleted", selectedForDeletion.getFName() + selectedForDeletion.getLNname() + " was deleted successfully.");
                list.remove(selectedForDeletion);
            } else {
                AlertMaker.showSimpleAlert("Failed", selectedForDeletion.getFName() + selectedForDeletion.getLNname() + " could not be deleted");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    void handleLibraryStaffEditOption(ActionEvent event) {
        //Fetch the selected row
        LibraryStaff selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            AlertMaker.showErrorMessage("No library staff selected", "Please select a library staff for edit.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add_LibraryStaff.fxml"));
            Parent parent = loader.load();

            LibraryStaffAddController controller = (LibraryStaffAddController) loader.getController();
            controller.inflateUI(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Library Staff");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);

            stage.setOnHiding((e) -> {
                handleRefresh(new ActionEvent());
            });

        } catch (IOException ex) {
            Logger.getLogger(LibrarystaffListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        loadData();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

}
