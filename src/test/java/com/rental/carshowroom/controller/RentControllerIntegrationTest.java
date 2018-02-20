package com.rental.carshowroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rental.carshowroom.AbstractWebIntegrationTest;
import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.Rent;
import com.rental.carshowroom.model.Role;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.enums.*;
import com.rental.carshowroom.repository.CarRepository;
import com.rental.carshowroom.repository.RentRepository;
import com.rental.carshowroom.repository.UserRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataJpaTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:validationmessages.properties")
public class RentControllerIntegrationTest extends AbstractWebIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RentRepository rentRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private CarRepository carRepository;
    @Value("${msg.validation.car.notforrent}")
    private String notForRentError;

    private Car car;
    private User user;
    private User admin;
    private Rent rent;

    @Before
    public void setup() {
        initMockMvc();
        car = Car.builder()
                .model(RandomStringUtils.randomAlphabetic(5))
                .numberPlate(RandomStringUtils.randomAlphabetic(10))
                .engineCapacity(RandomUtils.nextLong())
                .enginePower(RandomUtils.nextLong())
                .status(CarStatus.FOR_RENT)
                .petrol(Petrol.DIESEL)
                .color("black")
                .vin("2AYNC6JM673S14892")
                .rentCostPerDay(BigDecimal.ONE)
                .build();
        user = User.builder()
                .username(RandomStringUtils.randomAlphabetic(5))
                .nameAndSurname(RandomStringUtils.randomAlphabetic(5))
                .pesel(RandomStringUtils.randomNumeric(11))
                .roles(Role.userRoles())
                .email("xxx@example.com")
                .status(UserStatus.ACTIVE)
                .password(RandomStringUtils.randomAlphanumeric(10))
                .build();
        admin = User.builder()
                .username("admin")
                .roles(Role.adminRoles())
                .status(UserStatus.ACTIVE)
                .build();
        rent = Rent.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(3))
                .car(Car.builder().id(1L).build())
                .build();
    }

    @Test
    public void rentCar_Positive_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        mockMvc.perform(authenticatedToken(post("/api/rent"), user)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(rent)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/rent")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionType").value(TransactionType.RENT.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(PaymentStatus.WAITING.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transaction.status").value(RentStatus.RESERVED.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transaction.car.id").value(car.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transaction.car.status").value(CarStatus.RENTED.name()))
                .andReturn();
    }

    @Test
    public void rentCar_NotForRent_Test() throws Exception {
        car.setStatus(CarStatus.FOR_SALE);
        carRepository.save(car);
        userRepository.save(user);
        mockMvc.perform(authenticatedToken(post("/api/rent"), user)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(rent)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(notForRentError))
                .andReturn();
    }

    @Test
    public void rentCar_Unauthorized_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        mockMvc.perform(post("/api/rent")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(rent)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void confirmRent_Positive_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.RESERVED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(authenticatedToken(post("/api/rent/confirm/" + rent.getId()), admin)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(RentStatus.CONFIRMED.name()))
                .andReturn();
    }

    @Test
    public void confirmRent_NotAdmin_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.RESERVED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(authenticatedToken(post("/api/rent/confirm/" + rent.getId()), user)
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void confirmRent_Unauthorized_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.RESERVED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(post("/api/rent/confirm/" + rent.getId())
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void cancelRent_Positive_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.RESERVED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(authenticatedToken(post("/api/rent/cancel/" + rent.getId()), admin)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(RentStatus.CANCELLED.name()))
                .andReturn();
    }

    @Test
    public void cancelRent_NotAdmin_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.RESERVED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(authenticatedToken(post("/api/rent/cancel/" + rent.getId()), user)
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void cancelRent_Unauthorized_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.RESERVED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(post("/api/rent/cancel/" + rent.getId())
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void finishRent_Positive_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.CONFIRMED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(authenticatedToken(post("/api/rent/finish/" + rent.getId()), admin)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(RentStatus.FINISHED.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.car.status").value(CarStatus.FOR_RENT.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.returnDate").exists())
                .andReturn();
    }

    @Test
    public void finishRent_NotAdmin_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.CONFIRMED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(authenticatedToken(post("/api/rent/finish/" + rent.getId()), user)
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void finishRent_Unauthorized_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.CONFIRMED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(post("/api/rent/finish/" + rent.getId())
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void collect_Positive_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.CONFIRMED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(authenticatedToken(post("/api/rent/collect/" + rent.getId()), admin)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.borrowDate").exists())
                .andReturn();
    }

    @Test
    public void collect_NotAdmin_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.CONFIRMED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(authenticatedToken(post("/api/rent/collect/" + rent.getId()), user)
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void collect_Unauthorized_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.CONFIRMED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(post("/api/rent/collect/" + rent.getId())
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void getRent_Admin_Test() throws Exception {
        getRentTestForUser(admin);
    }

    @Test
    public void getRent_Owner_Test() throws Exception {
        getRentTestForUser(user);
    }

    private void getRentTestForUser(User principal) throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.CONFIRMED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(authenticatedToken(get("/api/rent/" + rent.getId()), principal)
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getRent_NotOwner_Test() throws Exception {
        User testUser = User.builder()
                .username(RandomStringUtils.randomAlphabetic(8))
                .roles(Role.userRoles())
                .status(UserStatus.ACTIVE)
                .build();
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.CONFIRMED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(authenticatedToken(get("/api/rent/" + rent.getId()), testUser)
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void getRent_Unauthorized_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        rent.setStatus(RentStatus.CONFIRMED);
        rent.setUser(user);
        rentRepository.save(rent);
        mockMvc.perform(get("/api/rent/" + rent.getId())
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}
