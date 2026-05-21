package com.hr.backend.domain.qna.entity;

import com.hr.backend.domain.course.entity.Course;
import com.hr.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "qna_questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_resolved", nullable = false)
    private boolean resolved = false;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<QnaAnswer> answers = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public QnaQuestion(Course course, User user, String title, String content) {
        this.course  = course;
        this.user    = user;
        this.title   = title;
        this.content = content;
    }

    public void resolve() {
        this.resolved  = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateContent(String title, String content) {
        this.title     = title;
        this.content   = content;
        this.updatedAt = LocalDateTime.now();
    }
}
