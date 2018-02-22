package com.rental.carshowroom.service.email;

import com.rental.carshowroom.model.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Qualifier("jms")
public class JmsEmailService extends EmailService {
    private JmsTemplate jmsTemplate;

    @Autowired
    public JmsEmailService(JmsTemplate jmsTemplate, JavaMailSender mailSender) {
        super(mailSender);
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void sendEmail(String subject, String message, String... to) {
        jmsTemplate.convertAndSend("EmailQueue", new EmailMessage(subject, message, to));
    }

    @JmsListener(destination = "EmailQueue", containerFactory = "factory")
    public void receiveMessage(EmailMessage email) {
        super.sendEmail(email.getSubject(), email.getMessage(), email.getTo());
    }
}
