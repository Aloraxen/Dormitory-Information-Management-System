package com.cxt.dormitory.service;

import com.cxt.dormitory.entity.Room;
import com.cxt.dormitory.entity.StayRecord;
import com.cxt.dormitory.entity.Student;
import com.cxt.dormitory.repository.RoomRepository;
import com.cxt.dormitory.repository.StayRecordRepository;
import com.cxt.dormitory.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class StayService {

    @Autowired
    private StayRecordRepository stayRecordRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRepository roomRepository;

    /** 全部住宿记录（含学生、房间、楼栋、房型，供列表展示） */
    public List<StayRecord> findAll() {
        return stayRecordRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public StayRecord findById(Long id) {
        return id == null ? null : stayRecordRepository.findByIdWithDetails(id);
    }

    /** 分配床位（入住登记） */
    public void assign(StayRecord form) {
        if (form.getStudent() == null || form.getStudent().getId() == null) {
            throw new IllegalArgumentException("请选择要安排住宿的学生");
        }
        if (form.getRoom() == null || form.getRoom().getId() == null) {
            throw new IllegalArgumentException("请选择房间");
        }
        if (form.getBedNo() == null || form.getBedNo().trim().isEmpty()) {
            throw new IllegalArgumentException("请选择床位");
        }
        Long roomId = form.getRoom().getId();
        String bedNo = form.getBedNo().trim();
        if (stayRecordRepository.existsByRoomIdAndBedNoAndStatus(roomId, bedNo, "在住")) {
            throw new IllegalArgumentException("该床位已有人入住，请重新选择");
        }
        Room room = roomRepository.findById(roomId).orElse(null);
        Student student = studentRepository.findById(form.getStudent().getId()).orElse(null);
        form.setRoom(room);
        form.setStudent(student);
        form.setBedNo(bedNo);
        if (form.getCheckInDate() == null) {
            form.setCheckInDate(LocalDate.now());
        }
        form.setStatus("在住");
        form.setCheckOutDate(null);
        stayRecordRepository.save(form);
    }

    /** 调整住宿安排（换房/换床） */
    public void update(StayRecord form) {
        if (form.getId() == null) {
            throw new IllegalArgumentException("记录不存在");
        }
        if (form.getRoom() == null || form.getRoom().getId() == null) {
            throw new IllegalArgumentException("请选择房间");
        }
        if (form.getBedNo() == null || form.getBedNo().trim().isEmpty()) {
            throw new IllegalArgumentException("请选择床位");
        }
        Long roomId = form.getRoom().getId();
        String bedNo = form.getBedNo().trim();
        if (stayRecordRepository.existsByRoomIdAndBedNoAndStatusAndIdNot(roomId, bedNo, "在住", form.getId())) {
            throw new IllegalArgumentException("该床位已有人入住，请重新选择");
        }
        Room room = roomRepository.findById(roomId).orElse(null);
        form.setRoom(room);
        form.setBedNo(bedNo);
        stayRecordRepository.save(form);
    }

    /** 办理退宿 */
    @Transactional
    public void checkout(Long id) {
        StayRecord stay = stayRecordRepository.findById(id).orElse(null);
        if (stay == null) {
            throw new IllegalArgumentException("住宿记录不存在");
        }
        if ("已退宿".equals(stay.getStatus())) {
            throw new IllegalArgumentException("该学生已退宿");
        }
        stay.setStatus("已退宿");
        stay.setCheckOutDate(LocalDate.now());
        stayRecordRepository.save(stay);
    }

    public void delete(Long id) {
        stayRecordRepository.deleteById(id);
    }

    public long countActive() {
        return stayRecordRepository.countByStatus("在住");
    }
}
