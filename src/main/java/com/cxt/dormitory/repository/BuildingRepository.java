package com.cxt.dormitory.repository;

import com.cxt.dormitory.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    List<Building> findAllByOrderByIdAsc();
}
