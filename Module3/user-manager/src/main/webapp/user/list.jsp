<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%-- Đảm bảo dòng thư viện JSTL Core này chính xác cho Tomcat 9 --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>User Management Application</title>
    <style>
        /* Thêm chút style đơn giản để bảng dễ nhìn hơn */
        table { width: 80%; border-collapse: collapse; margin-top: 20px; }
        th, td { padding: 10px; text-align: center; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
<center>
    <h1>User Management</h1>
    <h2>
        <%-- Đã sửa đường dẫn tương đối bằng cách bỏ dấu / ở đầu --%>
        <a href="users?action=create">Add New User</a>
    </h2>
</center>
<div align="center">
    <table border="1" cellpadding="5">
        <caption><h2>List of Users</h2></caption>
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Country</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%-- Vòng lặp duyệt danh sách listUser gửi từ Servlet --%>
            <c:forEach var="user" items="${listUser}">
                <tr>
                    <td><c:out value="${user.id}"/></td>
                    <td><c:out value="${user.name}"/></td>
                    <td><c:out value="${user.email}"/></td>
                    <td><c:out value="${user.country}"/></td>
                    <td>
                        <%-- Đã sửa đường dẫn tương đối cho nút Edit và Delete --%>
                        <a href="users?action=edit&id=${user.id}">Edit</a> |
                        <a href="users?action=delete&id=${user.id}" onclick="return confirm('Are you sure?')">Delete</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>