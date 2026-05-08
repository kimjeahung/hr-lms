package com.hr.backend.domain.courses.entity;
import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_rounds")
@Getter
@Setter
public class CourseRounds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long round_id; //강의 회차 고유ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Courses course; //강의와 연관된 엔티티

    private int round_no; //회차 번호

    private Date start_date; //회차 시작일

    private Date end_date; //회차 종료일
    
    private Date create_at; //등록일시
}
