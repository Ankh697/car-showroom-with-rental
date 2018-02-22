package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.VerificationToken;
import com.rental.carshowroom.model.enums.UserStatus;
import com.rental.carshowroom.repository.VerificationTokenRepository;
import com.rental.carshowroom.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@PropertySource("classpath:messages.properties")
public class VerificationTokenService {

    private VerificationTokenRepository verificationTokenRepository;
    private EmailService emailService;

    @Value("${message.registration.subject}")
    private String subject;

    @Value("${message.registration.email}")
    private String message;

    @Autowired
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository, @Qualifier("jms") EmailService emailService) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
    }

    private void createVerificationTokenForUser(final User user, final String token) {
        verificationTokenRepository.save(new VerificationToken(token, user));
    }

    public void sendConfirmationEmail(User user, String appUrl) {
        String token = UUID.randomUUID().toString();
        createVerificationTokenForUser(user, token);
        emailService.sendEmail(subject, message + "\n" + appUrl + "/api/user/registration/confirm?token=" + token, user.getEmail());
    }

    private VerificationToken getVerificationToken(String verificationToken) {
        return verificationTokenRepository.findByToken(verificationToken).orElseThrow(() -> new NotFoundException(NotFoundExceptionCode.TOKEN_NOT_FOUND));
    }

    public User activateAccountWithToken(String token) {
        VerificationToken verificationToken = getVerificationToken(token);
        User user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
