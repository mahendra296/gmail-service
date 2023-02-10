package com.gmail.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.gmail.service.DispatchService;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final DispatchService dispatchService;

    @GetMapping("/test/hello")
    public String hello() {
        return "Hello";
    }

    @GetMapping("/send/{email}")
    public String sendMail(@PathVariable("email") String email) throws MessagingException, IOException {
        dispatchService.sendMail("subject", "Content", email);
        return "mail sent successfully.";
    }
}
