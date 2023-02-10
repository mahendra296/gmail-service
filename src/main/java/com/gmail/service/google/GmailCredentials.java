package com.gmail.service.google;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class GmailCredentials implements Serializable {
    private final String userEmail;
    private final String clientId;
    private final String clientSecret;
    private final String accessToken;
    private final String refreshToken;
}
