package controller;

import model.NoteManagement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/delete-note")
public class NoteDeleteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));

        HttpSession session = request.getSession();
        NoteManagement management = (NoteManagement) session.getAttribute("noteManagement");

        if (management != null) {
            management.removeNote(id);
        }

        response.sendRedirect("list-notes");
    }
}