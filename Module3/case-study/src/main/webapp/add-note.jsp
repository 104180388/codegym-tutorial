<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm mới ghi chú</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* Tùy chỉnh một chút form cho giống với form mẫu (có viền) */
        .form-container {
            max-width: 500px;
            margin: 50px auto;
            border: 1px solid #333;
            padding: 30px;
            background-color: #fff;
        }
    </style>
</head>
<body class="bg-light">

<div class="container">
    <div class="form-container">
        <h3 class="text-center mb-4">Thêm mới ghi chú</h3>

        <form action="add-note" method="POST">

            <div class="row mb-3 align-items-center">
                <label for="title" class="col-sm-3 col-form-label">Tiêu đề</label>
                <div class="col-sm-9">
                    <input type="text" class="form-control border-dark" id="title" name="title" required>
                </div>
            </div>

            <div class="row mb-4">
                <label for="content" class="col-sm-3 col-form-label">Nội dung</label>
                <div class="col-sm-9">
                    <textarea class="form-control border-dark" id="content" name="content" rows="5" required></textarea>
                </div>
            </div>

            <div class="row mb-4 align-items-center">
                <label for="typeId" class="col-sm-3 col-form-label">Phân loại</label>
                <div class="col-sm-9">
                    <select name="typeId" id="typeId" class="form-select border-dark" required>
                        <option value="1">Công việc</option>
                        <option value="2">Cá nhân</option>
                        <option value="3">Học tập</option>
                    </select>
                </div>
            </div>

            <div class="row text-center">
                <div class="col-12">
                    <a href="list-notes" class="btn btn-light border-dark px-4 me-3">Hủy</a>

                    <button type="submit" class="btn btn-light border-dark px-4">Lưu</button>
                </div>
            </div>



        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>