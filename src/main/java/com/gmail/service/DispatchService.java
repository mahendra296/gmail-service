package com.gmail.service;

import com.gmail.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.gmail.service.google.GmailService;
import com.gmail.service.google.GmailServiceImpl;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.io.IOException;


@Service
public class DispatchService {
    private final GmailService gmailService = new GmailServiceImpl();

    @Autowired
    private AppConfig coffeeSpaceEnvConfig;

    @PostConstruct
    public void init() {
        gmailService.init(coffeeSpaceEnvConfig);
    }

    public void sendMail(String subject, String content, String recipientAddress) throws IOException, MessagingException {
        gmailService.sendMessage(recipientAddress, subject, content);
    }

    @Scheduled(fixedRate = 500)
    private void sendMailFromQueue() throws IOException {
        // sends two emails from queue every second
        gmailService.sendMailFromQueue();
    }

    @Scheduled(fixedRate = 1800000)
    private void refreshAccessToken() {
        gmailService.refreshAccessToken();
    }
}
