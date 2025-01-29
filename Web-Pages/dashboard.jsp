<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.*" %>
<%@ page import="jakarta.servlet.http.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>

<%
    // Get the username and role from the session
    String username = (session != null) ? (String) session.getAttribute("username") : null;
    String userRole = (session != null) ? (String) session.getAttribute("user_role") : null; // Assuming user_role is stored in session
    String csrfToken = (session != null) ? (String) session.getAttribute("csrfToken") : null;
    
    // Redirect to login if not logged in
    if (username == null) {
        response.sendRedirect("login");
        return;
    }
    
    

    // Get the books from the request attribute
    List<Map<String, Object>> books = (List<Map<String, Object>>) request.getAttribute("books");
    String notificationMessage = (String) request.getSession().getAttribute("notificationMessage"); // For notifications
    request.getSession().removeAttribute("notificationMessage"); // Clear the message after displaying it
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Books Dashboard</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <style>
       body {
            display: flex;
            flex-direction: column;
            min-height: 100vh; /* Full height of the viewport */
            background-color: #f8f9fa;
        }
        .header {
            background-color: #343a40;
            color: white;
            padding: 20px 0;
            text-align: center;
        }
        .content {
            flex: 1; /* This makes the content area flexible */
            padding: 20px; /* Add some padding */
        }
        .book-card {
            margin-bottom: 20px;
            transition: transform 0.2s;
        }
        .book-card:hover {
            transform: scale(1.05);
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
        }
        .book-image {
            width: 100%;
            height: 200px;
            object-fit: cover;
        }
        .footer {
            background-color: #343a40;
            color: white;
            text-align: center;
            padding: 10px 0;
        }
        .notification {
            display: none; /* Initially hidden */
        }
    </style>
    <script>
        function showNotification(message) {
            const notification = document.getElementById('notification');
            notification.innerText = message;
            notification.style.display = 'block'; // Show notification
            setTimeout(() => {
                notification.style.display = 'none'; // Hide after 3 seconds
            }, 3000);
        }
    </script>
</head>
<body>
    <div class="header">
        <h1>Library Dashboard</h1>
        <h3>Welcome, <%= StringEscapeUtils.escapeHtml4(username) %>!</h3>
        <form method="GET" action="logout" style="display:inline;">
            <input type="hidden" name="csrfToken" value="<%=csrfToken%>">
            <button type="submit" class="btn btn-danger">Logout</button>
        </form>
    </div>

    <div class="content">
        <div class="container mt-4">
            <% if (notificationMessage != null) { %>
                <div class="alert alert-success" id="notification">
                    <%= StringEscapeUtils.escapeHtml4(notificationMessage) %>
                </div>
                <script>showNotification("<%= StringEscapeUtils.escapeHtml4(notificationMessage) %>");</script>
            <% } %>
            <!-- Search Form -->
            <form method="GET" action="dashboard" class="mb-4">
                <input type="hidden" name="csrfToken" value="<%=csrfToken%>">
                <div class="input-group">
                    <input type="text" name="search" class="form-control" placeholder="Search by title, author, or year"
                           value="<%= request.getParameter("search") != null ? StringEscapeUtils.escapeHtml4(request.getParameter("search")) : "" %>">
                    <div class="input-group-append">
                        <button class="btn btn-primary" type="submit">Search</button>
                    </div>
                </div>
            </form>
                    
            <%if ("admin".equals(userRole)){%>
            <form method="GET" action="addBook.jsp" class="mb-4">
                <div class="input-group">
                    <div class="input-group-append">
                        <button class="btn btn-success border rounded" type="submit">Add Books</button>
                    </div>
                </div>
            </form>
            <%}%>

            <div class="row">
                <%
                    if (books == null || books.isEmpty()) {
                %>
                    <div class="col-12">
                        <h3 class="text-center">No books available.</h3>
                    </div>
                <%
                    } else {
                        for (Map<String, Object> book : books) {
                %>
                    <div class="col-md-4">
                        <div class="card book-card shadow-sm">
                            <img src="<%= book.get("image") %>" class="card-img-top book-image" alt="<%= StringEscapeUtils.escapeHtml4((String) book.get("title")) %>">
                            <div class="card-body">
                                <h5 class="card-title"><%= StringEscapeUtils.escapeHtml4((String) book.get("title")) %></h5>
                                <p class="card-text"><strong>Author:</strong> <%= StringEscapeUtils.escapeHtml4((String) book.get("author")) %></p>
                                <p class="card-text"><strong>Year:</strong> <%= book.get("cyear") %></p>
                                <% if (!(Boolean) book.get("isReserved")) { %> <!-- Show Reserve button only if not reserved -->
                                    <form method="POST" action="reserveBook" onsubmit="disableReserveButton(this.querySelector('button'));">
                                        <input type="hidden" name="bookId" value="<%= book.get("id") %>">
                                        <input type="hidden" name="username" value="<%= username %>">
                                        <input type="hidden" name="csrfToken" value="<%=csrfToken%>">
                                        <button type="submit" class="btn btn-success" style="background-color: green;">Reserve</button>
                                    </form>
                                <% } else { %> <!-- Show Return button if reserved -->
                                    <form method="POST" action="returnBook">
                                        <input type="hidden" name="bookId" value="<%= book.get("id") %>">
                                        <input type="hidden" name="username" value="<%= username %>">
                                        <input type="hidden" name="csrfToken" value="<%=csrfToken%>">
                                        <button type="submit" class="btn btn-warning">Return</button>
                                    </form>
                                <% } %>
                                <br>
                                <% if ("admin".equals(userRole)) { %>
                                    <form method="POST" action="removeBook" onsubmit="return confirm('Are you sure you want to delete this book?');">
                                        <input type="hidden" name="bookId" value="<%= book.get("id") %>">
                                        <input type="hidden" name="csrfToken" value="<%=csrfToken%>">
                                        <button type="submit" class="btn btn-danger">Delete</button>
                                    </form>
                                <% } %>
                            </div>
                        </div>
                    </div>
                <%
                        }
                    }
                %>
            </div>
        </div>
    </div>

    <div class="footer">
        <p>&copy; 2025 Youcef Hamdani. All rights reserved.</p>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>