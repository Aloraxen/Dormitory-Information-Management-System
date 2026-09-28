package com.cxt.dormitory.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 学生申请表 t_apply：宿舍类型申请 / 调宿申请 / 宿舍报修
 */
@Entity
@Table(name = "t_apply")
public class ApplyRequest {

    public static final String TYPE_ROOM_TYPE = "宿舍类型申请";
    public static final String TYPE_TRANSFER = "调宿申请";
    public static final String TYPE_REPAIR = "宿舍报修";

    public static final String STATUS_PENDING = "待处理";
    public static final String STATUS_APPROVED = "已通过";
    public static final String STATUS_REJECTED = "已驳回";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 申请人 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** 申请类型 */
    @Column(nullable = false, length = 30)
    private String type;

    /** 标题 */
    @Column(length = 100)
    private String title;

    /** 申请内容 */
    @Column(length = 2000)
    private String content;

    /** 状态：待处理/已通过/已驳回 */
    @Column(length = 10)
    private String status = STATUS_PENDING;

    /** 处理回复 */
    @Column(length = 255)
    private String reply;

    /** 申请时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    /** 处理时间 */
    @Column(name = "handle_time")
    private LocalDateTime handleTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(LocalDateTime handleTime) {
        this.handleTime = handleTime;
    }
}
