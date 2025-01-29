<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.*" %>
<%@ page import="jakarta.servlet.http.*" %>
<%
    // Retrieve the message from the request (if any)
    String message = (String) request.getAttribute("message") != null ? (String) request.getAttribute("message") : "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register Page</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        body {
            background-color: rgba(220, 220, 220, 0.8);
            background-size: cover; /* Cover the entire viewport */
            background-position: center; /* Center the background image */
        }
        .container {
            margin-top: 100px;
            max-width: 400px;
        }
        .card {
            border: none; /* Remove border */
            border-radius: 8px; /* Optional: round the corners */
        }
        .message {
            margin-top: 15px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="card">
            <div class="card-body">
                <h2 class="card-title text-center">Register</h2>
                <div class="message">
                    <div class="alert <% if (message.contains("fill")||message.contains("Invalid")||message.contains("failed")) { %>alert-danger<% }else if(message.contains("choose")||message.contains("error")||message.contains("bad")){%>alert-warning<%} else if (message.contains("successful")) { %>alert-success<% } %> <% if (message.isEmpty()) { %>d-none<% } %>" role="alert">
                        <%= message %>
                    </div>
                </div>
                <form class="login-form" method="post" action="register">
                    <div class="form-group">
                        <label for="username">Username</label>
                        <input type="text" class="form-control" id="username" name="username" required>
                    </div>
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" class="form-control" id="password" name="password" required>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Register</button>
                </form>
                <p class="text-center mt-3">Already have an account? <a href="login" style="color: blue">Login here</a></p>
            </div>
        </div>
    </div>
    <!-- Bootstrap JS and dependencies -->
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>