package com.example.rfid_server_test.entity;

import javax.persistence.*;

@Entity
@Table(name = "information")
public class User {

    @Id
    @Column(name = "student_id")
    private String studentId;
    @Column(name = "student_name")
    private String studentName;
    @Column(name = "student_card_id")
    private String studentCardId;

    public User(){
        this.studentId = "1907030405";
        this.studentName = "qiushibo";
        this.studentCardId = "8A10D91B";
    }
    public User(String cardId){
        this.studentId = "1907030405";
        this.studentName = "asdfjkl;";
        this.studentCardId = cardId;
    }
    public User(String studentId, String studentName, String studentCardId) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentCardId = studentCardId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentCardId() {
        return studentCardId;
    }

    public void setStudentCardId(String studentCardId) {
        this.studentCardId = studentCardId;
    }
}
