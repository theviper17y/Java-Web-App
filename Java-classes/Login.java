
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.http.Cookie;

@WebServlet(urlPatterns = {"/login"})
public class Login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            response.sendRedirect("dashboard"); 
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/html");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String message = "";

        // Validate input
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            message = "Username and password cannot be empty.";
        } else {
            // Check credentials against the database
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = Db.getConnection();
                String query = "SELECT passwd,user_role FROM users WHERE username=?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    String hashedPassword = rs.getString("passwd"); // Corrected to match the column name
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        // User found, login successful
                        String user_role = rs.getString("user_role");
                        String csrfToken = UUID.randomUUID().toString(); // Generate a unique token

                        HttpSession session = request.getSession();
                        session.setAttribute("username", username);
                        session.setAttribute("user_role", user_role);
                        session.setAttribute("csrfToken", csrfToken); // Store it in the session
                        
                        
                        // Set a cookie to remember the username
                        Cookie userCookie = new Cookie("username", username);
                        userCookie.setMaxAge(60 * 60 * 24); // 1 day
                        response.addCookie(userCookie);
                        
                        response.sendRedirect("dashboard");// Redirect to dashboard
                        return; // Stop further processing
                    } else {
                        // Invalid credentials
                        message = "Invalid username or password.";
                    }
                } else {
                    // User not found
                    message = "Invalid username or password.";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                message = "Database error: " + e.getMessage();
            } finally {
                // Close resources in the reverse order of their creation
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Set the message as a request attribute and forward to login.jsp
        request.setAttribute("message", message);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
        dispatcher.forward(request, response);
    }
}