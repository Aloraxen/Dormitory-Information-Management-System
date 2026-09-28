package com.cxt.dormitory.repository;

import com.cxt.dormitory.entity.BackupRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BackupRepository extends JpaRepository<BackupRecord, Long> {

    List<BackupRecord> findAllByOrderByIdDesc();
}
