package controller;

import model.RoomDB;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/add-room")
public class RoomAddServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String tenNguoiThue = request.getParameter("tenNguoiThue").trim();
        String soDienThoai = request.getParameter("soDienThoai").trim();
        String ngayBatDauStr = request.getParameter("ngayBatDau");
        String maHinhThucStr = request.getParameter("maHinhThucThanhToan");
        String ghiChu = request.getParameter("ghiChu");

        boolean isValid = true;

        if (tenNguoiThue.isEmpty() || soDienThoai.isEmpty() || ngayBatDauStr == null || maHinhThucStr == null) {
            isValid = false;
        }

        if (tenNguoiThue.length() < 5 || tenNguoiThue.length() > 50 || !tenNguoiThue.matches("^[\\p{L}\\s]+$")) {
            isValid = false;
        }

        if (!soDienThoai.matches("^\\d{10}$")) {
            isValid = false;
        }

        Date sqlDate = null;
        try {
            LocalDate inputDate = LocalDate.parse(ngayBatDauStr);
            LocalDate today = LocalDate.now();
            if (inputDate.isBefore(today)) {
                isValid = false;
            }
            sqlDate = Date.valueOf(inputDate);
        } catch (Exception e) {
            isValid = false;
        }

        if (ghiChu != null && ghiChu.length() > 200) {
            isValid = false;
        }

        if (isValid) {
            int maHinhThucThanhToan = Integer.parseInt(maHinhThucStr);
            RoomDB newRoom = new RoomDB(0, tenNguoiThue, soDienThoai, sqlDate, maHinhThucThanhToan, ghiChu);
            newRoom.save();
        }

        response.sendRedirect("list-rooms");
    }
}