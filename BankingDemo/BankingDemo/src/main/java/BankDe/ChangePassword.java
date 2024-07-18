package BankDe;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ChangePassword extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int customerId = (int) session.getAttribute("customerId");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            response.getWriter().println("New passwords do not match.");
            return;
        }

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish Connection to MySQL database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDemo", "root", "7395897383");

            // Check if current password matches
            String checkPasswordQuery = "SELECT * FROM user_credential WHERE customer_id = ? AND password = ?";
            PreparedStatement checkPasswordStmt = conn.prepareStatement(checkPasswordQuery);
            checkPasswordStmt.setInt(1, customerId);
            checkPasswordStmt.setString(2, currentPassword);
            ResultSet resultSet = checkPasswordStmt.executeQuery();

            if (!resultSet.next()) {
                response.getWriter().println("Current password is incorrect.");
                return;
            }

            // Update password
            String updatePasswordQuery = "UPDATE user_credential SET password = ? WHERE customer_id = ?";
            PreparedStatement updatePasswordStmt = conn.prepareStatement(updatePasswordQuery);
            updatePasswordStmt.setString(1, newPassword);
            updatePasswordStmt.setInt(2, customerId);

            int rowsUpdated = updatePasswordStmt.executeUpdate();
            if (rowsUpdated > 0) {
                response.sendRedirect("pass_change_success.html");
            } else {
                response.getWriter().println("Failed to update password.");
            }

            // Clean up resources
            resultSet.close();
            updatePasswordStmt.close();
            checkPasswordStmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Exception: " + e.getMessage());
        }
    }
}
