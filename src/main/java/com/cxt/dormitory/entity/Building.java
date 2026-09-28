package com.cxt.dormitory.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 宿舍楼栋表 t_building
 */
@Entity
@Table(name = "t_building")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 楼栋名称，如：一号公寓 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 入住性别：男/女 */
    @Column(name = "gender_limit", length = 4)
    private String genderLimit = "男";

    /** 层数 */
    @Column
    private Integer floors = 6;

    /** 负责人 */
    @Column(length = 50)
    private String manager;

    /** 备注 */
    @Column(length = 255)
    private String remark;

    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenderLimit() {
        return genderLimit;
    }

    public void setGenderLimit(String genderLimit) {
        this.genderLimit = genderLimit;
    }

    public Integer getFloors() {
        return floors;
    }

    public void setFloors(Integer floors) {
        this.floors = floors;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
