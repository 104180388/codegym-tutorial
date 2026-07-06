package controller;

import model.Note;
import model.NoteDB;
import model.NoteManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/edit-note")
public class NoteEditServlet extends HttpServlet {

    // GET: Tìm ghi chú theo ID để đổ ngược dữ liệu vào form sửa
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        HttpSession session = request.getSession();
        NoteManagement management = (NoteManagement) session.getAttribute("noteManagement");

        Note targetNote = null;
        if (management != null) {
            // ĐÃ SỬA TẠI ĐÂY: Truyền thêm tham số null để lấy tất cả thể loại khi tìm theo ID
            Note[] allNotes = management.searchNotes("", null);

            for (Note n : allNotes) {
                    targetNote = n;
                    break;
            }
        }

        request.setAttribute("note", targetNote);
        request.getRequestDispatcher("edit-note.jsp").forward(request, response);
    }

    // POST: Nhận dữ liệu chỉnh sửa từ form và thực hiện lưu đè (Save)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("id"));
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        int typeId = Integer.parseInt(request.getParameter("typeId"));

        HttpSession session = request.getSession();
        NoteManagement management = (NoteManagement) session.getAttribute("noteManagement");

        if (management != null) {
                NoteDB dbNote = new NoteDB(id, title, content, typeId);
                dbNote.save();
        }

        response.sendRedirect("list-notes");
    }
}