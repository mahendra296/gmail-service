package com.gmail.service.google;

import com.gmail.config.AppConfig;

import javax.mail.MessagingException;
import java.io.IOException;

public interface GmailService {
    void setGmailCredentials(GmailCredentials gmailCredentials);

    void sendMessage(String recipientAddress, String subject, String body) throws MessagingException, IOException;

    void init(AppConfig coffeeSpaceEnvConfig);

    void sendMailFromQueue() throws IOException;
}