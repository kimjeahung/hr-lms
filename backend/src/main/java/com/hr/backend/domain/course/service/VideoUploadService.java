package com.hr.backend.domain.course.service;

import com.hr.backend.admin.dto.CourseVideoResponse;
import com.hr.backend.domain.course.entity.CourseVideo;
import com.hr.backend.domain.course.entity.Lecture;
import com.hr.backend.domain.course.repository.CourseVideoRepository;
import com.hr.backend.domain.course.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoUploadService {

    private final CourseVideoRepository courseVideoRepository;
    private final LectureRepository     lectureRepository;

    @Value("${video.storage-path:/app/videos}")
    private String storagePath;

    @Value("${video.base-url:/api/user/videos}")
    private String baseUrl;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "video/mp4", "video/webm", "video/ogg", "video/quicktime"
    );

    // ──────────────────────────────────────────────────────────
    // 영상 파일 업로드 (관리자)
    // ──────────────────────────────────────────────────────────

    @Transactional
    public CourseVideoResponse upload(Long lectureId, MultipartFile file,
                                      String title, int sortOrder) throws IOException {
        validateFile(file);

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("단원을 찾을 수 없습니다."));

        // 저장 디렉토리: /app/videos/{lectureId}/
        Path dir = Paths.get(storagePath, String.valueOf(lectureId));
        Files.createDirectories(dir);

        // 고유 파일명
        String ext      = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;
        Path   dest     = dir.resolve(filename);

        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

        // videoUrl = 스트리밍 API 경로
        String videoUrl = baseUrl + "/" + lectureId + "/stream/" + filename;

        // 재생 시간은 클라이언트에서 전달받거나 0으로 저장 (ffprobe 없이)
        CourseVideo video = CourseVideo.builder()
                .lecture(lecture)
                .title(title != null ? title : file.getOriginalFilename())
                .videoUrl(videoUrl)
                .durationSec(0)   // 프론트에서 재생 후 별도 업데이트 가능
                .sortOrder(sortOrder)
                .build();

        return new CourseVideoResponse(courseVideoRepository.save(video));
    }

    // ──────────────────────────────────────────────────────────
    // 영상 스트리밍 (사용자)
    // ──────────────────────────────────────────────────────────

    public Resource stream(Long lectureId, String filename) {
        // 경로 탈출 방지
        if (filename.contains("..") || filename.contains("/")) {
            throw new IllegalArgumentException("잘못된 파일명입니다.");
        }
        Path filePath = Paths.get(storagePath, String.valueOf(lectureId), filename);
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("영상 파일을 찾을 수 없습니다.");
        }
        return new FileSystemResource(filePath);
    }

    public String detectContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".mp4"))  return "video/mp4";
        if (lower.endsWith(".webm")) return "video/webm";
        if (lower.endsWith(".ogv"))  return "video/ogg";
        if (lower.endsWith(".mov"))  return "video/quicktime";
        return "application/octet-stream";
    }

    // ──────────────────────────────────────────────────────────
    // private helpers
    // ──────────────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "지원하지 않는 파일 형식입니다. (mp4, webm, ogg, mov만 허용)");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "mp4";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
