package view;

import dao.BookReturnCallback;
import dao.LibraryAssistantUtil;
import static dao.LibraryAssistantUtil.setStageIcon;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.LibraryStaff;

public class ToolbarController implements Initializable {

    private BookReturnCallback callback;
    
     private LibraryStaff currentStaff;

    public LibraryStaff getCurrentStaff() {
        return currentStaff;
    }

    public void setCurrentStaff(LibraryStaff currentStaff) {
        this.currentStaff = currentStaff;
    }

    public void setBookReturnCallback(BookReturnCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void loadAddMember(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/member_add.fxml"), "Add New Member", null);
    }

    @FXML
    private void loadAddBook(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/add_book.fxml"), "Add New Book", null);
    }

    @FXML
    private void loadMemberTable(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/member_list.fxml"), "Member List", null);
    }

    @FXML
    private void loadBookTable(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/book_list.fxml"), "Book List", null);
    }

    @FXML
    private void loadSettings(ActionEvent event) {
        try {
            System.out.println(currentStaff.getPassword());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/settings.fxml"));
            Parent parent = loader.load();
            SettingsController controller = loader.getController();
            controller.setCurrentStaff(currentStaff);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Settings");
            stage.setScene(new Scene(parent));
            stage.show();
            setStageIcon(stage);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //LibraryAssistantUtil.loadWindow(getClass().getResource("/view/settings.fxml"), "Settings", null);
    }

    @FXML
    private void loadIssuedBookList(ActionEvent event) {
        Object controller = LibraryAssistantUtil.loadWindow(getClass().getResource("/view/issued_list.fxml"), "Issued Book List", null);
        if (controller != null) {
            IssuedListController cont = (IssuedListController) controller;
            cont.setBookReturnCallback(callback);
        }
    }
@FXML
    void loadLibraryStaffList(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/librarystaff_list.fxml"), "Library Staff List", null);
    }

    @FXML
    void loadRenewedCardList(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/renewal_card_list.fxml"), "Renewed Card List", null);
    }
}
