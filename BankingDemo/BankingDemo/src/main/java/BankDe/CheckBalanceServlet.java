package BankDe;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/CheckBalanceServlet")
public class CheckBalanceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customerId");

        if (customerId == null) {
            response.sendRedirect("login.html"); // Redirect to login if not logged in
            return;
        }

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish Connection to MySQL database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDemo", "root", "7395897383");

            // Retrieve current balance
            double currentBalance = getCurrentBalance(conn, customerId);

            // Set current balance as request attribute
            request.setAttribute("currentBalance", currentBalance);

            // Forward to check_balance.html to display balance
            request.getRequestDispatcher("check_balance.jsp").forward(request, response);

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Exception: " + e.getMessage());
        }
    }

    // Helper method to retrieve current balance
    private double getCurrentBalance(Connection conn, int customerId) throws Exception {
        String query = "SELECT initial_balance FROM CustomerDetails WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble("initial_balance");
        }
        return 0.0; // Return default balance if not found
    }
}
