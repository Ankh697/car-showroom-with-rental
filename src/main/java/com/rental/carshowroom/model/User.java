package com.rental.carshowroom.model;

import com.rental.carshowroom.model.enums.UserStatus;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractEntity {
    @NotBlank
    @Size(max = 20, message = "{msg.validation.user.username.size}")
    private String username;
    @NotBlank
    @Size(max = 100, message = "{msg.validation.user.nameandsurname.size}")
    private String nameAndSurname;
    @NotNull
    @Pattern(regexp = Patterns.PESEL, message = "{msg.validation.user.pesel.pattern}")
    private String pesel;
    @Enumerated(EnumType.STRING)
    @NotNull
    private UserStatus status = UserStatus.DISACTIVE;
}
