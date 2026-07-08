<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý thuê phòng trọ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .table thead th { background-color: #e9ecef; font-weight: bold; border-bottom: 2px solid #333; }
        .disabled-lookup { background-color: #6c757d !important; color: white; }

        .custom-confirm-content { border: 2px solid #000 !important; border-radius: 15px !important; overflow: hidden; }
        .custom-confirm-footer { display: flex; padding: 0; margin: 0; border-top: 1px solid #000; }
        .custom-confirm-btn {
            background: none; border: none; width: 50%; padding: 10px 0;
            font-weight: bold; color: #000; text-align: center; text-decoration: none;
        }
        .custom-confirm-btn:hover { background-color: #f1f1f1; }
        .border-divider { border-right: 1px solid #000; }
    </style>
</head>
<body class="bg-light">
<div class="container mt-5">
    <h2 class="text-center mb-4 text-uppercase fw-bold text-success">Danh sách phòng thuê trọ</h2>

    <form action="list-rooms" method="GET" class="row g-2 mb-4">
        <div class="col-md-8">
            <input type="text" name="keyword" class="form-control border-dark"
                   placeholder="Tìm kiếm theo mã phòng, tên người thuê, hoặc số điện thoại..." value="${param.keyword}">
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-dark w-100">Tìm kiếm</button>
        </div>
        <div class="col-md-2">
            <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal" data-bs-target="#addRoomModal">
                + Thêm phòng
            </button>
        </div>
    </form>

    <form action="delete-room" method="POST" id="formBatchDelete">

        <div class="mb-2">
            <button type="button" class="btn btn-danger btn-sm px-3 fw-bold border-dark" id="btnOpenDeleteModal">
                🗑️ Xóa
            </button>
        </div>

        <div class="table-responsive">
            <table class="table table-bordered border-dark align-middle text-center">
                <thead>
                    <tr>
                        <th style="width: 4%;"><input type="checkbox" id="selectAll"></th>
                        <th style="width: 10%;">Mã phòng</th>
                        <th style="width: 20%;">Tên người thuê</th>
                        <th style="width: 13%;">Số điện thoại</th>
                        <th style="width: 13%;">Ngày bắt đầu</th>
                        <th style="width: 15%;">Hình thức thanh toán</th>
                        <th style="width: 15%;">Ghi chú</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="room" items="${roomList}">
                        <tr>
                            <td>
                                <!-- Checkbox mang giá trị ID số nguyên phục vụ xóa dưới DB -->
                                <input type="checkbox" name="selectedIds" value="${room.maPhongTro}" class="room-checkbox">
                            </td>
                            <!-- Hiển thị mã phòng định dạng chuỗi PT-00X -->
                            <td><strong>PT-00${room.maPhongTro}</strong></td>
                            <td class="text-start">${room.tenNguoiThue}</td>
                            <td>${room.soDienThoai}</td>
                            <td>${room.ngayBatDau}</td>
                            <td><span class="badge bg-info text-dark">${room.tenHinhThucThanhToan}</span></td>
                            <td class="text-start text-muted">${room.ghiChu}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty roomList}">
                        <tr><td colspan="8" class="text-center text-muted p-4">Không tìm thấy dữ liệu phòng trọ nào.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </form>
</div>

<div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-hidden="true" data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered" style="max-width: 420px;">
        <div class="modal-content custom-confirm-content">
            <div class="modal-header justify-content-center border-0 pt-3 pb-1">
                <h5 class="modal-title fw-bold text-dark fs-4">Xác nhận</h5>
            </div>
            <div class="modal-body text-center py-3 px-4 fs-5 text-dark">
                <p id="deleteModalMessage" class="fw-bold mb-0" style="line-height: 1.5;"></p>
            </div>
            <div class="custom-confirm-footer">
                <button type="button" class="custom-confirm-btn border-divider" data-bs-dismiss="modal">Không</button>
                <button type="button" id="btnConfirmSubmit" class="custom-confirm-btn">Có</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="addRoomModal" data-bs-backdrop="static" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-dark border-2">
            <div class="modal-header bg-light border-bottom border-dark">
                <h5 class="modal-title fw-bold">Tạo thông tin thuê trọ</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body px-5 py-4">
                <div id="errorAlert" class="alert alert-danger d-none" role="alert"></div>
                <form action="add-room" method="POST" id="addRoomForm">
                    <div class="row mb-3 align-items-center">
                        <label class="col-sm-4 fw-bold">Mã phòng trọ</label>
                        <div class="col-sm-8"><input type="text" class="form-control disabled-lookup" value="PT-Tự động tăng" readonly disabled></div>
                    </div>
                    <div class="row mb-3 align-items-center">
                        <label class="col-sm-4 fw-bold">Tên người thuê <span class="text-danger">*</span></label>
                        <div class="col-sm-8"><input type="text" name="tenNguoiThue" id="tenNguoiThue" class="form-control border-dark" required></div>
                    </div>
                    <div class="row mb-3 align-items-center">
                        <label class="col-sm-4 fw-bold">Số điện thoại <span class="text-danger">*</span></label>
                        <div class="col-sm-8"><input type="text" name="soDienThoai" id="soDienThoai" class="form-control border-dark" required></div>
                    </div>
                    <div class="row mb-3 align-items-center">
                        <label class="col-sm-4 fw-bold">Ngày bắt đầu thuê <span class="text-danger">*</span></label>
                        <div class="col-sm-8"><input type="date" name="ngayBatDau" id="ngayBatDau" class="form-control border-dark" required></div>
                    </div>
                    <div class="row mb-3 align-items-center">
                        <label class="col-sm-4 fw-bold">Hình thức thanh toán <span class="text-danger">*</span></label>
                        <div class="col-sm-8">
                            <select name="maHinhThucThanhToan" id="maHinhThucThanhToan" class="form-select border-dark" required>
                                <option value="">-- Chọn hình thức --</option>
                                <option value="1">Theo tháng</option>
                                <option value="2">Theo quý</option>
                                <option value="3">Theo năm</option>
                            </select>
                        </div>
                    </div>
                    <div class="row mb-4">
                        <label class="col-sm-4 fw-bold">Ghi chú</label>
                        <div class="col-sm-8"><textarea name="ghiChu" id="ghiChu" class="form-control border-dark" rows="3"></textarea></div>
                    </div>
                    <div class="d-flex justify-content-center gap-3 border-top pt-3">
                        <button type="submit" class="btn btn-light border-dark px-4 fw-bold" style="box-shadow: 2px 2px 0px #000;">Tạo mới</button>
                        <button type="button" class="btn btn-light border-dark px-4 fw-bold" data-bs-dismiss="modal" style="box-shadow: 2px 2px 0px #000;">Hủy</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.getElementById('selectAll').addEventListener('change', function() {
        let checkboxes = document.querySelectorAll('.room-checkbox');
        for (let checkbox of checkboxes) { checkbox.checked = this.checked; }
    });

    const deleteModalObj = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));

    document.getElementById('btnOpenDeleteModal').addEventListener('click', function() {
        let checkedBoxes = document.querySelectorAll('.room-checkbox:checked');

        if (checkedBoxes.length === 0) {
            alert("Vui lòng tích chọn ít nhất một phòng trọ để tiến hành xóa!");
            return;
        }

        let formattedCodes = [];
        checkedBoxes.forEach(function(cb) {
            formattedCodes.push("PT-00" + cb.value);
        });

        let messageText = "Bạn có muốn xóa thông tin thuê trọ " + formattedCodes.join(', ') + " hay không?";
        document.getElementById('deleteModalMessage').innerText = messageText;

        deleteModalObj.show();
    });

    function triggerSingleDelete(id) {
        document.querySelectorAll('.room-checkbox').forEach(cb => cb.checked = false);
        let targetCheckbox = document.querySelector(`.room-checkbox[value="${id}"]`);
        if (targetCheckbox) {
            targetCheckbox.checked = true;
        }
        let messageText = "Bạn có muốn xóa thông tin thuê trọ PT-00" + id + " hay không?";
        document.getElementById('deleteModalMessage').innerText = messageText;

        deleteModalObj.show();
    }

    document.getElementById('btnConfirmSubmit').addEventListener('click', function() {
        // Submit form gửi mảng tham số "selectedIds" lên Servlet xử lý xóa vật lý
        document.getElementById('formBatchDelete').submit();
    });

    document.getElementById('ngayBatDau').min = new Date().toISOString().split('T')[0];
</script>
</body>
</html>