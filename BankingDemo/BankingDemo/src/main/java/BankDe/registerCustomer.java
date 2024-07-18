package BankDe;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/registerCustomer")
public class registerCustomer extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String address = request.getParameter("address");
        String mobileNo = request.getParameter("mobileNo");
        String emailId = request.getParameter("emailId");
        String accountType = request.getParameter("accountType");
        double initialBalance = Double.parseDouble(request.getParameter("initialBalance"));
        String dateOfBirth = request.getParameter("dateOfBirth");
        String idProof = request.getParameter("idProof");

        String accountNo = generateAccountNumber();
        String password = generateTemporaryPassword();
        String username = emailId;  // Assuming the email ID is used as the username

        Connection conn = null;
        PreparedStatement statementCustomer = null;
        PreparedStatement statementUserCredentials = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDemo", "root", "7395897383");
            conn.setAutoCommit(false); // Start transaction

            String sqlCustomer = "INSERT INTO CustomerDetails (full_name, address, mobile_no, email_id, account_type, initial_balance, date_of_birth, id_proof, account_no, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statementCustomer = conn.prepareStatement(sqlCustomer, PreparedStatement.RETURN_GENERATED_KEYS);
            statementCustomer.setString(1, fullName);
            statementCustomer.setString(2, address);
            statementCustomer.setString(3, mobileNo);
            statementCustomer.setString(4, emailId);
            statementCustomer.setString(5, accountType);
            statementCustomer.setDouble(6, initialBalance);
            statementCustomer.setString(7, dateOfBirth);
            statementCustomer.setString(8, idProof);
            statementCustomer.setString(9, accountNo);
            statementCustomer.setString(10, password);

            int rowsInserted = statementCustomer.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = statementCustomer.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int customerId = generatedKeys.getInt(1);

                    String sqlUserCredentials = "INSERT INTO user_credential (username, password, customer_id) VALUES (?, ?, ?)";
                    statementUserCredentials = conn.prepareStatement(sqlUserCredentials);
                    statementUserCredentials.setString(1, username);
                    statementUserCredentials.setString(2, password);
                    statementUserCredentials.setInt(3, customerId);

                    int userCredentialsInserted = statementUserCredentials.executeUpdate();
                    if (userCredentialsInserted > 0) {
                        conn.commit(); // Commit transaction if both inserts are successful
                        response.sendRedirect("success.html?accountNo=" + accountNo + "&username=" + username + "&password=" + password);
                        return;
                    }
                }
            }

            conn.rollback(); // Rollback transaction if any insert fails
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction on exception
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } finally {
            try {
                if (statementCustomer != null) statementCustomer.close();
                if (statementUserCredentials != null) statementUserCredentials.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        response.getWriter().println("Error registering customer.");
    }

    private String generateAccountNumber() {
        SecureRandom random = new SecureRandom();
        long number = (long) (random.nextDouble() * 1_000_000_000_000L);
        return String.format("ACC%012d", number);
    }

    private String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }
}
