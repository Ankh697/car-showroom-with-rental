package com.rental.carshowroom.model;

import com.rental.carshowroom.model.enums.UserStatus;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users",
        indexes = {@Index(name = "index_id", columnList = "id", unique = true), @Index(name = "index_username", columnList = "username")},
        uniqueConstraints = {@UniqueConstraint(columnNames = {"username"}, name = "username")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    @Size(max = 20, message = "{msg.validation.user.username.size}")
    @Column(updatable = false)
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
