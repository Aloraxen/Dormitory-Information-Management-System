package com.cxt.dormitory.dto;

/**
 * 床位信息（用于房间床位可视化展示）
 */
public class BedInfo {

    /** 床位号，如：1号床 */
    private String bedNo;

    /** 是否已入住 */
    private boolean occupied;

    /** 入住学生姓名 */
    private String studentName;

    /** 入住学生ID */
    private Long studentId;

    /** 住宿记录ID */
    private Long stayId;

    /** 入住日期 */
    private String checkInDate;

    public BedInfo() {
    }

    public BedInfo(String bedNo, boolean occupied, String studentName, Long studentId, Long stayId, String checkInDate) {
        this.bedNo = bedNo;
        this.occupied = occupied;
        this.studentName = studentName;
        this.studentId = studentId;
        this.stayId = stayId;
        this.checkInDate = checkInDate;
    }

    public String getBedNo() {
        return bedNo;
    }

    public void setBedNo(String bedNo) {
        this.bedNo = bedNo;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getStayId() {
        return stayId;
    }

    public void setStayId(Long stayId) {
        this.stayId = stayId;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }
}
