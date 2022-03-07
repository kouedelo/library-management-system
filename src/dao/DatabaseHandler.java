package dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javax.swing.JOptionPane;
import model.Book;
import model.LibraryStaff;
import model.Member;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DatabaseHandler {

    private final static Logger LOGGER = LogManager.getLogger(DatabaseHandler.class.getName());

    private static DatabaseHandler handler = null;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC&useSSL=false";
    private static Connection conn = null;
    private static Statement stmt = null;

    private static List<LibraryStaff> stafflist = new ArrayList<>();

    static {
        createConnection();
        //inflateDB();
    }

    private DatabaseHandler() {
    }

    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

//    private static void inflateDB() {
//        List<String> tableData = new ArrayList<>();
//        try {
//            Set<String> loadedTables = getDBTables();
//            System.out.println("Already loaded tables " + loadedTables);
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            Document doc = dBuilder.parse(DatabaseHandler.class.getClass().getResourceAsStream("/resources/database/tables.xml"));
//            NodeList nList = doc.getElementsByTagName("table-entry");
//            for (int i = 0; i < nList.getLength(); i++) {
//                Node nNode = nList.item(i);
//                Element entry = (Element) nNode;
//                String tableName = entry.getAttribute("name");
//                String query = entry.getAttribute("col-data");
//                if (!loadedTables.contains(tableName.toLowerCase())) {
//                    tableData.add(String.format("CREATE TABLE %s (%s)", tableName, query));
//                }
//            }
//            if (tableData.isEmpty()) {
//                System.out.println("Tables are already loaded");
//            } else {
//                System.out.println("Inflating new tables.");
//                createTables(tableData);
//            }
//        } catch (Exception ex) {
//            LOGGER.log(Level.ERROR, "{}", ex);
//        }
//    }
    private static void createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found !!");
            return;
        }
        System.out.println("MySQL JDBC Driver Registered!");
        conn = null;
        stmt = null;
        try {
            conn = DriverManager.getConnection(DB_URL, "root", "sep@2398");
            stmt = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");

            return;
        }
        System.out.println("Connection to database established!");
    }

