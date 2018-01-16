package com.rental.carshowroom.model;

import com.rental.carshowroom.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.context.annotation.PropertySource;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@PropertySource("classpath:validation_messages.properties")
public class User extends AbstractEntity {
    @NotNull
    @NotBlank
    private String username;
    @NotNull
    @NotBlank
    private String nameAndSurname;
    @NotNull
    @NotBlank
    @Pattern(regexp = Patterns.PESEL, message = "{msg.validation.user.pesel.pattern}")
    private String pesel;
    @Enumerated(EnumType.STRING)
    @NotNull
    private UserStatus status;
}
