package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import dao.AlertMaker;
import dao.BookReturnCallback;
import dao.DataHelper;
import dao.DatabaseHandler;
import dao.LibraryAssistantUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.LibraryStaff;

public class MainController implements Initializable, BookReturnCallback {

    private static final String BOOK_NOT_AVAILABLE = "Not Available";
    private static final String NO_SUCH_BOOK_AVAILABLE = "No Such Book Available";
    private static final String NO_SUCH_MEMBER_AVAILABLE = "No Such Member Available";
    private static final String BOOK_AVAILABLE = "Available";

    private Boolean isReadyForSubmission = false;
    private DatabaseHandler databaseHandler;
    private PieChart bookChart;
    private PieChart memberChart;

    @FXML
    private HBox book_info;
    @FXML
    private HBox member_info;
//    @FXML
//    private TextField bookIDInput;
    @FXML
    private Text bookName;
    @FXML
    private Text bookAuthor;
    @FXML
    private Text bookStatus;
//    @FXML
//    private TextField memberIDInput;
    @FXML
    private Text memberName;
    @FXML
    private Text memberMobile;
    @FXML
    private JFXTextField bookID;
    @FXML
    private StackPane rootPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private Text memberNameHolder;
    @FXML
    private Text memberEmailHolder;
    @FXML
    private Text memberContactHolder;
    @FXML
    private Text bookNameHolder;
    @FXML
    private Text bookAuthorHolder;
    @FXML
    private Text bookPublisherHolder;
    @FXML
    private Text issueDateHolder;
    @FXML
    private Text numberDaysHolder;
    @FXML
    private Text fineInfoHolder;
    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private JFXButton renewButton;
    @FXML
    private JFXButton submissionButton;
    @FXML
    private HBox submissionDataContainer;
    @FXML
    private StackPane bookInfoContainer;
    @FXML
    private StackPane memberInfoContainer;
    @FXML
    private Tab bookIssueTab;
    @FXML
    private Tab renewTab;
    @FXML
    private JFXTabPane mainTabPane;
    @FXML
    private JFXButton btnIssue;

    @FXML
    private JFXComboBox<String> bookIDInput;

    @FXML
    private JFXComboBox<String> memberIDInput;

    private int numberOfCopies;

    private int copiesOut;

    private boolean isProfessor = false;

    private String memberSSn = null;

    public static LibraryStaff currentStaff;

    public LibraryStaff getCurrentStaff() {
        return currentStaff;
    }

    public void setCurrentStaff(LibraryStaff currentStaff) {
        this.currentStaff = currentStaff;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        memberIDInput.setItems(FXCollections.observableArrayList(DataHelper.getSSNList()));
        bookIDInput.setItems(FXCollections.observableArrayList(DataHelper.getISBNList()));

        initDrawer();
        initGraphs();
        initComponents();
    }

    private void refreshComboBoxes() {
        memberIDInput.setItems(FXCollections.observableArrayList(DataHelper.getSSNList()));
        bookIDInput.setItems(FXCollections.observableArrayList(DataHelper.getISBNList()));
    }

