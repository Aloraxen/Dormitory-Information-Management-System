package com.cxt.dormitory.service;

import com.cxt.dormitory.entity.Student;
import com.cxt.dormitory.entity.StayRecord;
import com.cxt.dormitory.repository.StudentRepository;
import com.cxt.dormitory.repository.StayRecordRepository;
import com.cxt.dormitory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StayRecordRepository stayRecordRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    /** 关键字搜索：学号 / 姓名 / 学院 */
    public List<Student> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String kw = keyword.trim();
        return studentRepository.findByStudentNoContainingOrNameContainingOrCollegeContaining(kw, kw, kw);
    }

    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return id == null ? null : studentRepository.findByIdWithUser(id);
    }

    @Transactional(readOnly = true)
    public Student findByUserId(Long userId) {
        return studentRepository.findByUserId(userId == null ? null : userId);
    }

    public List<StayRecord> findStaysOfStudent(Long studentId) {
        return stayRecordRepository.findByStudentIdWithDetails(studentId);
    }

    public Student save(Student form) {
        if (form.getStudentNo() == null || form.getStudentNo().trim().isEmpty()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        boolean duplicated = form.getId() == null
                ? studentRepository.existsByStudentNo(form.getStudentNo().trim())
                : studentRepository.existsByStudentNoAndIdNot(form.getStudentNo().trim(), form.getId());
        if (duplicated) {
            throw new IllegalArgumentException("学号已存在：" + form.getStudentNo());
        }
        form.setStudentNo(form.getStudentNo().trim());
        if (form.getUser() != null && form.getUser().getId() == null) {
            form.setUser(null);
        }
        return studentRepository.save(form);
    }

    @Transactional
    public void delete(Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return;
        }
        List<StayRecord> stays = stayRecordRepository.findByStudentIdOrderByIdDesc(id);
        for (StayRecord stay : stays) {
            stayRecordRepository.delete(stay);
        }
        studentRepository.delete(student);
    }
}
