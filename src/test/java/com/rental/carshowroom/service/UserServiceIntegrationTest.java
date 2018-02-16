package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.model.Role;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.enums.UserStatus;
import com.rental.carshowroom.repository.UserRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private User user;
    private User tempUser;
    private final String PESEL = RandomStringUtils.randomNumeric(11);
    private final String email = "xxx@example.com";

    @Before
    public void setup() {
        user = User.builder()
                .username(RandomStringUtils.randomAlphanumeric(5))
                .nameAndSurname(RandomStringUtils.randomAlphanumeric(5))
                .pesel(PESEL)
                .roles(Role.userRoles())
                .email(email)
                .password(RandomStringUtils.randomAlphabetic(10))
                .status(UserStatus.ACTIVE)
                .build();
        tempUser = User.builder()
                .username(user.getUsername())
                .nameAndSurname(user.getNameAndSurname())
                .pesel(user.getPesel())
                .roles(Role.userRoles())
                .email(user.getEmail())
                .status(user.getStatus())
                .password(user.getPassword())
                .build();
    }

    @Test
    public void addUserTest() {
        userService.addUser(user);
        assertEquals(tempUser.getUsername(), user.getUsername());
        assertTrue(bCryptPasswordEncoder.matches(tempUser.getPassword(), user.getPassword()));
    }

    @Test
    public void getUser_PositiveTest() {
        userRepository.save(user);
        assertEquals(user, userService.getUser(user.getId()));
    }

    @Test(expected = NotFoundException.class)
    public void getUser_NotFound_Test() {
        userService.getUser(1L);
    }

    @Test
    public void updateUser_PositiveTest() {
        userRepository.save(user);
        tempUser.setNameAndSurname(RandomStringUtils.randomAlphabetic(10));
        User updatedUser = userService.updateUser(tempUser, user.getId());
        assertEquals(tempUser.getUsername(), updatedUser.getUsername());
        assertEquals(tempUser.getNameAndSurname(), updatedUser.getNameAndSurname());
    }

    @Test
    public void updateUser_ChangeUsername_Test() {
        userRepository.save(user);
        tempUser.setUsername(RandomStringUtils.randomAlphabetic(10));
        tempUser.setNameAndSurname(RandomStringUtils.randomAlphabetic(10));
        User updatedUser = userService.updateUser(tempUser, user.getId());
        assertEquals(user.getUsername(), updatedUser.getUsername());
        assertEquals(tempUser.getNameAndSurname(), updatedUser.getNameAndSurname());
    }

    @Test(expected = NotFoundException.class)
    public void deleteUser_PositiveTest() {
        userRepository.save(user);
        userService.deleteUser(user.getId());
        userService.getUser(user.getId());
    }

    @Test
    public void listAllTest() {
        userRepository.save(user);
        userRepository.save(User.builder()
                .username(RandomStringUtils.randomAlphanumeric(5))
                .nameAndSurname(RandomStringUtils.randomAlphanumeric(5))
                .pesel(PESEL)
                .status(UserStatus.INACTIVE)
                .build());
        userRepository.save(User.builder()
                .username(RandomStringUtils.randomAlphanumeric(5))
                .nameAndSurname(RandomStringUtils.randomAlphanumeric(5))
                .pesel(PESEL)
                .status(UserStatus.INACTIVE)
                .build());
        List<User> users = userService.listAllUsers();
        assertEquals(3, users.size());
    }

    @Test
    public void listAll_EmptyTest() {
        List<User> users = userService.listAllUsers();
        assertTrue(users.isEmpty());
    }
}
