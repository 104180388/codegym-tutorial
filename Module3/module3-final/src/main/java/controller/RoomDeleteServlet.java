package controller;

import model.RoomDB;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/delete-room")
public class RoomDeleteServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] selectedIds = request.getParameterValues("selectedIds");
        if (selectedIds != null && selectedIds.length > 0) {
            RoomDB.deleteMultiple(selectedIds);
        }
        response.sendRedirect("list-rooms");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("list-rooms");
    }
}