//    private static Set<String> getDBTables() throws SQLException {
//        Set<String> set = new HashSet<>();
//        DatabaseMetaData dbmeta = conn.getMetaData();
//        readDBTable(set, dbmeta, "TABLE", null);
//        return set;
//    }
    private static void readDBTable(Set<String> set, DatabaseMetaData dbmeta, String searchCriteria, String schema) throws SQLException {
        ResultSet rs = dbmeta.getTables(null, schema, null, new String[]{searchCriteria});
        while (rs.next()) {
            set.add(rs.getString("TABLE_NAME").toLowerCase());
        }
    }

    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            //conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC&useSSL=false", "root", "sep@2398");
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return null;
        } finally {
        }
        return result;
    }

    public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return false;
        } finally {
        }
    }

    public boolean deleteBook(Book book) {
        try {
            String deleteStatement = "DELETE FROM BOOK WHERE ISBN = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, book.getIsbn());
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public boolean isBookAlreadyIssued(Book book) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM BORROW WHERE ISBN=?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            stmt.setString(1, book.getIsbn());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public boolean deleteMember(Member member) {
        try {
            String deleteStatement = "DELETE FROM MEMBERS WHERE ssn = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, member.getSsn());
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public boolean deleteLibraryStaff(LibraryStaff libraryStaff) {
        try {
            String deleteStatement = "DELETE FROM library_staff WHERE Staff_id = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, libraryStaff.getStaffID());
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public boolean isMemberHasAnyBooks(Member member) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM BORROW WHERE Ssn=?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            stmt.setString(1, member.getSsn());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public boolean updateBook(Book book) {
        try {
            String update = "UPDATE BOOK SET Subject_area=?, Title=?, Book_author=?,Volume=?,Description=?,Number_of_copies=?,Book_type=?,Reason=? WHERE ISBN=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, book.getSubjectArea());
            stmt.setString(3, book.getAuthor());
            stmt.setString(2, book.getTitle());
            stmt.setString(4, String.valueOf(book.getVolume()));
            stmt.setString(5, book.getDescription());
            stmt.setInt(6, book.getNoOfCopies());
            stmt.setString(7, book.getBookType());
            stmt.setString(8, book.getReason());
            stmt.setString(9, book.getIsbn());
            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public boolean updateMember(Member member) {
        try {
            String update = "UPDATE MEMBERS SET Lname=?,Fname=?,Birth_date=?,Phone_number=?,Campus_address=?, Home_address=?,Professor=?,department=?, max_days = ? WHERE SSN=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, member.getLname());
            stmt.setString(2, member.getFname());
            stmt.setDate(3, member.getBirthDate());
            stmt.setString(4, member.getPhoneNumber());
            stmt.setString(5, member.getCampusMailAddress());
            stmt.setString(6, member.getHomeMailAddress());
            stmt.setBoolean(7, member.getIsAProfessor());
            stmt.setString(8, member.getDepartment());
            stmt.setInt(9, member.getMaxDays());
            stmt.setString(10, member.getSsn());
            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public boolean updateLibraryStaff(LibraryStaff libraryStaff) {
        try {
            String update = "UPDATE Library_staff SET Fname=?, Lname=?, Staff_type=?,Birth_date=?,Username=?,Password=? WHERE Staff_id=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, libraryStaff.getFName());
            stmt.setString(2, libraryStaff.getLNname());
            stmt.setString(3, libraryStaff.getStaffType());
            stmt.setDate(4, libraryStaff.getBirthDate());
            stmt.setString(5, libraryStaff.getUsername());
            stmt.setString(6, libraryStaff.getPassword());
            stmt.setString(7, libraryStaff.getStaffID());
            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public void getPreferences() {

        stafflist.clear();
        String qu = "SELECT * FROM library_staff";
        ResultSet rs = execQuery(qu);
        try {
            while (rs.next()) {
                String fnamex = rs.getString("Fname");
                String lnamex = rs.getString("Lname");
                Date bdate = rs.getDate("Birth_date");
                String stafID = rs.getString("Staff_id");
                String stafType = rs.getString("Staff_type");
                String usrname = rs.getString("Username");
                String pasWord = rs.getString("Password");
                stafflist.add(new LibraryStaff(fnamex, lnamex, bdate, stafID, stafType, usrname, pasWord));

            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DatabaseHandler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public LibraryStaff returnUser(String userName, String password) {
        LibraryStaff lStaff = null;
        for (LibraryStaff libraryStaff : stafflist) {
            if (userName.equals(libraryStaff.getUsername()) && password.equals(libraryStaff.getPassword())) {
                lStaff = libraryStaff;
            }
        }
        return lStaff;
    }

    public static void main(String[] args) throws Exception {
        DatabaseHandler.getInstance();
    }

    public ObservableList<PieChart.Data> getBookGraphStatistics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            String qu1 = "SELECT COUNT(*) FROM BOOK";
            String qu2 = "SELECT COUNT(*) FROM BORROW";
            ResultSet rs = execQuery(qu1);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Total Books (" + count + ")", count));
            }
            rs = execQuery(qu2);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Issued Books (" + count + ")", count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public ObservableList<PieChart.Data> getMemberGraphStatistics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            String qu1 = "SELECT COUNT(*) FROM MEMBERS";
            String qu2 = "SELECT COUNT(DISTINCT ssn) FROM BORROW";
            ResultSet rs = execQuery(qu1);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Total Members (" + count + ")", count));
            }
            rs = execQuery(qu2);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Active (" + count + ")", count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private static void createTables(List<String> tableData) throws SQLException {
        Statement statement = conn.createStatement();
        statement.closeOnCompletion();
        for (String command : tableData) {
            System.out.println(command);
            statement.addBatch(command);
        }
        statement.executeBatch();
    }

    public Connection getConnection() {
        return conn;
    }

}
