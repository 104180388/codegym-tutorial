package controller;

import model.NoteManagement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/add-note")
public class NoteAddServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String title = request.getParameter("title");
        String content = request.getParameter("content");
        int typeId = Integer.parseInt(request.getParameter("typeId"));

        HttpSession session = request.getSession();
        NoteManagement management = (NoteManagement) session.getAttribute("noteManagement");
        if (management == null) {
            management = new NoteManagement();
        }
        management.addNote(title, content, typeId);
        response.sendRedirect("list-notes");
    }
}