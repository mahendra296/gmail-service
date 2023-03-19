package com.gmail.service.google;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class GmailCredentials implements Serializable {
    private String userEmail;
    private String clientId;
    private String clientSecret;
    private String accessToken;
    private String refreshToken;
}
