package com.hr.backend.domain.certificates.service;

import org.springframework.stereotype.Service;

import com.hr.backend.domain.certificates.repository.CertificatesRepository;
import com.hr.backend.domain.courses.repository.CoursesRepository;
import com.hr.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificatesService {
    private final CertificatesRepository certificatesRepository;
    private final UserRepository userRepository;
}
