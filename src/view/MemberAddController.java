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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Member;

public class MemberAddController implements Initializable {

    DatabaseHandler handler;
    @FXML
    private JFXDatePicker birthdate;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;

    private Boolean isInEditMode = false;
    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane mainContainer;
    @FXML
    private JFXTextField ssn;

    @FXML
    private JFXTextField phoneNumber;

    @FXML
    private JFXTextField campusMailAddress;

    @FXML
    private JFXTextField homeMailAddress;

    @FXML
    private JFXComboBox<Boolean> isAProfessor;

    @FXML
    private JFXTextField Department;

    @FXML
    private JFXTextField Fname;

    @FXML
    private JFXTextField Lname;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
        List<Boolean> profList = new ArrayList<>();
        profList.add(true);
        profList.add(false);
        isAProfessor.setItems(FXCollections.observableArrayList(profList));
    }
    
    private void refershIsProfessor(){
        List<Boolean> profList = new ArrayList<>();
        profList.add(true);
        profList.add(false);
        isAProfessor.setItems(FXCollections.observableArrayList(profList));
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) Fname.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void addMember(ActionEvent event) {
        String FName = Fname.getText();
        String LName = Lname.getText();
        String mSsn = ssn.getText();
        String mPhoneNumber = phoneNumber.getText();
        String mCmail = campusMailAddress.getText();
        String mHmail = homeMailAddress.getText();
        Boolean mType = isAProfessor.getSelectionModel().getSelectedItem();
        String mDepartment = Department.getText();
        int maxDays = 0;

        Boolean flag = FName.isEmpty() || FName.isEmpty() || mSsn.isEmpty() || mPhoneNumber.isEmpty() || mCmail.isEmpty() || mHmail.isEmpty() || birthdate.getValue() == null || (mType && mDepartment.isEmpty());
        if (flag) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Insufficient Data", "Please enter data in all fields.");
            return;
        }
        Date mBdate = java.sql.Date.valueOf(birthdate.getValue());

        if (isInEditMode) {
            handleUpdateMember();
            clearEntries();
            return;
        }

        if (DataHelper.isMemberExists(mSsn)) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Duplicate member ssn", "Member with same ssn exists.\nPlease use new ssn");
            return;
        }
        
        if(mType){
            maxDays = 94;
        }else{
            maxDays = 30;
        }

        Member member = new Member(mSsn, FName, LName, mBdate, mPhoneNumber, mHmail, mHmail, mType, mDepartment, maxDays);
        boolean result = DataHelper.insertNewMember(member);
        if (result) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "New member added", FName + " has been added");
            clearEntries();
            refershIsProfessor();
        } else {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to add new member", "Check you entries and try again.");
        }
    }

    public void infalteUI(Member member) {
        Fname.setText(member.getFname());
        Lname.setText(member.getLname());
        ssn.setText(member.getSsn());
        ssn.setEditable(false);
        birthdate.setValue(member.getBirthDate().toLocalDate());
        phoneNumber.setText(member.getPhoneNumber());
        campusMailAddress.setText(member.getCampusMailAddress());
        homeMailAddress.setText(member.getHomeMailAddress());
        isAProfessor.setValue(member.getIsAProfessor());
        Department.setText(member.getDepartment());
        isInEditMode = Boolean.TRUE;
    }

    private void clearEntries() {
        Fname.clear();
        Lname.clear();
        ssn.clear();
        birthdate.getEditor().clear();
        phoneNumber.clear();
        campusMailAddress.clear();
        homeMailAddress.clear();
        isAProfessor.getItems().clear();
        Department.clear();
    }

    private void handleUpdateMember() {
        Member member = new Member(ssn.getText(), Fname.getText(), Lname.getText(), java.sql.Date.valueOf(birthdate.getValue()), phoneNumber.getText(), campusMailAddress.getText(), homeMailAddress.getText(), isAProfessor.getSelectionModel().getSelectedItem(), Department.getText(), 0);
        if(member.getIsAProfessor()){
            member.setMaxDays(94);
        }else{
            member.setMaxDays(30);
        }
        if (DatabaseHandler.getInstance().updateMember(member)) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Success", "Member data updated.");
            refershIsProfessor();
        } else {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed", "Cannott update member.");
            refershIsProfessor();
        }
    }

}
