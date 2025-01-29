import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/addBook"})
@MultipartConfig
public class AddBook extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads"; // Directory to save uploaded images

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("user_role") : null; // Get user role from session

        // Check if the user is an admin
        if (userRole == null || !userRole.equals("admin")) {
            request.getSession().setAttribute("notificationMessage", "You do not have permission to add books.");
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
        
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String cyear = request.getParameter("cyear");
        Part imagePart = request.getPart("image"); // Get the uploaded file part

        // Create the uploads directory if it doesn't exist
        File uploadDir = new File(getServletContext().getRealPath("") + File.separator + UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
            System.out.println("Created upload directory: " + uploadDir.getAbsolutePath());
        }

        // Save the uploaded file
        String imageFileName = imagePart.getSubmittedFileName();
        // Generate a unique file name to avoid overwriting
        String uniqueFileName = System.currentTimeMillis() + "_" + imageFileName; // Append timestamp
        File file = new File(uploadDir, uniqueFileName);

        try {
            System.out.println("Uploaded file name: " + imageFileName);
            System.out.println("Uploaded file size: " + imagePart.getSize());
            Files.copy(imagePart.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions appropriately in production code
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "File upload failed.");
            return; // Exit the method if file upload fails
        }

        // Prepare the image path to store in the database
        String imagePath = UPLOAD_DIR + File.separator + uniqueFileName; // Store the unique file name

        // Insert the book details into the database
        try (Connection connection = Db.getConnection()) {
            String sql = "INSERT INTO books (title, author, cyear, image) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, title);
                statement.setString(2, author);
                statement.setString(3, cyear);
                statement.setString(4, imagePath); // Store the relative path to the image
                statement.executeUpdate();
                System.out.println("Book added successfully: " + title);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately in production code
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
            return; // Exit the method if database insertion fails
        }

        response.sendRedirect("dashboard"); // Redirect back to the dashboard after adding
    }
}