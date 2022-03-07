package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Book;
import model.LibraryStaff;
import model.MailServerInfo;
import model.Member;
import model.RenewalCard;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author afsal
 */
public class DataHelper {

    private final static Logger LOGGER = LogManager.getLogger(DatabaseHandler.class.getName());

    private static ArrayList<String> ssnList = new ArrayList<>();

    private static ArrayList<String> isbnList = new ArrayList<>();
    
    private static ArrayList<String> staffID = new ArrayList<>();

    public static boolean insertNewBook(Book book) {
        try {
            PreparedStatement statement = DatabaseHandler.getInstance().getConnection().prepareStatement(
                    "INSERT INTO BOOK(ISBN, Subject_Area, Book_author, Title, Volume, Description, Number_of_copies, Book_type, Reason) VALUES(?,?,?,?,?,?,?,?,?)");
            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getSubjectArea());
            statement.setString(4, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setInt(5, book.getVolume());
            statement.setString(6, book.getDescription());
            statement.setInt(7, book.getNoOfCopies());
            statement.setString(8, book.getBookType());
            statement.setString(9, book.getReason());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public static boolean insertNewMember(Member member) {
        try {
            PreparedStatement statement = DatabaseHandler.getInstance().getConnection().prepareStatement(
                    "INSERT INTO MEMBERS(SSN,Phone_Number,Home_Address,Campus_Address,Lname,Fname,Birth_Date,Professor,department,Max_days) VALUES(?,?,?,?,?,?,?,?,?,?)");
            statement.setString(1, member.getSsn());
            statement.setString(2, member.getPhoneNumber());
            statement.setString(3, member.getHomeMailAddress());
            statement.setString(4, member.getCampusMailAddress());
            statement.setString(5, member.getLname());
            statement.setString(6, member.getFname());
            statement.setDate(7, member.getBirthDate());
            statement.setBoolean(8, member.getIsAProfessor());
            if (!member.getIsAProfessor()) {
                statement.setString(9, null);
            } else {
                statement.setString(9, member.getDepartment());
            }
            statement.setInt(10, member.getMaxDays());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public static boolean insertNewLibraryStaff(LibraryStaff libraryStaff) {
        try {
            PreparedStatement statement = DatabaseHandler.getInstance().getConnection().prepareStatement(
                    "INSERT INTO LIBRARY_STAFF (Staff_id,Lname,Fname,Birth_date,Staff_type,Username,Password) VALUES(?,?,?,?,?,?,?)");
            statement.setString(1, libraryStaff.getStaffID());
            statement.setString(2, libraryStaff.getLNname());
            statement.setString(3, libraryStaff.getFName());
            statement.setDate(4, libraryStaff.getBirthDate());
            statement.setString(5, libraryStaff.getStaffType());
            statement.setString(6, libraryStaff.getUsername());
            statement.setString(7, libraryStaff.getPassword());

            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public static boolean insertRenewalCard(RenewalCard renewCard) {
        try {
            PreparedStatement statement = DatabaseHandler.getInstance().getConnection().prepareStatement(
                    "INSERT INTO RENEWAL_CARD (SSN,STAFF_ID,CARD_NUMBER,DELIVERY_DATE,EXPIRY_DATE) VALUES(?,?,?,?,?)");
            statement.setString(1, renewCard.getSsn());
            statement.setString(2, renewCard.getStaffID());
            statement.setString(3, renewCard.getCardNumber());
            statement.setDate(4, renewCard.getRenewalDate());
            statement.setDate(5, renewCard.getExpiryDate());

            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public static boolean isBookExists(String isbn) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM BOOK WHERE ISBN=?";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, isbn);
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

    public static boolean isMemberExists(String ssn) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM MEMBERS WHERE ssn=?";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, ssn);
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

    public static boolean isLibraryStaffExists(String libStafID) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM library_staff WHERE Staff_id=?";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, libStafID);
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

    public static ResultSet getBookInfoWithIssueData(String isbn) {
        try {
            String query = "SELECT B.title, B.Book_author, B.Number_of_copies, B.Number_of_copies_out, B.Book_type FROM BOOK AS B where B.isbn = ?";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return null;
    }

    public static Timestamp lastCopyBorrowedDate(String isbn) {
        try {
            String query = "SELECT B.isbn, max(Borrow_date) FROM BORROW AS B where B.isbn = ? GROUP BY B.isbn";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bisbn = rs.getString("isbn");
                Timestamp bDate = rs.getTimestamp("max(Borrow_date)");
                return bDate;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return null;
    }

    public static ArrayList<String> getISBNList() {
        isbnList.clear();
        try {
            String query = "SELECT ISBN from book";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            //stmt.setString(1, query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                isbnList.add(isbn);
            }
            return isbnList;
        } catch (SQLException ex) {
            //LOGGER.log(Level.ERROR, "{}", ex);
        }
        return null;
    }

    public static ArrayList<String> getSSNList() {
        ssnList.clear();
        try {
            String query = "SELECT ssn from members";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            //stmt.setString(1, query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String ssn = rs.getString("SSN");
                ssnList.add(ssn);
            }
            return ssnList;
        } catch (SQLException ex) {
            //LOGGER.log(level.ERROR, "{}", ex);
        }
        return null;
    }
    
    public static ArrayList<String> getStaffIdList() {
        staffID.clear();
        try {
            String query = "SELECT Staff_id from library_staff";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            //stmt.setString(1, query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String ssn = rs.getString("Staff_id");
                staffID.add(ssn);
            }
            return staffID;
        } catch (SQLException ex) {
            //LOGGER.log(level.ERROR, "{}", ex);
        }
        return null;
    }

    public static void wipeTable(String tableName) {
        try {
            Statement statement = DatabaseHandler.getInstance().getConnection().createStatement();
            statement.execute("DELETE FROM " + tableName + " WHERE TRUE");
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
    }

    public static boolean updateMailServerInfo(MailServerInfo mailServerInfo) {
        try {
            wipeTable("MAIL_SERVER_INFO");
            PreparedStatement statement = DatabaseHandler.getInstance().getConnection().prepareStatement(
                    "INSERT INTO MAIL_SERVER_INFO(server_name,server_port,user_email,user_password,ssl_enabled) VALUES(?,?,?,?,?)");
            statement.setString(1, mailServerInfo.getMailServer());
            statement.setInt(2, mailServerInfo.getPort());
            statement.setString(3, mailServerInfo.getEmailID());
            statement.setString(4, mailServerInfo.getPassword());
            statement.setBoolean(5, mailServerInfo.getSslEnabled());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public static MailServerInfo loadMailServerInfo() {
        try {
            String checkstmt = "SELECT * FROM MAIL_SERVER_INFO";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String mailServer = rs.getString("server_name");
                Integer port = rs.getInt("server_port");
                String emailID = rs.getString("user_email");
                String userPassword = rs.getString("user_password");
                Boolean sslEnabled = rs.getBoolean("ssl_enabled");
                return new MailServerInfo(mailServer, port, emailID, userPassword, sslEnabled);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return null;
    }

    public static int getNumberOfCopiesOut(int isbn) {
        int copies = 0;
        try {
            String query = "SELECT COUNT(*) from BORROW WHERE ISBN = '" + isbn + "'";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            //stmt.setString(1, query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                copies = rs.getInt("COUNT(*)");
            }
            return copies;
        } catch (SQLException ex) {
            //LOGGER.log(level.ERROR, "{}", ex);
        }
        return 0;
    }
}
