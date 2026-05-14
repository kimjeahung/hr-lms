package com.hr.backend.domain.qna.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "qna_questions")
public class QnaQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    private Long courseId;
    private Long userId;
    private String title;
    private String content;
    private boolean isResolved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}