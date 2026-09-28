package com.cxt.dormitory.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宿舍房型表 t_room_type
 */
@Entity
@Table(name = "t_room_type")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 房型名称，如：四人间 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 床位数 */
    @Column(name = "bed_count", nullable = false)
    private Integer bedCount = 4;

    /** 收费标准（元/学期） */
    @Column(precision = 8, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    /** 配置说明 */
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

    public Integer getBedCount() {
        return bedCount;
    }

    public void setBedCount(Integer bedCount) {
        this.bedCount = bedCount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
