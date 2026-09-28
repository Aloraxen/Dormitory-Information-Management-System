package com.cxt.dormitory.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 数据备份记录表 t_backup
 */
@Entity
@Table(name = "t_backup")
public class BackupRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 备份文件名 */
    @Column(name = "file_name", nullable = false, length = 100)
    private String fileName;

    /** 备份文件路径 */
    @Column(name = "file_path", length = 255)
    private String filePath;

    /** 文件大小（字节） */
    @Column
    private Long size = 0L;

    /** 操作人 */
    @Column(length = 50)
    private String operator;

    /** 备份时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
