<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách Ghi chú</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* Tùy chỉnh CSS để bảng giống với mẫu (tiêu đề cột màu xám) */
        .table thead th {
            background-color: #dcdcdc;
            font-weight: bold;
            border-bottom: 2px solid #333;
        }
        .pagination-text {
            font-size: 16px;
        }
    </style>
</head>
<body class="bg-light">

<div class="container mt-5">
    <h2 class="text-center mb-4">Các ghi chú</h2>

    <form action="list-notes" method="GET" class="row g-2 mb-3 align-items-center">
            <div class="col-auto">
                <select name="typeId" class="form-select border-dark">
                    <option value="">Thể loại</option>
                    <option value="1">Công việc</option>
                    <option value="2">Cá nhân</option>
                    <option value="3">Học tập</option>
                </select>
            </div>


            <div class="col">
                <input type="text" name="keyword" class="form-control border-dark" placeholder="Tiêu đề" value="${param.keyword}">
            </div>

            <div class="col-auto">
                <button type="submit" class="btn btn-light border-dark px-4">Tìm</button>
            </div>

            <div class="col-auto">
                <a href="add-note.jsp" class="btn btn-primary px-3 border-dark">
                    + Thêm ghi chú
                </a>
            </div>
        </form>

    <div class="table-responsive">
        <table class="table table-bordered border-dark align-middle">
            <thead>
                <tr>
                    <th scope="col" style="width: 5%;">▼ STT</th>
                    <th scope="col" style="width: 25%;">▼ Tiêu đề</th>
                    <th scope="col" style="width: 35%;">▼ Nội dung ghi chú</th> <th scope="col" style="width: 20%;">▼ Phân loại</th>
                    <th scope="col" style="width: 15%;">▼ Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="note" items="${noteList}" varStatus="status">
                    <tr>
                        <td>${status.index + 1}</td>

                        <td><strong class="text-primary">${note.title}</strong></td>

                        <td>${note.content}</td>

                        <td>${note.type}</td>

                        <td>
                            <a href="delete-note?id=${note.id}" class="btn btn-sm btn-danger text-white" onclick="return confirm('Bạn có chắc chắn muốn xóa ghi chú này?');">Xóa</a>
                            |
                            <a href="edit-note?id=${note.id}" class="btn btn-sm btn-warning text-dark">Sửa</a>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty noteList}">
                    <tr>
                        <td colspan="5" class="text-center text-muted">Không tìm thấy ghi chú nào khớp với điều kiện.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>