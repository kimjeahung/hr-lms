package com.hr.backend.domain.certificates.entity;

import java.util.Date;

import com.hr.backend.domain.courses.entity.CourseRounds;
import com.hr.backend.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "certificates")
public class Certificates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificate_id; //자격증 고유ID

    private User user; //자격증과 연관된 사용자 엔티티

    private CourseRounds courseRound; //자격증과 연관된 강의 회차 엔티티

    private Date issue_date; //자격증 발급일
    
    @Column(name = "file_url", length = 500)
    private String file_url;
}
