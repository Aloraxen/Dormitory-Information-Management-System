package com.cxt.dormitory.repository;

import com.cxt.dormitory.entity.StayRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StayRecordRepository extends JpaRepository<StayRecord, Long> {

    @Query("select distinct s from StayRecord s join fetch s.student join fetch s.room r " +
            "join fetch r.building join fetch r.roomType order by s.id desc")
    List<StayRecord> findAllWithDetails();

    @Query("select distinct s from StayRecord s join fetch s.room r join fetch r.building " +
            "join fetch r.roomType where s.student.id = :studentId order by s.id desc")
    List<StayRecord> findByStudentIdWithDetails(@Param("studentId") Long studentId);

    @Query("select distinct s from StayRecord s join fetch s.student " +
            "where s.room.id = :roomId and s.status = '在住' order by s.id asc")
    List<StayRecord> findActiveByRoomWithStudent(@Param("roomId") Long roomId);

    @Query("select distinct s from StayRecord s join fetch s.student st join fetch s.room r " +
            "join fetch r.building join fetch r.roomType where s.id = :id")
    StayRecord findByIdWithDetails(@Param("id") Long id);

    @Query("select distinct s from StayRecord s where s.room.id = :roomId")
    List<StayRecord> findByRoomIdPlain(@Param("roomId") Long roomId);

    List<StayRecord> findByStudentIdOrderByIdDesc(Long studentId);

    StayRecord findByStudentIdAndStatus(Long studentId, String status);

    boolean existsByRoomIdAndBedNoAndStatus(Long roomId, String bedNo, String status);

    boolean existsByRoomIdAndBedNoAndStatusAndIdNot(Long roomId, String bedNo, String status, Long id);

    long countByStatus(String status);
}
