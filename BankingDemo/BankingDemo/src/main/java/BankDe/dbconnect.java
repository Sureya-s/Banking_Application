package BankDe;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;


/**
 * Servlet implementation class dbconnect
 */
@WebServlet("/dbconnect")
public class dbconnect extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public dbconnect() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String _uname = request.getParameter("username");

        String _pass = request.getParameter("password");

        String jdbcUrl = "jdbc:mysql://localhost:3306/BankingDemo";

        String jdbcUser = "root";

        String jdbcPassword = "7395897383";

        

        Connection con = null;

        PreparedStatement pstmt = null;

        ResultSet rs = null;



        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);

            String sql = "SELECT username, password FROM Admin WHERE username = ? AND password = ?";

            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, _uname);

            pstmt.setString(2, _pass);

            rs = pstmt.executeQuery();

            

            if (rs.next()) {

                response.sendRedirect("admin_dashboard.html");

            } else {

                response.sendRedirect("admin.html");

            }

        } catch (ClassNotFoundException e) {

            throw new ServletException("JDBC Driver not found", e);

        } catch (SQLException e) {

            throw new ServletException("SQL Error", e);

        } finally {

            try {

                if (rs != null) rs.close();

                if (pstmt != null) pstmt.close();

                if (con != null) con.close();

            } catch (SQLException e) {

                e.printStackTrace();

            }

        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
