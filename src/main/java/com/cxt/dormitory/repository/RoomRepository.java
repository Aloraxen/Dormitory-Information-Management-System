package com.cxt.dormitory.repository;

import com.cxt.dormitory.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("select distinct r from Room r join fetch r.building join fetch r.roomType " +
            "where r.building.id = :buildingId order by r.floor asc, r.roomNo asc")
    List<Room> findByBuildingIdWithDetails(@Param("buildingId") Long buildingId);

    @Query("select distinct r from Room r join fetch r.building join fetch r.roomType where r.id = :id")
    Room findByIdWithDetails(@Param("id") Long id);

    @Query("select distinct r from Room r join fetch r.building join fetch r.roomType " +
            "order by r.building.id asc, r.floor asc, r.roomNo asc")
    List<Room> findAllWithDetails();

    boolean existsByBuildingIdAndRoomNo(Long buildingId, String roomNo);

    boolean existsByBuildingIdAndRoomNoAndIdNot(Long buildingId, String roomNo, Long id);
}
