package com.gmail.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.gmail.config.AppConfig;
import org.apache.commons.codec.binary.Base64;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

public final class GmailServiceImpl implements GmailService {

    private static final String APPLICATION_NAME = "Test Application";

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private HttpTransport httpTransport;
    private GmailCredentials gmailCredentials;

    private final Queue<Message> emailQueue = new LinkedList<>();

    public GmailServiceImpl(HttpTransport httpTransport) {
        this.httpTransport = httpTransport;
    }

    @Override
    public void init(AppConfig coffeeSpaceEnvConfig) {
        try {
            GmailCredentials cred = GmailCredentials.builder()
                    .userEmail(coffeeSpaceEnvConfig.getGmailUserEmail())
                    .clientId(coffeeSpaceEnvConfig.getGmailClientId())
                    .clientSecret(coffeeSpaceEnvConfig.getGmailClientSecret())
                    .accessToken(coffeeSpaceEnvConfig.getGmailAccessToken())
                    .refreshToken(coffeeSpaceEnvConfig.getGmailRefreshToken())
                    .build();

            setGmailCredentials(cred);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GmailServiceImpl() {
        try {
            this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setGmailCredentials(GmailCredentials gmailCredentials) {
        this.gmailCredentials = gmailCredentials;
    }

    @Override
    public void sendMessage(String recipientAddress, String subject, String body) throws MessagingException, IOException {
        Message message = createMessageWithEmail(
                createEmail(recipientAddress, gmailCredentials.getUserEmail(), subject, body));

        emailQueue.add(message);
    }

    @Override
    public void sendMailFromQueue() throws IOException {
        if (!emailQueue.isEmpty()) {
            Message message = emailQueue.remove();
            createGmail().users()
                    .messages()
                    .send(gmailCredentials.getUserEmail(), message)
                    .execute()
                    .getLabelIds().contains("SENT");
        }
    }

    private Gmail createGmail() {
        Credential credential = authorize();
        return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    private MimeMessage createEmail(String to, String from, String subject, String messageBodyHtml) throws MessagingException, UnsupportedEncodingException {
        MimeMessage email = new MimeMessage(Session.getDefaultInstance(new Properties(), null));
        email.setFrom(new InternetAddress(from, APPLICATION_NAME));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject, "utf-8");
        //wrapper
        final MimeBodyPart wrap = new MimeBodyPart();

        //Text
        MimeMultipart cover = new MimeMultipart("alternative");

        //HTML
        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(messageBodyHtml, "text/html; charset=utf-8");
        htmlPart.setDisposition(Part.INLINE);
        cover.addBodyPart(htmlPart);

        wrap.setContent(cover);

        MimeMultipart content = new MimeMultipart("related");
        email.setContent(content);
        content.addBodyPart(wrap);
        return email;
    }

    private Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);

        return new Message()
                .setRaw(Base64.encodeBase64URLSafeString(buffer.toByteArray()));
    }

    private Credential authorize() {
        return new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(gmailCredentials.getClientId(), gmailCredentials.getClientSecret())
                .build()
                .setAccessToken(gmailCredentials.getAccessToken())
                .setRefreshToken(gmailCredentials.getRefreshToken());
    }

}