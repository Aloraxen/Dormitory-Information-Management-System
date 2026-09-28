package com.cxt.dormitory.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生信息表 t_student
 */
@Entity
@Table(name = "t_student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联登录账户 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** 学号 */
    @Column(name = "student_no", nullable = false, unique = true, length = 30)
    private String studentNo;

    /** 姓名 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 性别：男/女 */
    @Column(length = 4)
    private String gender = "男";

    /** 学院 */
    @Column(length = 100)
    private String college;

    /** 专业 */
    @Column(length = 100)
    private String major;

    /** 班级 */
    @Column(name = "class_name", length = 50)
    private String className;

    /** 联系电话 */
    @Column(length = 20)
    private String phone;

    /** 电子邮箱 */
    @Column(length = 100)
    private String email;

    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    /** 住宿记录 */
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<StayRecord> stays = new ArrayList<StayRecord>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public List<StayRecord> getStays() {
        return stays;
    }

    public void setStays(List<StayRecord> stays) {
        this.stays = stays;
    }
}
