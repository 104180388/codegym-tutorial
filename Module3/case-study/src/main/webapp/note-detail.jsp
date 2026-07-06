<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết Ghi chú</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* Khung bao ngoài cùng */
        .form-container {
            max-width: 500px;
            margin: 50px auto;
            border: 1px solid #333;
            padding: 30px 40px;
            background-color: #fff;
        }

        /* Tiêu đề của ghi chú */
        .note-title {
            font-size: 18px;
            margin-bottom: 10px;
        }

        /* ---------------------------------------------------
           CSS TẠO HIỆU ỨNG TỜ GIẤY GHI CHÚ MÀU VÀNG
           --------------------------------------------------- */
        .notepad {
            position: relative;
            background-color: #fffab3; /* Màu nền vàng nhạt */
            padding: 20px 20px 40px 40px; /* Thụt lề trái nhiều hơn một chút */
            margin-bottom: 30px;
            min-height: 150px;
            font-size: 16px;
            line-height: 30px; /* Khớp với khoảng cách dòng kẻ */

            /* Tạo dòng kẻ ngang màu cam nhạt */
            background-image: repeating-linear-gradient(transparent, transparent 29px, #e8d087 30px);

            /* Tạo đường chỉ đỏ dọc bên trái (giống lề vở) */
            border-left: 2px solid #ffb3b3;
        }

        /* Tạo viền răng cưa ở dưới đáy tờ giấy */
        .notepad::after {
            content: "";
            position: absolute;
            bottom: -10px; /* Kéo xuống dưới phần nền vàng */
            left: -2px;    /* Khớp với viền trái */
            right: 0;
            height: 10px;
            background-size: 10px 10px;
            /* Dùng linear-gradient để tạo hình tam giác liên tiếp (răng cưa) */
            background-image:
                linear-gradient(135deg, #fffab3 50%, transparent 50%),
                linear-gradient(225deg, #fffab3 50%, transparent 50%);
            background-position: left top;
        }
    </style>
</head>
<body class="bg-light">

<div class="container">
    <div class="form-container">
        <h2 class="text-center mb-4">Ghi chú</h2>

        <div class="note-title">
            ${note.title}
        </div>

        <div class="notepad">
            ${note.content}
        </div>

        <div class="d-flex justify-content-between px-2">
            <a href="delete-note?id=${note.id}" class="btn btn-light border-dark px-4"
               onclick="return confirm('Bạn có chắc chắn muốn xóa ghi chú này?');">Xóa</a>

            <a href="edit-note?id=${note.id}" class="btn btn-light border-dark px-4">Sửa</a>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>