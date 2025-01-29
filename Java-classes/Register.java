
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/register"})
public class Register extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            response.sendRedirect("dashboard"); 
            return;
        }
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String message = "";
         String username = request.getParameter("username") != null ? request.getParameter("username").replaceAll("\\s+", "_") : null;
        String password = request.getParameter("password");

        // Validate input
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            message = "Please fill in all fields.";
        } else {
            Connection conn = null;
            PreparedStatement checkStmt = null;
            ResultSet checkRs = null;
            PreparedStatement insertStmt = null;

            try {
                conn = Db.getConnection();

                // Check if the username already exists
                String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
                checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, username);
                checkRs = checkStmt.executeQuery();

                if (checkRs.next() && checkRs.getInt(1) > 0) {
                    message = "Username already exists";
                } else {
                    if(username.matches(".*[!@#$%^&*()+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")){
                        message = "Username contains bad chars";
                    } else if (username.length()>120 || username.length()<4){
                        message = "Username error";
                    
                    }else if (password.length() < 8 || 
                        !password.matches(".*[A-Z].*") || 
                        !password.matches(".*[0-9].*") || 
                        !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                        message = "Please choose a strong password";
                    } else {
                        // Hash the password
                        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                        // Prepare SQL statement to insert new user
                        String sql = "INSERT INTO users (username, passwd) VALUES (?, ?)";
                        insertStmt = conn.prepareStatement(sql);
                        insertStmt.setString(1, username);
                        insertStmt.setString(2, hashedPassword);
                        int rowsAffected = insertStmt.executeUpdate();

                        if (rowsAffected > 0) {
                            message = "Registration successful!";
                        } else {
                            message = "Registration failed";
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                message = "Error: " + e.getMessage();
            } finally {
                // Close resources in the reverse order of their creation
                try {
                    if (checkRs != null) checkRs.close();
                    if (checkStmt != null) checkStmt.close();
                    if (insertStmt != null) insertStmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Set the message as a request attribute and forward to the registration page
        request.setAttribute("message", message);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/register.jsp");
        dispatcher.forward(request, response);
    }
}