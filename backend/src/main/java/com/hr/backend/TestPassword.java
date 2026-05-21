package com.hr.backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "1234";

        String encoded = encoder.encode(password);

        System.out.println("PASSWORD" + encoded);
    }
}