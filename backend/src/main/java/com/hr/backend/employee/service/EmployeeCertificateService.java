package com.hr.backend.employee.service;

import com.hr.backend.domain.enrollment.entity.Certificate;
import com.hr.backend.domain.enrollment.repository.CertificateRepository;
import com.hr.backend.domain.user.entity.User;
import com.hr.backend.domain.user.repository.UserRepository;
import com.hr.backend.employee.dto.response.CertificateResponse;
import com.hr.backend.employee.exception.ResourceNotFoundException;
import com.hr.backend.employee.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeCertificateService {
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    public List<CertificateResponse> getMyCertificates() { User u=getCurrentUser(); return certificateRepository.findAll().stream().filter(c -> c.getUser().getUserId().equals(u.getUserId())).map(this::toResponse).toList(); }
    public CertificateResponse getCertificateDetail(Long certificateId) { Certificate c=certificateRepository.findById(certificateId).orElseThrow(() -> new ResourceNotFoundException("Certificate", "certificateId", certificateId)); return toResponse(c); }
    private CertificateResponse toResponse(Certificate c){return CertificateResponse.builder().certificateId(c.getCertificateId()).courseId(c.getRound().getCourse().getCourseId()).courseTitle(c.getRound().getCourse().getTitle()).issuedAt(c.getIssuedAt()).fileURL(c.getFileUrl()).build();}
    private User getCurrentUser(){Long id=currentUserProvider.getCurrentUserId();return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "userId", id));}
}
