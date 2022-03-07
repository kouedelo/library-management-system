package view;

import com.jfoenix.controls.JFXTextField;
import dao.BookReturnCallback;
import dao.DatabaseHandler;
import dao.LibraryAssistantUtil;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
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
import model.Borrow;

/*
 * @author afsal villan
 */
public class IssuedListController implements Initializable {

    @FXML
    private TableView<Borrow> tableView;

    @FXML
    private TableColumn<Borrow, String> isbnCol;

    @FXML
    private TableColumn<Borrow, String> ssnCol;

    @FXML
    private TableColumn<Borrow, String> staffIDCol;

    @FXML
    private TableColumn<Borrow, String> borrowDateCol;

    @FXML
    private TableColumn<Borrow, String> returnDateCol;

    private ObservableList<Borrow> list = FXCollections.observableArrayList();
    
    private BookReturnCallback callback;

    @FXML
    private JFXTextField searchBar;
     @FXML
    void searchIssue(ActionEvent event) {
        ObservableList<Borrow> newlist = FXCollections.observableArrayList();
        loadData();
        for (Borrow borrow : list) {
            if(borrow.getIsbn().toLowerCase().contains(searchBar.getText()))
                newlist.add(borrow);
        }
        tableView.setItems(newlist);
    }
    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane contentPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    private void initCol() {
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        ssnCol.setCellValueFactory(new PropertyValueFactory<>("ssn"));
        staffIDCol.setCellValueFactory(new PropertyValueFactory<>("staffID"));
        borrowDateCol.setCellValueFactory(new PropertyValueFactory<>("borrowed_date"));
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returned_date"));
        tableView.setItems(list);
    }

    public void setBookReturnCallback(BookReturnCallback callback) {
        this.callback = callback;
    }

    private void loadData() {
        list.clear();
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT isbn, ssn, staff_id, borrow_date, return_date FROM borrow ";
        ResultSet rs = handler.execQuery(qu);
        Preferences pref = Preferences.getPreferences();
        try {
            int counter = 0;
            while (rs.next()) {
                counter += 1;
                String isbn = rs.getString("isbn");
                String ssn = rs.getString("ssn");
                String staffid = rs.getString("staff_id");
                String bordate = rs.getString("borrow_date");
                String returndate = rs.getString("return_date");
                //System.out.println("Issued on " + issueTime);
//                Integer days = Math.toIntExact(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - issueTime.getTime())) + 1;
//                Float fine = LibraryAssistantUtil.getFineAmount(days);
                //IssueInfo issueInfo = new IssueInfo(counter, bookID, bookTitle, memberName, LibraryAssistantUtil.formatDateTimeString(new Date(issueTime.getTime())), days, fine);
                Borrow borrow = new Borrow(isbn, ssn, staffid, bordate, returndate, 0.0);
                list.add(borrow);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private Stage getStage() {
        return (Stage) tableView.getScene().getWindow();
    }

    @FXML
    void closeStage(ActionEvent event) {
        getStage().close();
    }

    @FXML
    void exportAsPDF(ActionEvent event) {
        List<List> printData = new ArrayList<>();
        String[] headers = {"ISBN", "SSN", "      STAFF ID       ", "    BORROW DATE     ", "RETURN DATE"};
        printData.add(Arrays.asList(headers));
        for (Borrow info : list) {
            List<String> row = new ArrayList<>();
            row.add(info.getIsbn());
            row.add(info.getSsn());
            row.add(info.getStaffID());
            row.add(info.getBorrowed_date());
            row.add(info.getReturned_date());
            printData.add(row);
        }
        LibraryAssistantUtil.initPDFExprot(rootPane, contentPane, getStage(), printData);
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    void handleReturn(ActionEvent event) {
        Borrow issueInfo = tableView.getSelectionModel().getSelectedItem();
        if (issueInfo != null) {
            callback.loadBookReturn(issueInfo.getIsbn());
            ((Stage)tableView.getScene().getWindow()).close();
        }
    }
}
