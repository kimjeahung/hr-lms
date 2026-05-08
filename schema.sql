-- ============================================================
-- HR-LMS Database Schema v1.0
-- 기반: 팀원 초안 스키마 + 피드백 반영 수정본
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
-- ============================================================
CREATE TABLE users (
    user_id         BIGINT       NOT NULL AUTO_INCREMENT,
    employee_no     VARCHAR(20)  NOT NULL,              -- 사번
    name            VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) NOT NULL,
    password        VARCHAR(255) NOT NULL,              -- BCrypt 해시
    department_id   INT          NOT NULL,
    position        VARCHAR(50)  NOT NULL,              -- 직급 (사원/대리/과장/차장/부장)
    emp_type        TINYINT      NOT NULL DEFAULT 0,    -- 0=사무직, 1=현장직
    role            VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER',  -- ROLE_USER / ROLE_ADMIN
    phone           VARCHAR(20)  NULL,
    hire_date       DATE         NOT NULL,
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,    -- 재직 여부
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_employee_no (employee_no),
    UNIQUE KEY uq_email (email),
    CONSTRAINT fk_users_department FOREIGN KEY (department_id) REFERENCES departments (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 3. 강의 (courses)
-- ============================================================
CREATE TABLE courses (
    course_id       BIGINT       NOT NULL AUTO_INCREMENT,
    title           VARCHAR(200) NOT NULL,
    description     TEXT         NULL,
    category        VARCHAR(100) NULL,
    target_role     TINYINT      NOT NULL DEFAULT 0,    -- 0=전체, 1=현장직, 2=사무직
    duration_min    INT          NULL,                  -- 강의 총 시간(분)
    thumbnail_url   VARCHAR(500) NULL,
    deadline        DATE         NULL,
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,    -- 소프트 삭제용
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (course_id),
    INDEX idx_courses_category (category),
    INDEX idx_courses_deadline (deadline)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 4. 강의 영상 (course_videos)
-- ============================================================
CREATE TABLE course_videos (
    video_id        BIGINT       NOT NULL AUTO_INCREMENT,
    course_id       BIGINT       NOT NULL,
    title           VARCHAR(200) NOT NULL,
    video_url       VARCHAR(500) NOT NULL,
    duration_sec    INT          NOT NULL DEFAULT 0,    -- 영상 길이(초)
    sort_order      INT          NOT NULL DEFAULT 0,    -- 영상 순서
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (video_id),
    CONSTRAINT fk_videos_course FOREIGN KEY (course_id) REFERENCES courses (course_id) ON DELETE CASCADE,
    INDEX idx_videos_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 5. 수강 등록 (enrollments)
-- ============================================================
CREATE TABLE enrollments (
    enrollment_id   BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    course_id       BIGINT       NOT NULL,
    progress        INT          NOT NULL DEFAULT 0,     -- 진행률 0~100
    status          VARCHAR(20)  NOT NULL DEFAULT 'NOT_STARTED', -- NOT_STARTED / IN_PROGRESS / DONE
    enrolled_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at    DATETIME     NULL,                   -- 이수 완료 시각
    PRIMARY KEY (enrollment_id),
    UNIQUE KEY uq_enrollment (user_id, course_id),
    CONSTRAINT fk_enrollment_user   FOREIGN KEY (user_id)   REFERENCES users   (user_id),
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES courses  (course_id),
    INDEX idx_enrollment_user   (user_id),
    INDEX idx_enrollment_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 6. 영상 시청 로그 (video_watch_logs)
-- ============================================================
CREATE TABLE video_watch_logs (
    log_id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    video_id        BIGINT       NOT NULL,
    watched_sec     INT          NOT NULL DEFAULT 0,    -- 시청한 초 수
    is_completed    TINYINT(1)   NOT NULL DEFAULT 0,    -- 영상 완료 여부
    last_watched_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (log_id),
    UNIQUE KEY uq_watch_log (user_id, video_id),
    CONSTRAINT fk_watch_user  FOREIGN KEY (user_id)  REFERENCES users         (user_id),
    CONSTRAINT fk_watch_video FOREIGN KEY (video_id) REFERENCES course_videos (video_id),
    INDEX idx_watch_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 7. 이수증 (certificates)
-- ============================================================
CREATE TABLE certificates (
    certificate_id  BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    course_id       BIGINT       NOT NULL,
    issued_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    file_url        VARCHAR(500) NULL,                  -- 이수증 PDF 경로
    PRIMARY KEY (certificate_id),
    UNIQUE KEY uq_certificate (user_id, course_id),
    CONSTRAINT fk_cert_user   FOREIGN KEY (user_id)   REFERENCES users   (user_id),
    CONSTRAINT fk_cert_course FOREIGN KEY (course_id) REFERENCES courses (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 8. 공지사항 (notices)
-- ============================================================
CREATE TABLE notices (
    notice_id       BIGINT       NOT NULL AUTO_INCREMENT,
    author_id       BIGINT       NOT NULL,              -- 작성자 (관리자)
    title           VARCHAR(300) NOT NULL,
    content         TEXT         NOT NULL,
    view_count      INT          NOT NULL DEFAULT 0,
    is_pinned       TINYINT(1)   NOT NULL DEFAULT 0,    -- 상단 고정 여부
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (notice_id),
    CONSTRAINT fk_notice_author FOREIGN KEY (author_id) REFERENCES users (user_id),
    INDEX idx_notice_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 9. 설문 (surveys)
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
-- 10. 설문 문항 (survey_questions)
-- ============================================================
CREATE TABLE survey_questions (
    question_id     BIGINT       NOT NULL AUTO_INCREMENT,
    survey_id       BIGINT       NOT NULL,
    question_text   TEXT         NOT NULL,              -- 문항 내용 (수정됨: VARCHAR→TEXT)
    question_type   VARCHAR(20)  NOT NULL DEFAULT 'CHOICE', -- CHOICE / TEXT
    sort_order      INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (question_id),
    CONSTRAINT fk_question_survey FOREIGN KEY (survey_id) REFERENCES surveys (survey_id) ON DELETE CASCADE,
    INDEX idx_question_survey (survey_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 11. 설문 선택지 (survey_choices)
-- ============================================================
CREATE TABLE survey_choices (
    choice_id       BIGINT       NOT NULL AUTO_INCREMENT,
    question_id     BIGINT       NOT NULL,
    choice_text     VARCHAR(300) NOT NULL,
    sort_order      INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (choice_id),
    CONSTRAINT fk_choice_question FOREIGN KEY (question_id) REFERENCES survey_questions (question_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 12. 설문 응답 (survey_responses)
-- ============================================================
CREATE TABLE survey_responses (
    response_id     BIGINT       NOT NULL AUTO_INCREMENT,
    survey_id       BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    question_id     BIGINT       NOT NULL,
    choice_id       BIGINT       NULL,                  -- 객관식 선택 (CHOICE 유형)
    text_answer     TEXT         NULL,                  -- 주관식 답변 (TEXT 유형)
    responded_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (response_id),
    CONSTRAINT fk_resp_survey   FOREIGN KEY (survey_id)   REFERENCES surveys          (survey_id),
    CONSTRAINT fk_resp_user     FOREIGN KEY (user_id)     REFERENCES users            (user_id),
    CONSTRAINT fk_resp_question FOREIGN KEY (question_id) REFERENCES survey_questions (question_id),
    CONSTRAINT fk_resp_choice   FOREIGN KEY (choice_id)   REFERENCES survey_choices   (choice_id),
    INDEX idx_response_survey_user (survey_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 13. QnA 질문 (qna_questions)
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
-- 14. QnA 답변 (qna_answers)
-- 수정: author_id FK 추가, answered_at → created_at 통일
-- ============================================================
CREATE TABLE qna_answers (
    answer_id       BIGINT       NOT NULL AUTO_INCREMENT,
    question_id     BIGINT       NOT NULL,
    author_id       BIGINT       NOT NULL,              -- 답변 작성자 (관리자 또는 강사)
    content         TEXT         NOT NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (answer_id),
    CONSTRAINT fk_answer_question FOREIGN KEY (question_id) REFERENCES qna_questions (question_id) ON DELETE CASCADE,
    CONSTRAINT fk_answer_author   FOREIGN KEY (author_id)   REFERENCES users         (user_id),
    INDEX idx_answer_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 15. 알림 (notifications)  ← 신규 추가
-- ============================================================
CREATE TABLE notifications (
    notification_id BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    type            VARCHAR(50)  NOT NULL,              -- DEADLINE / ENROLLMENT / NOTICE / QNA 등
    title           VARCHAR(300) NOT NULL,
    message         TEXT         NULL,
    is_read         TINYINT(1)   NOT NULL DEFAULT 0,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (notification_id),
    CONSTRAINT fk_noti_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    INDEX idx_noti_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 16. 관리자 감사 로그 (admin_audit_logs)  ← 신규 추가
-- ============================================================
CREATE TABLE admin_audit_logs (
    log_id          BIGINT       NOT NULL AUTO_INCREMENT,
    admin_id        BIGINT       NOT NULL,              -- 작업한 관리자
    action          VARCHAR(100) NOT NULL,              -- CREATE_COURSE / DELETE_USER 등
    target_type     VARCHAR(50)  NULL,                  -- 대상 엔티티 타입
    target_id       BIGINT       NULL,                  -- 대상 엔티티 ID
    detail          TEXT         NULL,                  -- 상세 내용 (JSON 가능)
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (log_id),
    CONSTRAINT fk_audit_admin FOREIGN KEY (admin_id) REFERENCES users (user_id),
    INDEX idx_audit_admin    (admin_id),
    INDEX idx_audit_created  (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
