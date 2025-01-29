<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.*" %>
<%@ page import="jakarta.servlet.http.*" %>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="java.nio.file.StandardCopyOption" %>

<%
    
    String username = (session != null) ? (String) session.getAttribute("username") : null;
    String userRole = (session != null) ? (String) session.getAttribute("user_role") : null;
    String csrfToken = (session != null) ? (String) session.getAttribute("csrfToken") : null;
    
    if (username == null || !"admin".equals(userRole)) {
        response.sendRedirect("login"); // Redirect to login if not logged in or not an admin
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Book</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <h2>Add New Book</h2>
        <form method="POST" action="addBook" enctype="multipart/form-data"> <!-- Added enctype for file upload -->
            <input type="hidden" name="csrfToken" value="<%=csrfToken%>">
            <div class="form-group">
                <label for="title">Title</label>
                <input type="text" class="form-control" id="title" name="title" required>
            </div>
            <div class="form-group">
                <label for="author">Author</label>
                <input type="text" class="form-control" id="author" name="author" required>
            </div>
            <div class="form-group">
                <label for="cyear">Year</label>
                <input type="text" class="form-control" id="cyear" name="cyear" required>
            </div>
            <div class="form-group">
                <label for="image">Image</label>
                <input type="file" class="form-control-file" id="image" name="image" accept="image/*" required> <!-- File input for image -->
            </div>
            <button type="submit" class="btn btn-primary">Add Book</button>
            <a href="dashboard" class="btn btn-secondary">Cancel</a>
        </form>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>