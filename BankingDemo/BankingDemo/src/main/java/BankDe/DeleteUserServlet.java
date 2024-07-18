package BankDe;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DeleteUserServlet")
public class DeleteUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String emailId = request.getParameter("email");

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish Connection to MySQL database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDemo", "root", "7395897383");

            // Delete user by email_id
            String deleteUserQuery = "DELETE FROM CustomerDetails WHERE email_id = ?";
            PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery);
            deleteUserStmt.setString(1, emailId);

            int rowsDeleted = deleteUserStmt.executeUpdate();
            if (rowsDeleted > 0) {
                response.sendRedirect("delete_success.html");
            } else {
                response.getWriter().println("Failed to delete user. User not found.");
            }

            deleteUserStmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Exception: " + e.getMessage());
        }
    }
}
