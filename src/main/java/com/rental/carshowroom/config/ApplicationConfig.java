package com.rental.carshowroom.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class ApplicationConfig {

    @Bean
    @Qualifier("myEmail")
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setProtocol("smtp");
        sender.setHost("smtp.gmail.com");
        sender.setPort(587);
        sender.setUsername("showroomjava@gmail.com");
        sender.setPassword("Qwerty123!");

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        sender.setJavaMailProperties(properties);

        return sender;
    }

}
