package com.rental.carshowroom.registration;

import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.listener.OnRegistrationCompleteEvent;
import com.rental.carshowroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@PropertySource("classpath:messages.properties")
@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    private final UserService service;

    private final JavaMailSender mailSender;

    @Value("${message.regSucc}")
    private String message;

    @Autowired
    public RegistrationListener(UserService service, JavaMailSender mailSender) {
        this.service = service;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationTokenForUser(user, token);
        String confirmationUrl
                = event.getAppUrl() + "/api/user/registrationConfirm.html?token=" + token;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Registration Confirmation");
        email.setText(message + confirmationUrl);
        mailSender.send(email);
    }
}
