package com.rental.carshowroom.model;

import com.rental.carshowroom.model.enums.UserStatus;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:validationmessages.properties")
public class UserTest {
    @Autowired
    private LocalValidatorFactoryBean validator;

    private User user;
    private final String PESEL = "12345678901";
    private Set<ConstraintViolation<User>> violations;


    private String notBlankMessage = "may not be empty";
    private String notNullMessage = "may not be null";
    @Value("${msg.validation.user.pesel.pattern}")
    private String wrongPeselPattern;
    @Value("${msg.validation.user.username.size}")
    private String usernameSize;
    @Value("${msg.validation.user.nameandsurname.size}")
    private String nameAndSurnameSize;

    @Before
    public void setup() {
        user = User.builder()
                .username(RandomStringUtils.randomAlphanumeric(5))
                .nameAndSurname(RandomStringUtils.randomAlphanumeric(5))
                .pesel(PESEL)
                .status(UserStatus.DISACTIVE)
                .build();
    }

    @Test
    public void positiveValidationTest() {
        violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void nullUsernameTest() {
        user.setUsername(null);
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(notBlankMessage, violations.iterator().next().getMessage());
    }

    @Test
    public void emptyUsernameTest() {
        user.setUsername("");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(notBlankMessage, violations.iterator().next().getMessage());
    }

    @Test
    public void blankUsernameTest() {
        user.setUsername("     ");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(notBlankMessage, violations.iterator().next().getMessage());
    }

    @Test
    public void tooLongUsernameTest() {
        user.setUsername(RandomStringUtils.randomAlphanumeric(25));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(usernameSize, violations.iterator().next().getMessage());
    }

    @Test
    public void nullNameAndSurnameTest() {
        user.setNameAndSurname(null);
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(notBlankMessage, violations.iterator().next().getMessage());
    }

    @Test
    public void emptyNameAndSurnameTest() {
        user.setNameAndSurname("");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(notBlankMessage, violations.iterator().next().getMessage());
    }

    @Test
    public void blankNameAndSurnameTest() {
        user.setNameAndSurname("     ");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(notBlankMessage, violations.iterator().next().getMessage());
    }

    @Test
    public void tooLongNameAndSurnameTest() {
        user.setNameAndSurname(RandomStringUtils.randomAlphanumeric(120));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(nameAndSurnameSize, violations.iterator().next().getMessage());
    }

    @Test
    public void wrongPeselPatternTest() {
        user.setPesel(RandomStringUtils.randomAlphabetic(11));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(wrongPeselPattern, violations.iterator().next().getMessage());
    }

    @Test
    public void wrongPeselSizeTest() {
        user.setPesel(RandomStringUtils.randomAlphanumeric(2));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(wrongPeselPattern, violations.iterator().next().getMessage());
    }

    @Test
    public void emptyPeselTest() {
        user.setPesel("");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(wrongPeselPattern, violations.iterator().next().getMessage());
    }

    @Test
    public void nullPeselTest() {
        user.setPesel(null);
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(notNullMessage, violations.iterator().next().getMessage());
    }

    @Test
    public void nullStatusTest() {
        user.setStatus(null);
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals(notNullMessage, violations.iterator().next().getMessage());
    }
}
