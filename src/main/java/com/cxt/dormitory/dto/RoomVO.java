package com.cxt.dormitory.dto;

import com.cxt.dormitory.entity.Room;

/**
 * 房间展示对象：房间 + 已入住床位数
 */
public class RoomVO {

    private Room room;
    private long occupied;

    public RoomVO() {
    }

    public RoomVO(Room room, long occupied) {
        this.room = room;
        this.occupied = occupied;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public long getOccupied() {
        return occupied;
    }

    public void setOccupied(long occupied) {
        this.occupied = occupied;
    }
}
