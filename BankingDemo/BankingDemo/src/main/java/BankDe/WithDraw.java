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

@WebServlet("/WithDraw")
public class WithDraw extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customerId");
        double amount = Double.parseDouble(request.getParameter("amount"));

        if (customerId == null) {
            response.getWriter().println("You must be logged in to withdraw funds.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDemo", "root", "7395897383");

            String checkBalanceQuery = "SELECT initial_balance FROM CustomerDetails WHERE id = ?";
            PreparedStatement checkBalanceStmt = conn.prepareStatement(checkBalanceQuery);
            checkBalanceStmt.setInt(1, customerId);
            ResultSet rs = checkBalanceStmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("initial_balance");

                if (balance >= amount) {
                    String updateBalanceQuery = "UPDATE CustomerDetails SET initial_balance = initial_balance - ? WHERE id = ?";
                    PreparedStatement updateBalanceStmt = conn.prepareStatement(updateBalanceQuery);
                    updateBalanceStmt.setDouble(1, amount);
                    updateBalanceStmt.setInt(2, customerId);

                    int rowsUpdated = updateBalanceStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        String insertTransactionQuery = "INSERT INTO Transactions (customer_id, type, amount, balance) VALUES (?, 'Withdraw', ?, (SELECT initial_balance FROM CustomerDetails WHERE id = ?))";
                        PreparedStatement insertTransactionStmt = conn.prepareStatement(insertTransactionQuery);
                        insertTransactionStmt.setInt(1, customerId);
                        insertTransactionStmt.setDouble(2, amount);
                        insertTransactionStmt.setInt(3, customerId);
                        insertTransactionStmt.executeUpdate();

                        // Get the updated balance
                        String getUpdatedBalanceQuery = "SELECT initial_balance FROM CustomerDetails WHERE id = ?";
                        PreparedStatement getUpdatedBalanceStmt = conn.prepareStatement(getUpdatedBalanceQuery);
                        getUpdatedBalanceStmt.setInt(1, customerId);
                        ResultSet updatedRs = getUpdatedBalanceStmt.executeQuery();

                        if (updatedRs.next()) {
                            double updatedBalance = updatedRs.getDouble("initial_balance");
                            // Set updated balance in session
                            session.setAttribute("currentBalance", updatedBalance);
                        }

                        response.sendRedirect("with_success.html");
                    } else {
                        response.getWriter().println("Failed to withdraw amount.");
                    }
                } else {
                    response.getWriter().println("Insufficient balance.");
                }
            } else {
                response.getWriter().println("Customer not found.");
            }

            checkBalanceStmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Exception: " + e.getMessage());
        }
    }
}
