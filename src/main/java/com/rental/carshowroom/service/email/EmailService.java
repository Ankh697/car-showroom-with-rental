package com.rental.carshowroom.service.email;

import com.rental.carshowroom.model.User;

public interface EmailService {
    void sendEmail(User user, String subject, String message);
}
