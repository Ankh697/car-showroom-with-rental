package com.rental.carshowroom.model;

import com.google.common.collect.Sets;
import com.rental.carshowroom.model.enums.RoleType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "roles")
@NoArgsConstructor
public class Role extends AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    public Role(RoleType roleType) {
        this.roleType = roleType;
    }

    public static Set<Role> adminRoles() {
        return Sets.newHashSet(admin(), user());
    }

    public static Set<Role> userRoles() {
        return Sets.newHashSet(user());
    }

    public static Role admin() {
        return new Role(RoleType.ROLE_ADMIN);
    }

    public static Role user() {
        return new Role(RoleType.ROLE_USER);
    }
}
