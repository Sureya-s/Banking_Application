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
import javax.servlet.http.HttpSession;

@WebServlet("/DepositServlet")
public class DepositServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customerId");
        double amount = Double.parseDouble(request.getParameter("amount"));

        if (customerId == null) {
            response.getWriter().println("You must be logged in to deposit funds.");
            return;
        }

        Connection conn = null;
        PreparedStatement updateBalanceStmt = null;
        PreparedStatement insertTransactionStmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankingDemo", "root", "7395897383");

            conn.setAutoCommit(false); // Enable transaction management

            String updateBalanceQuery = "UPDATE CustomerDetails SET initial_balance = initial_balance + ? WHERE id = ?";
            updateBalanceStmt = conn.prepareStatement(updateBalanceQuery);
            updateBalanceStmt.setDouble(1, amount);
            updateBalanceStmt.setInt(2, customerId);

            int rowsUpdated = updateBalanceStmt.executeUpdate();

            if (rowsUpdated > 0) {
                String insertTransactionQuery = "INSERT INTO Transactions (customer_id, type, amount, balance) VALUES (?, 'Deposit', ?, (SELECT initial_balance FROM CustomerDetails WHERE id = ?))";
                insertTransactionStmt = conn.prepareStatement(insertTransactionQuery);
                insertTransactionStmt.setInt(1, customerId);
                insertTransactionStmt.setDouble(2, amount);
                insertTransactionStmt.setInt(3, customerId);
                insertTransactionStmt.executeUpdate();

                conn.commit(); // Commit the transaction

                response.sendRedirect("deposit_success.html");
            } else {
                conn.rollback(); // Rollback if the balance update failed
                response.getWriter().println("Failed to deposit amount.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback if an exception occurs
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            response.getWriter().println("Exception: " + e.getMessage());
        } finally {
            if (updateBalanceStmt != null) {
                try {
                    updateBalanceStmt.close();
                } catch (Exception closeEx) {
                    closeEx.printStackTrace();
                }
            }
            if (insertTransactionStmt != null) {
                try {
                    insertTransactionStmt.close();
                } catch (Exception closeEx) {
                    closeEx.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
}
