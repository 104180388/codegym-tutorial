package controller;

import model.Room;
import model.RoomManagement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/list-rooms")
public class RoomListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        RoomManagement management = (RoomManagement) session.getAttribute("roomManagement");
        if (management == null) {
            management = new RoomManagement();
            session.setAttribute("roomManagement", management);
        }

        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";

        Room[] rooms = management.searchRooms(keyword);

        request.setAttribute("roomList", rooms);
        request.getRequestDispatcher("list-rooms.jsp").forward(request, response);
    }
}