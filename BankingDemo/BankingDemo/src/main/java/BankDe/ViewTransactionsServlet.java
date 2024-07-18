package BankDe;

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/ViewTransactionsServlet")
public class ViewTransactionsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customerId");

        if (customerId == null) {
            response.getWriter().println("You must be logged in to view transactions.");
            return;
        }

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish Connection to MySQL database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDemo", "root", "7395897383");

            // Fetch transactions for the logged-in customer
            String fetchTransactionsQuery = "SELECT * FROM Transactions WHERE customer_id = ?";
            PreparedStatement fetchTransactionsStmt = conn.prepareStatement(fetchTransactionsQuery);
            fetchTransactionsStmt.setInt(1, customerId);

            ResultSet rs = fetchTransactionsStmt.executeQuery();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Transaction History</title></head><body>");
            out.println("<h2>Transaction History</h2>");
            out.println("<table border='1'><tr><th>ID</th><th>Date</th><th>Type</th><th>Amount</th><th>Balance</th></tr>");

            while (rs.next()) {
                out.println("<tr><td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getDate("date") + "</td>");
                out.println("<td>" + rs.getString("type") + "</td>");
                out.println("<td>" + rs.getDouble("amount") + "</td>");
                out.println("<td>" + rs.getDouble("balance") + "</td></tr>");
            }

            out.println("</table>");
            out.println("<a href='customer_dashboard.html'>Go to Dashboard</a>");
            out.println("</body></html>");

            fetchTransactionsStmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Exception: " + e.getMessage());
        }
    }
}
