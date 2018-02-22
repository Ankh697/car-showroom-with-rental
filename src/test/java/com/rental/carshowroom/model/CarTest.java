package com.rental.carshowroom.model;

import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.Petrol;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
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
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:validationmessages.properties")
public class CarTest {

    @Autowired
    private LocalValidatorFactoryBean validator;

    private Car car;
    private Set<ConstraintViolation<Car>> violations;

    private String notBlankMessage = "may not be empty";
    private String notNullMessage = "may not be null";

    @Value("${msg.validation.car.vin.pattern}")
    private String wrongVINPattern;
    @Value("${msg.validation.user.username.size}")
    private String usernameSize;
    @Value("${msg.validation.user.nameandsurname.size}")
    private String nameAndSurnameSize;
    @Value("${msg.validation.user.email.pattern}")
    private String emailPattern;

    @Before
    public void setup() {
        car = Car.builder()
                .model(RandomStringUtils.randomAlphabetic(5))
                .numberPlate(RandomStringUtils.randomAlphabetic(10))
                .engineCapacity(RandomUtils.nextDouble())
                .enginePower(RandomUtils.nextLong())
                .status(CarStatus.FOR_RENT)
                .petrol(Petrol.DIESEL)
                .color("black")
                .vin("2AYNC6JM673S14892")
                .rentCostPerDay(BigDecimal.ONE)
                .build();
    }

    @Test
    public void positiveValidationTest() {
        violations = validator.validate(car);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void nullModelTest() {
        car.setModel(null);
        violations = validator.validate(car);
        assertEquals(1, violations.size());
        assertEquals(notBlankMessage, violations.iterator().next().getMessage());
    }

    @Test
    public void emptyModelTest() {
        car.setModel("");
        violations = validator.validate(car);
        assertEquals(1, violations.size());
        assertEquals(notBlankMessage, violations.iterator().next().getMessage());
    }

    @Test
    public void blankModelTest() {
        car.setModel("     ");
        violations = validator.validate(car);
        assertEquals(1, violations.size());
        assertEquals(notBlankMessage, violations.iterator().next().getMessage());
    }

    @Test
    public void wrongVinPatternTest() {
        car.setVin(RandomStringUtils.randomAlphanumeric(11));
        violations = validator.validate(car);
        assertEquals(1, violations.size());
        assertEquals(wrongVINPattern, violations.iterator().next().getMessage());
    }

    @Test
    public void wrongVinSizeTest() {
        car.setVin(RandomStringUtils.randomAlphanumeric(18));
        violations = validator.validate(car);
        assertEquals(1, violations.size());
        assertEquals(wrongVINPattern, violations.iterator().next().getMessage());
    }

    @Test
    public void emptyVinTest() {
        car.setVin("");
        violations = validator.validate(car);
        assertEquals(1, violations.size());
        assertEquals(wrongVINPattern, violations.iterator().next().getMessage());
    }

    @Test
    public void nullVinTest() {
        car.setVin(null);
        violations = validator.validate(car);
        assertEquals(1, violations.size());
        assertEquals(notNullMessage, violations.iterator().next().getMessage());
    }
}
