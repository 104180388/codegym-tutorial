package model;

import java.util.List;

public class NoteManagement {

    private Note note;

    public NoteManagement() {
        this.note = new NoteDB();
    }

    public NoteManagement(Note note) {
        this.note = note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public void addNote(String title, String content, int typeId) {
            NoteDB dbNote = new NoteDB(0, title, content, typeId);
            dbNote.save();
    }

    public void removeNote(int noteId) {
            NoteDB dbNote = new NoteDB();
            dbNote.setId(noteId);
            dbNote.delete();
    }

    public Note[] searchNotes(String keyword, String typeIdStr) {
        if (this.note == null) {
            return new Note[0];
        }

        if (this.note instanceof NoteDB) {
            List<NoteDB> dbList = NoteDB.searchInDB(keyword, typeIdStr); // Truyền thêm tham số ở đây
            return dbList.toArray(new Note[0]);
        }


        return new Note[0];
    }

    public boolean changeNoteStore(String storeType) {

        if ("DB".equalsIgnoreCase(storeType)) {
            this.note = new NoteDB();
            return true;
        }

        this.note = null;
        return false;
    }

    public Note getNote() {
        return this.note;
    }
}