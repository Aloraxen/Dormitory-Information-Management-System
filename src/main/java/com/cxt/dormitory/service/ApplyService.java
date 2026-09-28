package com.cxt.dormitory.service;

import com.cxt.dormitory.entity.ApplyRequest;
import com.cxt.dormitory.entity.Student;
import com.cxt.dormitory.repository.ApplyRepository;
import com.cxt.dormitory.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplyService {

    @Autowired
    private ApplyRepository applyRepository;

    @Autowired
    private StudentRepository studentRepository;

    /** 全部申请（供宿管/管理员处理） */
    public List<ApplyRequest> findAll() {
        return applyRepository.findAllWithStudent();
    }

    /** 某学生的申请 */
    public List<ApplyRequest> findByStudentId(Long studentId) {
        return applyRepository.findByStudentIdWithStudent(studentId);
    }

    @Transactional(readOnly = true)
    public ApplyRequest findById(Long id) {
        return id == null ? null : applyRepository.findById(id).orElse(null);
    }

    /** 学生提交申请 */
    public void submit(ApplyRequest form) {
        if (form.getStudent() == null || form.getStudent().getId() == null) {
            throw new IllegalArgumentException("申请信息不完整");
        }
        if (form.getType() == null || form.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("请选择申请类型");
        }
        if (form.getTitle() == null || form.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("请填写申请标题");
        }
        if (form.getContent() == null || form.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("请填写申请内容");
        }
        Student student = studentRepository.findById(form.getStudent().getId()).orElse(null);
        form.setStudent(student);
        form.setStatus(ApplyRequest.STATUS_PENDING);
        form.setReply(null);
        form.setHandleTime(null);
        applyRepository.save(form);
    }

    /** 宿管/管理员处理申请 */
    @Transactional
    public void handle(Long id, String status, String reply) {
        ApplyRequest apply = applyRepository.findById(id).orElse(null);
        if (apply == null) {
            throw new IllegalArgumentException("申请记录不存在");
        }
        if (ApplyRequest.STATUS_APPROVED.equals(status) || ApplyRequest.STATUS_REJECTED.equals(status)) {
            apply.setStatus(status);
            apply.setReply(reply == null || reply.trim().isEmpty() ? null : reply.trim());
            apply.setHandleTime(LocalDateTime.now());
            applyRepository.save(apply);
        }
    }

    public long countPending() {
        return applyRepository.countByStatus(ApplyRequest.STATUS_PENDING);
    }

    public void delete(Long id) {
        applyRepository.deleteById(id);
    }
}
