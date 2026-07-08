package model;

import java.util.List;

public class RoomManagement {
    private Room room;

    public RoomManagement() {
        this.room = new RoomDB();
    }

    public Room getRoom() { return this.room; }

    public void setRoom(Room room) { this.room = room; }

    public Room[] searchRooms(String keyword) {
        if (this.room instanceof RoomDB) {
            List<RoomDB> list = RoomDB.searchInDB(keyword);
            return list.toArray(new Room[0]);
        }
        return new Room[0];
    }

}