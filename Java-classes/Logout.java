
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import jakarta.servlet.http.Cookie;

@WebServlet(urlPatterns = {"/logout"})
public class Logout extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Invalidate the session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Clear the username cookie
        Cookie userCookie = new Cookie("username", null);
        userCookie.setMaxAge(0); // Set cookie to expire immediately
        response.addCookie(userCookie);

        // Redirect to the login page
        response.sendRedirect("login");
    }
}