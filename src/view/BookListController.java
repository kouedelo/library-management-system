package view;

import com.jfoenix.controls.JFXTextField;
import dao.AlertMaker;
import dao.DatabaseHandler;
import dao.LibraryAssistantUtil;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Book;

public class BookListController implements Initializable {

    ObservableList<Book> list = FXCollections.observableArrayList();

    @FXML
    private StackPane rootPane;
    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> idCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> descriptionCol;
    @FXML
    private TableColumn<Book, Boolean> availabilityCol;

    @FXML
    private TableColumn<Book, Integer> numOfCopiesCol;

    @FXML
    private TableColumn<Book, Integer> volumeCol;

    @FXML
    private TableColumn<Book, String> subAreaCol;

    @FXML
    private TableColumn<Book, String> bookTypeCol;

    @FXML
    private TableColumn<Book, String> reasonCol;

    @FXML
    private TableColumn<Book, Integer> copiesOutCol;
    
    @FXML
    private AnchorPane contentPane;
    @FXML
    private JFXTextField searchBar;

    @FXML
    void searchBook(ActionEvent event) {
        ObservableList<Book> newlist = FXCollections.observableArrayList();
        loadData();
        for (Book book : list) {
            if(book.getTitle().toLowerCase().contains(searchBar.getText()))
                newlist.add(book);
        }
        tableView.setItems(newlist);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    private Stage getStage() {
        return (Stage) tableView.getScene().getWindow();
    }

    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        volumeCol.setCellValueFactory(new PropertyValueFactory<>("volume"));
        subAreaCol.setCellValueFactory(new PropertyValueFactory<>("subjectArea"));
        bookTypeCol.setCellValueFactory(new PropertyValueFactory<>("bookType"));
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        numOfCopiesCol.setCellValueFactory(new PropertyValueFactory<>("noOfCopies"));
        copiesOutCol.setCellValueFactory(new PropertyValueFactory<>("noOfCopiesOut"));
    }

    private void loadData() {
        list.clear();

        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM BOOK";//if(handler == null){return;}
        try {
            System.out.println("SQL Connection to database established!");
            ResultSet rs = handler.execQuery(qu);
            while (rs.next()) {

                String isbn = rs.getString("ISBN");
                String subjArea = rs.getString("Subject_area");
                String author = rs.getString("Book_author");
                String titlex = rs.getString("Title");
                int volum = Integer.parseInt(rs.getString("Volume"));
                String desc = rs.getString("Description");
                int ncopies = rs.getInt("Number_of_copies");
                String btype = rs.getString("Book_type");
                String reasonx = rs.getString("Reason");
                int copiesOut = rs.getInt("Number_of_copies_out");

                list.add(new Book(isbn, subjArea, titlex, author, volum, desc, ncopies, btype, reasonx, copiesOut));

            }
        } catch (SQLException ex) {
            Logger.getLogger(BookAddController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableView.setItems(list);
    }

    @FXML
    private void handleBookDeleteOption(ActionEvent event) {
        //Fetch the selected row
        Book selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if (selectedForDeletion == null) {
            AlertMaker.showErrorMessage("No book selected", "Please select a book for deletion.");
            return;
        }
        if (DatabaseHandler.getInstance().isBookAlreadyIssued(selectedForDeletion)) {
            AlertMaker.showErrorMessage("Cant be deleted", "This book is already issued and cant be deleted.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting book");
        alert.setContentText("Are you sure want to delete the book " + selectedForDeletion.getTitle() + " ?");
        Optional<ButtonType> answer = AlertMaker.showConfirmationMessage("Deleting book", "Are you sure want to delete " + selectedForDeletion.getTitle() + " ?").showAndWait();
        if (answer.get() == ButtonType.OK) {
            Boolean result = DatabaseHandler.getInstance().deleteBook(selectedForDeletion);
            if (result) {
                AlertMaker.showSimpleAlert("Book deleted", selectedForDeletion.getTitle() + " was deleted successfully.");
                list.remove(selectedForDeletion);
            } else {
                AlertMaker.showSimpleAlert("Failed", selectedForDeletion.getTitle() + " could not be deleted");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    private void handleBookEditOption(ActionEvent event) {
        //Fetch the selected row
        Book selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            AlertMaker.showErrorMessage("No book selected", "Please select a book for edit.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add_book.fxml"));
            Parent parent = loader.load();

            BookAddController controller = loader.getController();
            controller.inflateUI(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
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
    private void handleRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void exportAsPDF(ActionEvent event) {
        List<List> printData = new ArrayList<>();
        String[] headers = {"   Title   ", "ISBN", "  Author  ", "  Description ", "Avail", "Book Type", "Reason", "Number Of Copies", "Volume", "Number of Copies Out"};
        printData.add(Arrays.asList(headers));
        for (Book book : list) {
            List<String> row = new ArrayList<>();
            row.add(book.getTitle());
            row.add(book.getIsbn());
            row.add(book.getAuthor());
            row.add(book.getDescription());
            row.add(book.getBookType());
            row.add(book.getReason());
            row.add(String.valueOf(book.getNoOfCopies()));
            row.add(String.valueOf(book.getVolume()));
            row.add(String.valueOf(book.getNoOfCopiesOut()));

            printData.add(row);
        }
        LibraryAssistantUtil.initPDFExprot(rootPane, contentPane, getStage(), printData);
    }

    @FXML
    private void closeStage(ActionEvent event) {
        getStage().close();
    }

}
