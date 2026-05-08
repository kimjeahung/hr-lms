-- ============================================================
-- HR-LMS Database Schema v1.1
-- 변경: course_rounds, lecture_progress, quizzes, exams,
--       questions, choices, attempts 추가
-- 작성: 2026-05-07
-- ============================================================

-- ============================================================
-- 1. 부서 (departments)
-- ============================================================
CREATE TABLE departments (
    department_id   INT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 2. 직원 (users)
-- 사번 패턴: 현장직=A-NNNNN, 사무직=B-NNNNN
-- ============================================================
CREATE TABLE users (
    user_id         BIGINT       NOT NULL AUTO_INCREMENT,
    employee_no     VARCHAR(20)  NOT NULL,              -- 사번 (A-NNNNN / B-NNNNN)
    name            VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) NOT NULL,
    password        VARCHAR(255) NOT NULL,              -- BCrypt 해시
    department_id   INT          NOT NULL,
    position        VARCHAR(50)  NOT NULL,              -- 직급
    emp_type        TINYINT      NOT NULL DEFAULT 0,    -- 0=사무직, 1=현장직
    role            VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER',
    phone           VARCHAR(20)  NULL,
    hire_date       DATE         NOT NULL,
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_employee_no (employee_no),
    UNIQUE KEY uq_email (email),
    CONSTRAINT fk_users_department FOREIGN KEY (department_id) REFERENCES departments (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 3. 강좌 (courses)
-- deadline은 course_rounds로 이동
-- ============================================================
CREATE TABLE courses (
    course_id       BIGINT       NOT NULL AUTO_INCREMENT,
    title           VARCHAR(200) NOT NULL,
    description     TEXT         NULL,
    category        VARCHAR(100) NULL,
    target_role     TINYINT      NOT NULL DEFAULT 0,    -- 0=전체, 1=현장직, 2=사무직
    duration_min    INT          NULL,
    thumbnail_url   VARCHAR(500) NULL,
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (course_id),
    INDEX idx_courses_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 4. 차수 (course_rounds)
-- 같은 강좌를 여러 차수로 운영 (기한 내 이수 = 출석)
-- ============================================================
CREATE TABLE course_rounds (
    round_id        BIGINT       NOT NULL AUTO_INCREMENT,
    course_id       BIGINT       NOT NULL,
    round_no        INT          NOT NULL,              -- 차수 번호 (1, 2, 3...)
    start_date      DATE         NOT NULL,              -- 수강 시작일
    end_date        DATE         NOT NULL,              -- 수강 마감일
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (round_id),
    UNIQUE KEY uq_round (course_id, round_no),
    CONSTRAINT fk_round_course FOREIGN KEY (course_id) REFERENCES courses (course_id) ON DELETE CASCADE,
    INDEX idx_round_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 5. 강의 (lectures)  -- 강좌의 하위 단원
-- ============================================================
CREATE TABLE lectures (
    lecture_id      BIGINT       NOT NULL AUTO_INCREMENT,
    course_id       BIGINT       NOT NULL,
    title           VARCHAR(200) NOT NULL,
    description     TEXT         NULL,
    sort_order      INT          NOT NULL DEFAULT 0,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (lecture_id),
    CONSTRAINT fk_lecture_course FOREIGN KEY (course_id) REFERENCES courses (course_id) ON DELETE CASCADE,
    INDEX idx_lecture_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 6. 강의 영상 (course_videos)
-- ============================================================
CREATE TABLE course_videos (
    video_id        BIGINT       NOT NULL AUTO_INCREMENT,
    lecture_id      BIGINT       NOT NULL,
    title           VARCHAR(200) NOT NULL,
    video_url       VARCHAR(500) NOT NULL,
    duration_sec    INT          NOT NULL DEFAULT 0,
    sort_order      INT          NOT NULL DEFAULT 0,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (video_id),
    CONSTRAINT fk_videos_lecture FOREIGN KEY (lecture_id) REFERENCES lectures (lecture_id) ON DELETE CASCADE,
    INDEX idx_videos_lecture (lecture_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 7. 강의 진도 (lecture_progress)
-- 강의 단위 완료 여부 추적
-- ============================================================
CREATE TABLE lecture_progress (
    progress_id     BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    lecture_id      BIGINT       NOT NULL,
    is_completed    TINYINT(1)   NOT NULL DEFAULT 0,
    completed_at    DATETIME     NULL,
    PRIMARY KEY (progress_id),
    UNIQUE KEY uq_lecture_progress (user_id, lecture_id),
    CONSTRAINT fk_lp_user    FOREIGN KEY (user_id)    REFERENCES users    (user_id),
    CONSTRAINT fk_lp_lecture FOREIGN KEY (lecture_id) REFERENCES lectures (lecture_id) ON DELETE CASCADE,
    INDEX idx_lp_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 8. 퀴즈 (quizzes)  -- 강의마다 1개, 강의 완료 조건
-- ============================================================
CREATE TABLE quizzes (
    quiz_id         BIGINT       NOT NULL AUTO_INCREMENT,
    lecture_id      BIGINT       NOT NULL,
    title           VARCHAR(200) NOT NULL,
    pass_score      INT          NOT NULL DEFAULT 70,   -- 합격 점수 (100점 기준)
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (quiz_id),
    CONSTRAINT fk_quiz_lecture FOREIGN KEY (lecture_id) REFERENCES lectures (lecture_id) ON DELETE CASCADE,
    INDEX idx_quiz_lecture (lecture_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 9. 시험 (exams)  -- 강좌당 1개, 이수증 발급 조건
-- ============================================================
CREATE TABLE exams (
    exam_id         BIGINT       NOT NULL AUTO_INCREMENT,
    course_id       BIGINT       NOT NULL,
    title           VARCHAR(200) NOT NULL,
    pass_score      INT          NOT NULL DEFAULT 70,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (exam_id),
    CONSTRAINT fk_exam_course FOREIGN KEY (course_id) REFERENCES courses (course_id) ON DELETE CASCADE,
    INDEX idx_exam_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 10. 문항 (questions)  -- 퀴즈/시험 공용
-- quiz_id 또는 exam_id 중 하나만 값을 가짐
-- ============================================================
CREATE TABLE questions (
    question_id     BIGINT       NOT NULL AUTO_INCREMENT,
    quiz_id         BIGINT       NULL,
    exam_id         BIGINT       NULL,
    question_text   TEXT         NOT NULL,
    score           INT          NOT NULL DEFAULT 10,   -- 문항 배점
    sort_order      INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (question_id),
    CONSTRAINT fk_q_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (quiz_id) ON DELETE CASCADE,
    CONSTRAINT fk_q_exam FOREIGN KEY (exam_id) REFERENCES exams   (exam_id) ON DELETE CASCADE,
    INDEX idx_q_quiz (quiz_id),
    INDEX idx_q_exam (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 11. 선택지 (choices)  -- 퀴즈/시험 문항 공용
-- ============================================================
CREATE TABLE choices (
    choice_id       BIGINT       NOT NULL AUTO_INCREMENT,
    question_id     BIGINT       NOT NULL,
    choice_text     VARCHAR(300) NOT NULL,
    is_correct      TINYINT(1)   NOT NULL DEFAULT 0,    -- 정답 여부
    sort_order      INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (choice_id),
    CONSTRAINT fk_choice_question FOREIGN KEY (question_id) REFERENCES questions (question_id) ON DELETE CASCADE,
    INDEX idx_choice_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 12. 응시 결과 (attempts)  -- 퀴즈/시험 공용
-- quiz_id 또는 exam_id 중 하나만 값을 가짐
-- ============================================================
CREATE TABLE attempts (
    attempt_id      BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    quiz_id         BIGINT       NULL,
    exam_id         BIGINT       NULL,
    score           INT          NOT NULL DEFAULT 0,
    is_passed       TINYINT(1)   NOT NULL DEFAULT 0,
    attempted_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (attempt_id),
    CONSTRAINT fk_att_user FOREIGN KEY (user_id) REFERENCES users  (user_id),
    CONSTRAINT fk_att_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (quiz_id),
    CONSTRAINT fk_att_exam FOREIGN KEY (exam_id) REFERENCES exams   (exam_id),
    INDEX idx_att_user (user_id),
    INDEX idx_att_quiz (quiz_id),
    INDEX idx_att_exam (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 13. 수강 등록 (enrollments)
-- course_id → round_id 로 변경 (차수 기반 수강)
-- ============================================================
CREATE TABLE enrollments (
    enrollment_id   BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    round_id        BIGINT       NOT NULL,              -- 차수 ID (변경)
    approval_status VARCHAR(20)  NOT NULL DEFAULT 'PENDING', -- PENDING / APPROVED / REJECTED
    progress        INT          NOT NULL DEFAULT 0,
    status          VARCHAR(20)  NOT NULL DEFAULT 'NOT_STARTED', -- NOT_STARTED / IN_PROGRESS / DONE
    enrolled_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at     DATETIME     NULL,
    completed_at    DATETIME     NULL,
    PRIMARY KEY (enrollment_id),
    UNIQUE KEY uq_enrollment (user_id, round_id),
    CONSTRAINT fk_enrollment_user  FOREIGN KEY (user_id)  REFERENCES users         (user_id),
    CONSTRAINT fk_enrollment_round FOREIGN KEY (round_id) REFERENCES course_rounds (round_id),
    INDEX idx_enrollment_user  (user_id),
    INDEX idx_enrollment_round (round_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 14. 영상 시청 로그 (video_watch_logs)
-- 배속 감지: session_started_at vs session_ended_at
-- ============================================================
CREATE TABLE video_watch_logs (
    log_id              BIGINT       NOT NULL AUTO_INCREMENT,
    user_id             BIGINT       NOT NULL,
    video_id            BIGINT       NOT NULL,
    watched_sec         INT          NOT NULL DEFAULT 0,    -- 누적 시청 초
    is_completed        TINYINT(1)   NOT NULL DEFAULT 0,
    session_started_at  DATETIME     NULL,                  -- 시청 시작 시각 (배속 감지용)
    session_ended_at    DATETIME     NULL,                  -- 시청 종료 시각 (배속 감지용)
    last_watched_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (log_id),
    UNIQUE KEY uq_watch_log (user_id, video_id),
    CONSTRAINT fk_watch_user  FOREIGN KEY (user_id)  REFERENCES users         (user_id),
    CONSTRAINT fk_watch_video FOREIGN KEY (video_id) REFERENCES course_videos (video_id),
    INDEX idx_watch_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 15. 이수증 (certificates)
-- round_id 참조 (어느 차수에서 이수했는지 추적)
-- ============================================================
CREATE TABLE certificates (
    certificate_id  BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    round_id        BIGINT       NOT NULL,
    issued_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    file_url        VARCHAR(500) NULL,
    PRIMARY KEY (certificate_id),
    UNIQUE KEY uq_certificate (user_id, round_id),
    CONSTRAINT fk_cert_user  FOREIGN KEY (user_id)  REFERENCES users         (user_id),
    CONSTRAINT fk_cert_round FOREIGN KEY (round_id) REFERENCES course_rounds (round_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 16. 공지사항 (notices)
-- ============================================================
CREATE TABLE notices (
    notice_id       BIGINT       NOT NULL AUTO_INCREMENT,
    author_id       BIGINT       NOT NULL,
    title           VARCHAR(300) NOT NULL,
    content         TEXT         NOT NULL,
    view_count      INT          NOT NULL DEFAULT 0,
    is_pinned       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (notice_id),
    CONSTRAINT fk_notice_author FOREIGN KEY (author_id) REFERENCES users (user_id),
    INDEX idx_notice_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 17. 설문 (surveys)
-- ============================================================
CREATE TABLE surveys (
    survey_id       BIGINT       NOT NULL AUTO_INCREMENT,
    course_id       BIGINT       NOT NULL,
    title           VARCHAR(200) NOT NULL,
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (survey_id),
    CONSTRAINT fk_survey_course FOREIGN KEY (course_id) REFERENCES courses (course_id),
    INDEX idx_survey_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 18. 설문 문항 (survey_questions)
-- ============================================================
CREATE TABLE survey_questions (
    question_id     BIGINT       NOT NULL AUTO_INCREMENT,
    survey_id       BIGINT       NOT NULL,
    question_text   TEXT         NOT NULL,
    question_type   VARCHAR(20)  NOT NULL DEFAULT 'CHOICE',
    sort_order      INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (question_id),
    CONSTRAINT fk_sq_survey FOREIGN KEY (survey_id) REFERENCES surveys (survey_id) ON DELETE CASCADE,
    INDEX idx_sq_survey (survey_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 19. 설문 선택지 (survey_choices)
-- ============================================================
CREATE TABLE survey_choices (
    choice_id       BIGINT       NOT NULL AUTO_INCREMENT,
    question_id     BIGINT       NOT NULL,
    choice_text     VARCHAR(300) NOT NULL,
    sort_order      INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (choice_id),
    CONSTRAINT fk_sc_question FOREIGN KEY (question_id) REFERENCES survey_questions (question_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 20. 설문 응답 (survey_responses)
-- ============================================================
CREATE TABLE survey_responses (
    response_id     BIGINT       NOT NULL AUTO_INCREMENT,
    survey_id       BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    question_id     BIGINT       NOT NULL,
    choice_id       BIGINT       NULL,
    text_answer     TEXT         NULL,
    responded_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (response_id),
    CONSTRAINT fk_sr_survey   FOREIGN KEY (survey_id)   REFERENCES surveys         (survey_id),
    CONSTRAINT fk_sr_user     FOREIGN KEY (user_id)     REFERENCES users           (user_id),
    CONSTRAINT fk_sr_question FOREIGN KEY (question_id) REFERENCES survey_questions (question_id),
    CONSTRAINT fk_sr_choice   FOREIGN KEY (choice_id)   REFERENCES survey_choices  (choice_id),
    INDEX idx_sr_survey_user (survey_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 21. QnA 질문 (qna_questions)
-- ============================================================
CREATE TABLE qna_questions (
    question_id     BIGINT       NOT NULL AUTO_INCREMENT,
    course_id       BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    title           VARCHAR(300) NOT NULL,
    content         TEXT         NOT NULL,
    is_resolved     TINYINT(1)   NOT NULL DEFAULT 0,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (question_id),
    CONSTRAINT fk_qna_course FOREIGN KEY (course_id) REFERENCES courses (course_id),
    CONSTRAINT fk_qna_user   FOREIGN KEY (user_id)   REFERENCES users   (user_id),
    INDEX idx_qna_course (course_id),
    INDEX idx_qna_user   (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 22. QnA 답변 (qna_answers)
-- ============================================================
CREATE TABLE qna_answers (
    answer_id       BIGINT       NOT NULL AUTO_INCREMENT,
    question_id     BIGINT       NOT NULL,
    author_id       BIGINT       NOT NULL,
    content         TEXT         NOT NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (answer_id),
    CONSTRAINT fk_ans_question FOREIGN KEY (question_id) REFERENCES qna_questions (question_id) ON DELETE CASCADE,
    CONSTRAINT fk_ans_author   FOREIGN KEY (author_id)   REFERENCES users         (user_id),
    INDEX idx_ans_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 23. 알림 (notifications)
-- ============================================================
CREATE TABLE notifications (
    notification_id BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    type            VARCHAR(50)  NOT NULL,
    title           VARCHAR(300) NOT NULL,
    message         TEXT         NULL,
    is_read         TINYINT(1)   NOT NULL DEFAULT 0,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (notification_id),
    CONSTRAINT fk_noti_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    INDEX idx_noti_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 24. 관리자 감사 로그 (admin_audit_logs)
-- ============================================================
CREATE TABLE admin_audit_logs (
    log_id          BIGINT       NOT NULL AUTO_INCREMENT,
    admin_id        BIGINT       NOT NULL,
    action          VARCHAR(100) NOT NULL,
    target_type     VARCHAR(50)  NULL,
    target_id       BIGINT       NULL,
    detail          TEXT         NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (log_id),
    CONSTRAINT fk_audit_admin FOREIGN KEY (admin_id) REFERENCES users (user_id),
    INDEX idx_audit_admin   (admin_id),
    INDEX idx_audit_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
