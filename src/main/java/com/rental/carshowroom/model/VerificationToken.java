package com.rental.carshowroom.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationToken {
    private static final int EXPIRATION = 1;

    @Id
    @GeneratedValue
    private Long id;
    private String token;
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    private LocalDateTime expirationDate;

    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expirationDate = calculateExpiryDate();
    }

    private LocalDateTime calculateExpiryDate() {
       return LocalDateTime.now().plusDays(EXPIRATION);
    }
}
