package com.academic.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    @Value("${jwt.secret:${JWT_SECRET:academic-jwt-secret-key-for-academic-warning-system-2026}}")
    private String secret;
    
    private long expiration = 7200000L;
    
    private long refreshExpiration = 86400000L;
}