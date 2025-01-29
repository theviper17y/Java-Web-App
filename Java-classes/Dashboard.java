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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/dashboard"})
public class Dashboard extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if the user is logged in
        HttpSession session = request.getSession(false);
        String username = null;

        if (session != null) {
            username = (String) session.getAttribute("username");
        }

        if (username == null) {
            response.sendRedirect("login"); // Redirect to login if not logged in
            return;
        }

        // Fetch books from the database
        List<Map<String, Object>> books = new ArrayList<>();
        String searchQuery = request.getParameter("search");

        try (Connection connection = Db.getConnection()) {
            String sql = "SELECT id, title, author, cyear, image FROM books"; // Default query to fetch all books

            // Add search conditions if a search query is provided
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                sql += " WHERE title LIKE ? OR author LIKE ? OR cyear LIKE ?";
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameters for the search query
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                    String searchParam = "%" + searchQuery + "%"; // Wildcard for LIKE
                    statement.setString(1, searchParam);
                    statement.setString(2, searchParam);
                    statement.setString(3, searchQuery); // Assuming cyear is a string; adjust if it's an integer
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Map<String, Object> book = new HashMap<>();
                        book.put("id", resultSet.getInt("id"));
                        book.put("title", resultSet.getString("title"));
                        book.put("author", resultSet.getString("author"));
                        book.put("cyear", resultSet.getInt("cyear"));
                        book.put("image", resultSet.getString("image"));

                        // Check if the user has reserved this book
                        boolean isReserved = false;
                        String checkReservationSql = "SELECT COUNT(*) FROM reservations WHERE username = ? AND book_id = ?";
                        try (PreparedStatement checkStatement = connection.prepareStatement(checkReservationSql)) {
                            checkStatement.setString(1, username);
                            checkStatement.setInt(2, resultSet.getInt("id"));
                            try (ResultSet checkResultSet = checkStatement.executeQuery()) {
                                if (checkResultSet.next() && checkResultSet.getInt(1) > 0) {
                                    isReserved = true;
                                }
                            }
                        }

                        book.put("isReserved", isReserved); // Add reservation status to the book map
                        books.add(book);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately in production code
        }

        // Set the books list as a request attribute
        request.setAttribute("books", books);
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}