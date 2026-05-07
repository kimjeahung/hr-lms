package com.hr.backend.domain.enrollments.entity;

import java.util.Date;

import com.hr.backend.domain.courses.entity.Courses;
import com.hr.backend.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
public class Enrollments { //수강 및 이수 관리를 위한 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollment_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user_id; //수강 직원
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Courses course_id; // 수강 강의

    @Column(name = "employee_no", nullable = false, length = 20)
    private int progress; // 수강 진행률 (0~100)

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "started_at", nullable = true)
    private Date started_at; // 수강 시작일

    @Column(name = "completed_at", nullable = true)
    private Date completed_at; // 수강 완료일

}
