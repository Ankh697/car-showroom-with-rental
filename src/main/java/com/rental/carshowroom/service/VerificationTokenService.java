package com.rental.carshowroom.service;

import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.VerificationToken;
import com.rental.carshowroom.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@PropertySource("classpath:messages.properties")
public class VerificationTokenService {

    private VerificationTokenRepository verificationTokenRepository;
    private EmailService emailServiceJavaMailSender;

    @Value("${message.registrationsubject}")
    private String subject;

    @Value("${message.registrationemail}")
    private String message;

    @Autowired
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository, EmailService emailServiceJavaMailSender) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailServiceJavaMailSender = emailServiceJavaMailSender;
    }

    private void createVerificationTokenForUser(final User user, final String token) {
        verificationTokenRepository.save(new VerificationToken(token, user));
    }

    public void sendConfirmationEmail(User user, String appUrl) {
        String token = UUID.randomUUID().toString();
        createVerificationTokenForUser(user, token);
        StringBuilder messageBuilder = new StringBuilder(message).append("\n").append(appUrl).append("/api/user/registration/confirm?token=").append(token);
        emailServiceJavaMailSender.sendEmail(user, subject, messageBuilder.toString());
    }

    public VerificationToken getVerificationToken(String verificationToken) {
        return verificationTokenRepository.findByToken(verificationToken);
    }
}
