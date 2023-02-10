package com.gmail.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AppConfig {

    @Value("${gmail.cred.userEmail}")
    private String gmailUserEmail;

    @Value("${gmail.cred.clientId}")
    private String gmailClientId;

    @Value("${gmail.cred.clientSecret}")
    private String gmailClientSecret;

    @Value("${gmail.cred.accessToken}")
    private String gmailAccessToken;

    @Value("${gmail.cred.refreshToken}")
    private String gmailRefreshToken;
}
