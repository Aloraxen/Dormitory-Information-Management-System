package com.cxt.dormitory.repository;

import com.cxt.dormitory.entity.ApplyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplyRepository extends JpaRepository<ApplyRequest, Long> {

    @Query("select distinct a from ApplyRequest a join fetch a.student order by a.id desc")
    List<ApplyRequest> findAllWithStudent();

    @Query("select distinct a from ApplyRequest a join fetch a.student " +
            "where a.student.id = :studentId order by a.id desc")
    List<ApplyRequest> findByStudentIdWithStudent(@Param("studentId") Long studentId);

    long countByStatus(String status);
}
