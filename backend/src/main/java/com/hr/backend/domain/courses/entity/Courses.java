package com.hr.backend.domain.courses.entity;

import java.util.Date;

import com.hr.backend.domain.courses.CoursesEnum;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Courses { //강의를 관리 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long course_id; //강좌 고유ID

    @Column(name = "title", nullable = false, length = 100)
    private String title; //강좌명

    @Column(name = "description", length = 500) //TEXT
    private String description; //강좌 설명

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50)//enum으로 관리
    private CoursesEnum category; 

    @Column(name = "target_role", nullable = false, columnDefinition = "int default 0") 
    private int target_role; // 0: 관리자, 1: 현장직, 2: 사무직

    @Column(name = "duration_min", nullable = false)
    private int duration_min = 0; // 교육 시간 (분)

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnail_url; // 썸네일 이미지 URL
    
    @Column(name = "deadline")
    private Date deadline; // 이수 마감일

    @Column(name = "is_active") //운영여부
    private Boolean is_active;

    @Column(name = "create_at") //등록일시
    private Date create_at;

}
