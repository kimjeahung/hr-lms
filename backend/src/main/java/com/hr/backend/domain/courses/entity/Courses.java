package com.hr.backend.domain.courses.entity;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Courses { //강의를 관리 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "target_role", nullable = false, columnDefinition = "int default 0")
    private int target_role; // 0: 관리자, 1: 현장직, 2: 사무직

    @Column(name = "duration_min", nullable = false)
    private int duration_min = 0; // 교육 시간 (분)

    @Column(name = "deadline")
    private Date deadline; // 이수 마감일

    @Column(name = "is_active")
    private Boolean is_active;

    @Column(name = "create_at")
    private Date create_at;

}
