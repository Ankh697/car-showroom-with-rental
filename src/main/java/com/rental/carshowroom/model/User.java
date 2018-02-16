package com.rental.carshowroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rental.carshowroom.model.enums.UserStatus;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users",
        indexes = {@Index(name = "index_id", columnList = "id", unique = true), @Index(name = "index_username", columnList = "username")},
        uniqueConstraints = {@UniqueConstraint(columnNames = {"username"}, name = "username")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractEntity implements UserDetails {
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
    private UserStatus status = UserStatus.INACTIVE;
    @NotNull
    @Pattern(regexp = Patterns.EMAIL, message = "{msg.validation.user.email.pattern}")
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String password;
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleType().name())).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return status != UserStatus.BANNED && status != UserStatus.BANNED_TEMPORARY;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return status != UserStatus.INACTIVE;
    }
}
