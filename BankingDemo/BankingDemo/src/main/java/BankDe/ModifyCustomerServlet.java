package BankDe;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/modify_customer_servlet")
public class ModifyCustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customerId");

        if (customerId == null) {
            response.getWriter().println("You must be logged in to modify customer details.");
            return;
        }

        // Get parameters from the request
        String fullName = request.getParameter("fullName");
        String address = request.getParameter("address");
        String mobileNo = request.getParameter("mobileNo");
        String emailId = request.getParameter("emailId");
        String accountType = request.getParameter("accountType");
        double initialBalance = Double.parseDouble(request.getParameter("initialBalance"));
        String dateOfBirth = request.getParameter("dateOfBirth");
        String idProof = request.getParameter("idProof");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDemo", "root", "7395897383");

            // Prepare update query
            String updateCustomerQuery = "UPDATE CustomerDetails SET full_name = ?, address = ?, mobile_no = ?, email_id = ?, account_type = ?, initial_balance = ?, date_of_birth = ?, id_proof = ? WHERE id = ?";
            PreparedStatement updateCustomerStmt = conn.prepareStatement(updateCustomerQuery);
            updateCustomerStmt.setString(1, fullName);
            updateCustomerStmt.setString(2, address);
            updateCustomerStmt.setString(3, mobileNo);
            updateCustomerStmt.setString(4, emailId);
            updateCustomerStmt.setString(5, accountType);
            updateCustomerStmt.setDouble(6, initialBalance);
            updateCustomerStmt.setString(7, dateOfBirth);
            updateCustomerStmt.setString(8, idProof);
            updateCustomerStmt.setInt(9, customerId);

            // Execute update
            int rowsUpdated = updateCustomerStmt.executeUpdate();

            if (rowsUpdated > 0) {
                response.sendRedirect("modify_success.html"); // Redirect to success page
            } else {
                response.getWriter().println("Failed to modify customer details.");
            }

            updateCustomerStmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Exception: " + e.getMessage());
        }
    }
}
