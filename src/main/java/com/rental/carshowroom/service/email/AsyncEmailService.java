package com.rental.carshowroom.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Qualifier("async")
public class AsyncEmailService extends EmailService {
    @Autowired
    public AsyncEmailService(JavaMailSender mailSender) {
        super(mailSender);
    }

    @Override
    @Async
    public void sendEmail(String subject, String message, String... to) {
        super.sendEmail(subject, message, to);
    }
}