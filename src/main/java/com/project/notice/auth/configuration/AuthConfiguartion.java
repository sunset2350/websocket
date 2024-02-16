package com.project.notice.auth.configuration;


import com.project.notice.auth.util.HashUtil;
import com.project.notice.auth.util.JwtUtil;
import org.apache.tomcat.Jar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfiguartion {

    @Bean
    public HashUtil hashUtil() {
        return new HashUtil();
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }
}
