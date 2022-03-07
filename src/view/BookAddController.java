package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import dao.AlertMaker;
import dao.DataHelper;
import dao.DatabaseHandler;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
import model.Book;
import model.EBookType;

public class BookAddController implements Initializable {

    @FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane mainContainer;

    @FXML
    private JFXTextField title;

    @FXML
    private JFXTextField isbn;

    @FXML
    private JFXTextField author;

    @FXML
    private JFXTextField subjectArea;

    @FXML
    private JFXTextField volume;

    @FXML
    private JFXTextField description;

    @FXML
    private JFXTextField noOfCopies;

    @FXML
    private JFXComboBox<String> bookType;

    @FXML
    private JFXTextField reason;

    @FXML
    private JFXComboBox<Boolean> isAvail;

    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXButton cancelButton;

    private DatabaseHandler databaseHandler;

    private Boolean isInEditMode = Boolean.FALSE;

    private List<String> listBookType = new ArrayList<>();
    private List<Boolean> availList = new ArrayList<>();

    @FXML
    void addBook(ActionEvent event) {
        String bookIsbn = isbn.getText();
        String bookArea = subjectArea.getText();
        String bookAuthor = author.getText();
        String bookTitle = title.getText();
        String bookDescription = description.getText();
        String bookReason = reason.getText();

        if (bookIsbn.isEmpty() || bookAuthor.isEmpty() || bookTitle.isEmpty() || volume.getText().isEmpty() || bookArea.isEmpty() || bookDescription.isEmpty()
                || noOfCopies.getText().isEmpty() || bookType.getSelectionModel().getSelectedItem() == null
                || (bookReason.isEmpty() && !bookType.getSelectionModel().getSelectedItem().equals("Can be Lent"))) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Insufficient Data", "Please enter data in all fields.");
            return;
        }
        int bookVolume = Integer.parseInt(volume.getText());
        int bookCopies = Integer.valueOf(noOfCopies.getText());
        String booktype = bookType.getSelectionModel().getSelectedItem();

        if (isInEditMode) {
            handleEditOperation();
            clearEntries();
            refreshComboBoxes();
            return;
        }

        if (DataHelper.isBookExists(bookIsbn)) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Duplicate book id", "Book with same Book ID exists.\nPlease use new ID");
            refreshComboBoxes();
            return;
        }

        Book book = new Book(bookIsbn, bookArea, bookTitle, bookAuthor, bookVolume, bookDescription, bookCopies, booktype, bookReason, 0);
        boolean result = DataHelper.insertNewBook(book);
        if (result) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "New book added", bookTitle + " has been added");
            clearEntries();
            refreshComboBoxes();
        } else {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to add new book", "Check all the entries and try again");
            refreshComboBoxes();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        listBookType.add("Can be Lent");
        listBookType.add("Cannot be Lent");
        listBookType.add("Want to acquire");
        bookType.setItems(FXCollections.observableArrayList(listBookType));

    }

    private void refreshComboBoxes() {
        bookType.setItems(FXCollections.observableArrayList(listBookType));

    }

    private ArrayList<EBookType> getBType() {
        ArrayList<EBookType> list = new ArrayList<>();
        list.add(EBookType.can_be_lent);
        list.add(EBookType.cannot_be_lent);
        list.add(EBookType.want_to_aquire);
        return list;
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void checkData() {
        String qu = "SELECT title FROM BOOK";
        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while (rs.next()) {
                String titlex = rs.getString("title");
                System.out.println(titlex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BookAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void inflateUI(Book book) {
        title.setText(book.getTitle());
        isbn.setText(book.getIsbn());
        author.setText(book.getAuthor());
        subjectArea.setText(book.getSubjectArea());
        volume.setText(String.valueOf(book.getVolume()));
        description.setText(book.getDescription());
        noOfCopies.setText(String.valueOf(book.getNoOfCopies()));
        reason.setText(book.getReason());
        bookType.getSelectionModel().select(book.getBookType());
        isbn.setEditable(false);
        //bookType.setValue(EBookType.can_be_lent);
        isInEditMode = Boolean.TRUE;
    }

    private void clearEntries() {
        title.clear();
        isbn.clear();
        author.clear();
        subjectArea.clear();
        volume.clear();
        description.clear();
        noOfCopies.clear();
        bookType.getItems().clear();
        reason.clear();
    }

    private void handleEditOperation() {
        Book book = new Book(isbn.getText(), subjectArea.getText(), title.getText(), author.getText(), Integer.parseInt(volume.getText()), 
                description.getText(), Integer.parseInt(noOfCopies.getText()), bookType.getSelectionModel().getSelectedItem(), 
                reason.getText(), DataHelper.getNumberOfCopiesOut(Integer.parseInt(isbn.getText())));
        if (databaseHandler.updateBook(book)) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Success", "Update complete");
            refreshComboBoxes();
        } else {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed", "Could not update data");
            refreshComboBoxes();
        }
    }
}
