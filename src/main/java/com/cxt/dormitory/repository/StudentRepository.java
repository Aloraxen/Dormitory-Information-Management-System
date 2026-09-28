package com.cxt.dormitory.repository;

import com.cxt.dormitory.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("select distinct s from Student s left join fetch s.user where s.id = :id")
    Student findByIdWithUser(@Param("id") Long id);

    boolean existsByStudentNo(String studentNo);

    boolean existsByStudentNoAndIdNot(String studentNo, Long id);

    Student findByUserId(Long userId);

    List<Student> findByStudentNoContainingOrNameContainingOrCollegeContaining(String studentNo, String name, String college);
}