    @FXML
    private void loadBookInfo(ActionEvent event) {
        clearBookCache();
        enableDisableGraph(false);

        String id = bookIDInput.getSelectionModel().getSelectedItem();
        ResultSet rs = DataHelper.getBookInfoWithIssueData(id);
        Boolean flag = false;
        try {
            if (rs.next()) {
                String bName = rs.getString("Title");
                String bAuthor = rs.getString("Book_author");
                String bookType = rs.getString("Book_type");
                numberOfCopies = rs.getInt("Number_of_copies");
                copiesOut = rs.getInt("Number_of_copies_out");
                //Timestamp issuedOn = rs.getTimestamp("Borrow_date");

                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                if (bookType.equals("Can be Lent")) {
                    String status = (numberOfCopies != 0) ? BOOK_AVAILABLE : String.format("Last copy borrowed on %s", LibraryAssistantUtil.getDateString(new Date(DataHelper.lastCopyBorrowedDate(id).getTime())));
                    if (numberOfCopies == 0) {
                        bookStatus.getStyleClass().add("not-available");
                    } else {
                        bookStatus.getStyleClass().remove("not-available");
                    }
                    bookStatus.setText(status);

                    flag = true;
                } else {
                    bookName.setText(NO_SUCH_BOOK_AVAILABLE);
                }
            }

            if (!flag) {
                bookName.setText(NO_SUCH_BOOK_AVAILABLE);
            } else {
                memberIDInput.requestFocus();
            }

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    void clearBookCache() {
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
    }

    void clearMemberCache() {
        memberName.setText("");
        memberMobile.setText("");
    }

    @FXML
    private void loadMemberInfo(ActionEvent event) {
        clearMemberCache();
        enableDisableGraph(false);

        String id = memberIDInput.getSelectionModel().getSelectedItem();
        String qu = "SELECT * FROM MEMBERS WHERE ssn = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;
        try {
            while (rs.next()) {
                String mName = rs.getString("Fname");
                String mMobile = rs.getString("Phone_number");
                isProfessor = rs.getBoolean("Professor");
                memberName.setText(mName);
                memberMobile.setText(mMobile);

                flag = true;
            }

            if (!flag) {
                memberName.setText(NO_SUCH_MEMBER_AVAILABLE);
            } else {
                btnIssue.requestFocus();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadIssueOperation(ActionEvent event) {
        if (checkForIssueValidity()) {
            JFXButton btn = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Invalid Input", null);
            refreshComboBoxes();
            return;
        }
        if (!bookStatus.getText().equals(BOOK_AVAILABLE)) {
            JFXButton btn = new JFXButton("Okay!");
            JFXButton viewDetails = new JFXButton("View Details");
            viewDetails.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
                String bookToBeLoaded = bookIDInput.getSelectionModel().getSelectedItem();
                bookID.setText(bookToBeLoaded);
                bookID.fireEvent(new ActionEvent());
                mainTabPane.getSelectionModel().select(renewTab);
            });
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn, viewDetails), "No More Copies", "All the copies of this book have been borrowed. Cant process borrow request");
            refreshComboBoxes();
            clearEntries();
            return;
        }

        String memberID = memberIDInput.getSelectionModel().getSelectedItem();
        String bookID = bookIDInput.getSelectionModel().getSelectedItem();
        Date date = new Date();
        Timestamp currentDate = new Timestamp(date.getTime());
        Timestamp returnDate;
        if (isProfessor) {
            returnDate = new Timestamp(date.getYear(), date.getMonth(), date.getDate() + 94, date.getHours(), date.getMinutes(), date.getSeconds(), 0);
        } else {
            returnDate = new Timestamp(date.getYear(), date.getMonth(), date.getDate() + 31, date.getHours(), date.getMinutes(), date.getSeconds(), 0);
        }

        JFXButton yesButton = new JFXButton("YES");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            String str = "INSERT INTO BORROW(ISBN,SSN,Staff_ID,Fine,Borrow_date,Return_date) VALUES ("
                    + "'" + bookID + "',"
                    + "'" + memberID
                    + "'," + "'" + currentStaff.getStaffID()
                    + "'," + "'" + String.valueOf(0.0) + "',"
                    + "'" + LibraryAssistantUtil.getDateString(new Date(currentDate.getTime()))
                    + "'," + "'" + LibraryAssistantUtil.getDateString(new Date(returnDate.getTime())) + "'" + ")";
            String str2 = "UPDATE BOOK SET Number_of_copies = " + String.valueOf(numberOfCopies - 1) + " WHERE isbn = '" + bookID + "'";
            String str3 = "UPDATE BOOK SET Number_of_copies_out = " + String.valueOf(copiesOut + 1) + " WHERE isbn = '" + bookID + "'";
            System.out.println(str + " and " + str2);

            if (databaseHandler.execAction(str) && databaseHandler.execAction(str2) && databaseHandler.execAction(str3)) {
                JFXButton button = new JFXButton("Done!");
                button.setOnAction((actionEvent) -> {
                    bookIDInput.requestFocus();
                });
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Book borrow Complete", null);
                refreshGraphs();
                refreshComboBoxes();

            } else {
                JFXButton button = new JFXButton("Okay.I'll Check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Borrow Operation Failed", null);
            }
            clearIssueEntries();
            refreshComboBoxes();
        });
        JFXButton noButton = new JFXButton("NO");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            JFXButton button = new JFXButton("That's Okay");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Borrow Cancelled", null);
            clearIssueEntries();
            refreshComboBoxes();
        });
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Borrow",
                String.format("Are you sure want to borrow the book '%s' to '%s' ?", bookName.getText(), memberName.getText()));
        refreshComboBoxes();
    }

    @FXML
    private void loadBookInfo2(ActionEvent event) {
        clearEntries();
        ObservableList<String> issueData = FXCollections.observableArrayList();
        isReadyForSubmission = false;

        try {
            String id = bookID.getText();
            int maxDays;
            String myQuery = "SELECT BO.ISBN, BO.SSN, BO.Borrow_date,\n"
                    + "M.Fname, M.Lname, M.Phone_number, M.Campus_address, M.Max_days, M.SSN,M.Professor,\n"
                    + "B.title, B.Book_author, B.Subject_area,B.Number_of_copies,B.Number_of_copies_out\n"
                    + "FROM BORROW AS BO, MEMBERS AS M, BOOK AS B\n"
                    + "WHERE BO.SSN=M.SSN AND BO.ISBN=B.ISBN AND BO.ISBN='" + id + "'";
            ResultSet rs = databaseHandler.execQuery(myQuery);
            if (rs.next()) {
                memberNameHolder.setText(rs.getString("Fname") + rs.getString("Lname"));
                memberContactHolder.setText(rs.getString("Phone_number"));
                memberEmailHolder.setText(rs.getString("Campus_address"));

                bookNameHolder.setText(rs.getString("title"));
                bookAuthorHolder.setText(rs.getString("Book_author"));
                bookPublisherHolder.setText(rs.getString("Subject_area"));

                maxDays = rs.getInt("Max_days");
                numberOfCopies = rs.getInt("Number_of_copies");
                copiesOut = rs.getInt("Number_of_copies_out");
                isProfessor = rs.getBoolean("Professor");
                memberSSn = rs.getString("SSN");

                String borrowDate = rs.getString("Borrow_date");
                java.sql.Date date = java.sql.Date.valueOf(borrowDate);
                Date dateOfIssue = new Date(date.getTime());
                issueDateHolder.setText(borrowDate);
                Long timeElapsed = System.currentTimeMillis() - date.getTime();
                Long days = TimeUnit.DAYS.convert(timeElapsed, TimeUnit.MILLISECONDS) + 1;
                String daysElapsed = String.format("Used %d days", days);
                numberDaysHolder.setText(daysElapsed);
                Float fine = LibraryAssistantUtil.getFineAmount(days.intValue(), maxDays);
                if (fine > 0) {
                    fineInfoHolder.setText(String.format("Fine : %.2f", LibraryAssistantUtil.getFineAmount(days.intValue(), maxDays)));
                } else {
                    fineInfoHolder.setText("");
                }

                isReadyForSubmission = true;
                disableEnableControls(true);
                submissionDataContainer.setOpacity(1);
            } else {
                JFXButton button = new JFXButton("Okay.I'll Check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "No such Book Exists in Borrow Database", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void loadSubmissionOp(ActionEvent event) {
        if (!isReadyForSubmission) {
            JFXButton btn = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Please select a book to submit", "Cant simply submit a null book :-)");
            return;
        }

        JFXButton yesButton = new JFXButton("YES, Please");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev) -> {
            String id = bookID.getText();
            String ac1 = "DELETE FROM BORROW WHERE ISBN = '" + id + "'" + "AND SSN = '" + memberSSn + "'";
            String ac3 = "UPDATE BOOK SET Number_of_copies = " + String.valueOf(numberOfCopies + 1) + " WHERE isbn = '" + id + "'";
            String ac2 = "UPDATE BOOK SET Number_of_copies_out = " + String.valueOf(copiesOut - 1) + " WHERE isbn = '" + id + "'";

            if (databaseHandler.execAction(ac1) && databaseHandler.execAction(ac3) && databaseHandler.execAction(ac2)) {
                JFXButton btn = new JFXButton("Done!");
                btn.setOnAction((actionEvent) -> {
                    bookID.requestFocus();
                });
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Book has been submitted", null);
                disableEnableControls(false);
                submissionDataContainer.setOpacity(0);
            } else {
                JFXButton btn = new JFXButton("Okay.I'll Check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Submission Has Been Failed", null);
            }
        });
        JFXButton noButton = new JFXButton("No, Cancel");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev) -> {
            JFXButton btn = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Submission Operation cancelled", null);
        });

        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Submission Operation", "Are you sure want to return the book ?");
    }

    @FXML
    private void loadRenewOp(ActionEvent event) {
        if (!isReadyForSubmission) {
            JFXButton btn = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Please select a book to renew", null);
            return;
        }
        Date date = new Date();
        Timestamp currentDate = new Timestamp(date.getTime());
        Timestamp returnDate;
        if (isProfessor) {
            returnDate = new Timestamp(date.getYear(), date.getMonth(), date.getDate() + 94, date.getHours(), date.getMinutes(), date.getSeconds(), 0);
        } else {
            returnDate = new Timestamp(date.getYear(), date.getMonth(), date.getDate() + 31, date.getHours(), date.getMinutes(), date.getSeconds(), 0);
        }

        JFXButton yesButton = new JFXButton("YES, Please");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            String ac = "UPDATE BORROW SET Borrow_date = " + "'" + LibraryAssistantUtil.getDateString(new Date(currentDate.getTime()))
                    + "'" + " WHERE ISBN = '" + bookID.getText() + "'" + "AND SSN = '" + memberSSn + "'";
            String ac2 = "UPDATE BORROW SET Return_date = " + "'" + LibraryAssistantUtil.getDateString(new Date(returnDate.getTime()))
                    + "'" + " WHERE ISBN = '" + bookID.getText() + "'" + "AND SSN = '" + memberSSn + "'";
            System.out.println(ac);
            System.out.println(ac2);
            if (databaseHandler.execAction(ac) && databaseHandler.execAction(ac2)) {
                JFXButton btn = new JFXButton("Alright!");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Book Has Been Renewed", null);
                disableEnableControls(false);
                submissionDataContainer.setOpacity(0);
            } else {
                JFXButton btn = new JFXButton("Okay!");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Renew Has Been Failed", null);
            }
        });
        JFXButton noButton = new JFXButton("No, Don't!");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            JFXButton btn = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Renew Operation cancelled", null);
        });
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Renew Operation", "Are you sure want to renew the book ?");
    }

    private Stage getStage() {
        return (Stage) rootPane.getScene().getWindow();
    }

    @FXML
    private void handleMenuClose(ActionEvent event) {
        getStage().close();
    }

    @FXML
    private void handleMenuAddBook(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/add_book.fxml"), "Add New Book", null);
        refreshComboBoxes();
    }

    @FXML
    private void handleMenuAddMember(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/member_add.fxml"), "Add New Member", null);
        refreshComboBoxes();
    }

    @FXML
    private void handleMenuAddLibraryStaff(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/add_LibraryStaff.fxml"), "Add New Library Staff", null);
        refreshComboBoxes();
    }

    @FXML
    private void handleMenuRenewMemberCard(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/renew_member_card.fxml"), "Renew A Card", null);
        refreshComboBoxes();
    }

    @FXML
    private void handleMenuViewBookList(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/book_list.fxml"), "Book List", null);
        refreshComboBoxes();
    }

    @FXML
    private void handleAboutMenu(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/about.fxml"), "About Us", null);
        refreshComboBoxes();
    }

    @FXML
    private void handleMenuSettings(ActionEvent event) {
        try {
            System.out.println("Current Staff Password: " + currentStaff.getPassword() + "\n");
            System.out.println("Current Staff Username: " + currentStaff.getUsername() + "\n");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/settings.fxml"));
            System.out.println("Current Staff Password: " + currentStaff.getPassword() + "\n");
            System.out.println("Current Staff Username: " + currentStaff.getUsername() + "\n");
            Parent parent = loader.load();
            System.out.println("Current Staff Password: " + currentStaff.getPassword() + "\n");
            System.out.println("Current Staff Username: " + currentStaff.getUsername() + "\n");
            SettingsController controller = loader.getController();
            System.out.println("Current Staff Password: " + currentStaff.getPassword() + "\n");
            System.out.println("Current Staff Username: " + currentStaff.getUsername() + "\n");
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Settings");
            stage.setScene(new Scene(parent));
            LibraryAssistantUtil.setStageIcon(stage);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //LibraryAssistantUtil.loadWindowSettings(getClass().getResource("/view/settings.fxml"), "Settings", null,currentStaff);
    }

    @FXML
    private void handleMenuViewMemberList(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/member_list.fxml"), "Member List", null);
        refreshComboBoxes();
    }

    @FXML
    private void handleMenuViewLibraryStaffList(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/librarystaff_list.fxml"), "Library Staff List", null);
        refreshComboBoxes();
    }

    @FXML
    private void handleIssuedList(ActionEvent event) {
        Object controller = LibraryAssistantUtil.loadWindow(getClass().getResource("/view/issued_list.fxml"), "Issued Book List", null);
        if (controller != null) {
            IssuedListController cont = (IssuedListController) controller;
            cont.setBookReturnCallback(this);
        }
        refreshComboBoxes();
    }

    @FXML
    void handleMenuRenewedCardList(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/renewal_card_list.fxml"), "Renewed Card List", null);
        refreshComboBoxes();
    }

    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = getStage();
        stage.setFullScreen(!stage.isFullScreen());
        refreshComboBoxes();
    }

    private void initDrawer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/toolbar.fxml"));
            VBox toolbar = loader.load();
            drawer.setSidePane(toolbar);
            ToolbarController controller = loader.getController();
            controller.setBookReturnCallback(this);
            controller.setCurrentStaff(currentStaff);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            drawer.toggle();
        });
        drawer.setOnDrawerOpening((event) -> {
            task.setRate(task.getRate() * -1);
            task.play();
            drawer.toFront();
        });
        drawer.setOnDrawerClosed((event) -> {
            drawer.toBack();
            task.setRate(task.getRate() * -1);
            task.play();
        });
    }

    private void clearEntries() {
        memberNameHolder.setText("");
        memberEmailHolder.setText("");
        memberContactHolder.setText("");

        bookNameHolder.setText("");
        bookAuthorHolder.setText("");
        bookPublisherHolder.setText("");

        issueDateHolder.setText("");
        numberDaysHolder.setText("");
        fineInfoHolder.setText("");

        disableEnableControls(false);
        submissionDataContainer.setOpacity(0);
    }

    private void disableEnableControls(Boolean enableFlag) {
        if (enableFlag) {
            renewButton.setDisable(false);
            submissionButton.setDisable(false);
        } else {
            renewButton.setDisable(true);
            submissionButton.setDisable(true);
        }
    }

    private void clearIssueEntries() {
        bookIDInput.getItems().clear();
        memberIDInput.getItems().clear();
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
        memberMobile.setText("");
        memberName.setText("");
        enableDisableGraph(true);
    }

    private void initGraphs() {
        bookChart = new PieChart(databaseHandler.getBookGraphStatistics());
        memberChart = new PieChart(databaseHandler.getMemberGraphStatistics());
        bookInfoContainer.getChildren().add(bookChart);
        memberInfoContainer.getChildren().add(memberChart);

        bookIssueTab.setOnSelectionChanged((Event event) -> {
            clearIssueEntries();
            if (bookIssueTab.isSelected()) {
                refreshGraphs();
                refreshComboBoxes();
            }
        });
    }

    private void refreshGraphs() {
        bookChart.setData(databaseHandler.getBookGraphStatistics());
        memberChart.setData(databaseHandler.getMemberGraphStatistics());
    }

    private void enableDisableGraph(Boolean status) {
        if (status) {
            bookChart.setOpacity(1);
            memberChart.setOpacity(1);
        } else {
            bookChart.setOpacity(0);
            memberChart.setOpacity(0);
        }
    }

    private boolean checkForIssueValidity() {
        bookIDInput.fireEvent(new ActionEvent());
        memberIDInput.fireEvent(new ActionEvent());
        return bookIDInput.getSelectionModel().getSelectedItem() == null || memberIDInput.getSelectionModel().getSelectedItem() == null
                || memberName.getText().isEmpty() || bookName.getText().isEmpty()
                || bookName.getText().equals(NO_SUCH_BOOK_AVAILABLE) || memberName.getText().equals(NO_SUCH_MEMBER_AVAILABLE);
    }

    @Override
    public void loadBookReturn(String bookID) {
        this.bookID.setText(bookID);
        mainTabPane.getSelectionModel().select(renewTab);
        loadBookInfo2(null);
        getStage().toFront();
        if (drawer.isOpened()) {
            drawer.close();
        }
    }

    @FXML
    private void handleIssueButtonKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loadIssueOperation(null);
        }
    }

    private void initComponents() {
        mainTabPane.tabMinWidthProperty().bind(rootAnchorPane.widthProperty().divide(mainTabPane.getTabs().size()).subtract(15));
    }

    @FXML
    private void handleMenuOverdueNotification(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/view/overdue_notification.fxml"), "Notify Users", null);
        refreshComboBoxes();
    }

}
