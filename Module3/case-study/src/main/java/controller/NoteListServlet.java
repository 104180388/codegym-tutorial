package controller;

import model.Note;
import model.NoteManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/list-notes")
public class NoteListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        NoteManagement management = (NoteManagement) session.getAttribute("noteManagement");
        if (management == null) {
            management = new NoteManagement();
            session.setAttribute("noteManagement", management);
        }

        String storeType = request.getParameter("storeType");
        if (storeType != null && !storeType.isEmpty()) {
            management.changeNoteStore(storeType);
            session.setAttribute("currentStore", storeType.toUpperCase());
        } else if (session.getAttribute("currentStore") == null) {
            session.setAttribute("currentStore", "DB");
        }

        // ĐỌC CẢ 2 THAM SỐ TỪ list-notes.jsp GỬI LÊN
        String keyword = request.getParameter("keyword");
        String typeId = request.getParameter("typeId"); // Thêm dòng này

        if (keyword == null) { keyword = ""; }

        // GỌI HÀM TÌM KIẾM ĐÃ NÂNG CẤP
        Note[] notes = management.searchNotes(keyword, typeId);

        request.setAttribute("noteList", notes);
        request.getRequestDispatcher("list-notes.jsp").forward(request, response);
    }
}