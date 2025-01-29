
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/removeBook"})
public class RemoveBook extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("user_role") : null; // Get user role from session

        // Check if the user is an admin
        if (userRole == null || !userRole.equals("admin")) {
            request.getSession().setAttribute("notificationMessage", "You do not have permission to delete books.");
            response.sendRedirect("dashboard"); // Redirect to dashboard if not admin
            return;
        }
        
        String csrfTokenFromSession = (String) session.getAttribute("csrfToken");
        String csrfTokenFromRequest = request.getParameter("csrfToken");

        // Validate the CSRF token
        if (csrfTokenFromSession == null || !csrfTokenFromSession.equals(csrfTokenFromRequest)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return; // Stop processing if the token is invalid
        }

        String bookId = request.getParameter("bookId");

        try (Connection connection = Db.getConnection()) {
            String sql = "DELETE FROM books WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, bookId);
                statement.executeUpdate();
            }
            request.getSession().setAttribute("notificationMessage", "Book deleted successfully!"); // Set notification in session
        } catch (SQLException e) {
            e.printStackTrace();
            request.getSession().setAttribute("notificationMessage", "Error deleting book: " + e.getMessage()); // Set error message in session
        }

        // Redirect to the dashboard
        response.sendRedirect("dashboard");
    }
}