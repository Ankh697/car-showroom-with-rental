package com.rental.carshowroom.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailMessage {
    String[] to;
    String subject;
    String message;

    public EmailMessage(String subject, String message, String... to) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }
}
