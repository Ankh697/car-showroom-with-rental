package com.rental.carshowroom.service;

import com.rental.carshowroom.CarShowroomApplication;
import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.enums.UserStatus;
import com.rental.carshowroom.repository.UserRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = {CarShowroomApplication.class})
public class UserServiceIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    private User user;
    private final String PESEL = "12345678901";

    @Before
    public void setup() {
        user = User.builder()
                .username(RandomStringUtils.randomAlphanumeric(5))
                .nameAndSurname(RandomStringUtils.randomAlphanumeric(5))
                .pesel(PESEL)
                .status(UserStatus.INACTIVE)
                .build();
    }

    @Test
    public void addUserTest() {
        User addedUser = userService.addUser(user);
        assertEquals(addedUser, userRepository.findOne(addedUser.getId()));
    }

    @Test
    public void getUser_PositiveTest() {
        User addedUser = userRepository.save(user);
        assertEquals(addedUser, userService.getUser(addedUser.getId()));
    }

    @Test(expected = NotFoundException.class)
    public void getUser_NotFound_Test() {
        userService.getUser(1L);
    }

    @Test
    public void updateUser_PositiveTest() {
        User addedUser = userRepository.save(user);
        assertEquals(addedUser, userRepository.findOne(addedUser.getId()));
        user.setNameAndSurname("xxx");
        addedUser.setNameAndSurname("xxx");
        User updatedUser = userService.updateUser(user, addedUser.getId());
        assertEquals(addedUser, userRepository.findOne(updatedUser.getId()));
        assertEquals("xxx", updatedUser.getNameAndSurname());
    }

    @Test(expected = NotFoundException.class)
    public void deleteUser_PositiveTest() {
        User addedUser = userRepository.save(user);
        assertEquals(addedUser, userRepository.findOne(addedUser.getId()));
        userService.deleteUser(addedUser.getId());
        userService.getUser(addedUser.getId());
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
    public void listAla_EmptyTest() {
        List<User> users = userService.listAllUsers();
        assertTrue(users.isEmpty());
    }
}
