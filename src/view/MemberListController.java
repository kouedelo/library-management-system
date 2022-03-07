package view;

import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Member;

public class MemberListController implements Initializable {

    ObservableList<Member> list = FXCollections.observableArrayList();

    @FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private TableView<Member> tableView;

    @FXML
    private TableColumn<Member, String> FnameCol;

    @FXML
    private TableColumn<Member, String> LnameCol;

    @FXML
    private TableColumn<Member, String> idCol;

    @FXML
    private TableColumn<Member, String> phoneNumberCol;

    @FXML
    private TableColumn<Member, String> homeAddressCol;

    @FXML
    private TableColumn<Member, String> campusAddressCol;

    @FXML
    private TableColumn<Member, Date> birthDateCol;

    @FXML
    private TableColumn<Member, Boolean> professorCol;

    @FXML
    private TableColumn<Member, String> departmentCol;
    
     @FXML
    private JFXTextField searchBar;

     
    @FXML
    void searchMember(ActionEvent event) {
        ObservableList<Member> newlist = FXCollections.observableArrayList();
        loadData();
        for (Member member : list) {
            if(member.getFname().toLowerCase().contains(searchBar.getText()))
                newlist.add(member);
        }
        tableView.setItems(newlist);
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    private void initCol() {
        FnameCol.setCellValueFactory(new PropertyValueFactory<>("Fname"));
        LnameCol.setCellValueFactory(new PropertyValueFactory<>("Lname"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("ssn"));
        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        homeAddressCol.setCellValueFactory(new PropertyValueFactory<>("homeMailAddress"));
        campusAddressCol.setCellValueFactory(new PropertyValueFactory<>("campusMailAddress"));
        birthDateCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        professorCol.setCellValueFactory(new PropertyValueFactory<>("isAProfessor"));
    }

    private Stage getStage() {
        return (Stage) tableView.getScene().getWindow();
    }

    private void loadData() {
        list.clear();

        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM MEMBERS";
        try {
            System.out.println("SQL Connection to database established!");
            ResultSet rs = handler.execQuery(qu);//stmt.executeQuery(qu);
            while (rs.next()) {
                String ssn = rs.getString("SSN");
                String phoneNumber = rs.getString("Phone_number");
                String homeMailAddress = rs.getString("home_address");
                String campusMailAddress = rs.getString("campus_address");
                String Lname = rs.getString("Lname");
                String Fname = rs.getString("Fname");
                Date birthDate = rs.getDate("Birth_date");
                Boolean isAProfessor = rs.getBoolean("Professor");
                String department = rs.getString("department");
                int maxDays = rs.getInt("Max_days");
                list.add(new Member(ssn, Fname, Lname, birthDate, phoneNumber, campusMailAddress, homeMailAddress, isAProfessor, department, maxDays));

            }
        } catch (SQLException ex) {
            Logger.getLogger(BookAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableView.setItems(list);
    }

    @FXML
    private void handleMemberDelete(ActionEvent event) {
        //Fetch the selected row
        Member selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if (selectedForDeletion == null) {
            AlertMaker.showErrorMessage("No member selected", "Please select a member for deletion.");
            return;
        }
        if (DatabaseHandler.getInstance().isMemberHasAnyBooks(selectedForDeletion)) {
            AlertMaker.showErrorMessage("Cant be deleted", "This member has some books.");
            return;
        }
        Optional<ButtonType> answer = AlertMaker.showConfirmationMessage("Deleting member", "Are you sure want to delete " + selectedForDeletion.getFname() + " " + selectedForDeletion.getLname() + " ?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            Boolean result = DatabaseHandler.getInstance().deleteMember(selectedForDeletion);
            if (result) {
                AlertMaker.showSimpleAlert("Member deleted", selectedForDeletion.getFname() + " " +selectedForDeletion.getLname() + " was deleted successfully.");
                list.remove(selectedForDeletion);
            } else {
                AlertMaker.showSimpleAlert("Failed", selectedForDeletion.getFname() + " " + selectedForDeletion.getLname() + " could not be deleted");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void handleMemberEdit(ActionEvent event) {
        //Fetch the selected row
        Member selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            AlertMaker.showErrorMessage("No member selected", "Please select a member for edit.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/member_add.fxml"));
            Parent parent = loader.load();

            MemberAddController controller = (MemberAddController) loader.getController();
            controller.infalteUI(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);

            stage.setOnHiding((e) -> {
                handleRefresh(new ActionEvent());
            });

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void exportAsPDF(ActionEvent event) {
        List<List> printData = new ArrayList<>();
        String[] headers = {"   FName    ", "   LName    ", "SSN", "phoneNumber", "    campusMailAddress   "};
        printData.add(Arrays.asList(headers));
        for (Member member : list) {
            List<String> row = new ArrayList<>();
            row.add(member.getFname());
            row.add(member.getLname());
            row.add(member.getSsn());
            row.add(member.getPhoneNumber());
            row.add(member.getCampusMailAddress());
            printData.add(row);
        }
        LibraryAssistantUtil.initPDFExprot(rootPane, contentPane, getStage(), printData);
    }

    @FXML
    private void closeStage(ActionEvent event) {
        getStage().close();
    }
}
