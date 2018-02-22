package com.rental.carshowroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rental.carshowroom.AbstractWebIntegrationTest;
import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.Leasing;
import com.rental.carshowroom.model.Role;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.enums.*;
import com.rental.carshowroom.repository.CarRepository;
import com.rental.carshowroom.repository.LeasingRepository;
import com.rental.carshowroom.repository.UserRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DataJpaTest
@AutoConfigureMockMvc
public class LeasingControllerIntegrationTest extends AbstractWebIntegrationTest {
    @Autowired
    private LeasingRepository leasingRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserRepository userRepository;

    @Value("${msg.validation.car.notforlease}")
    private String notForLeaseError;

    private Leasing leasing;
    private Car car;
    private User user;
    private User admin;

    private final String VIN = "2AYNC6JM673S14892";
    private final String model = "Audi";
    private final String numberPlate = RandomStringUtils.randomAlphanumeric(7);
    private final String color = "black";
    private final Double engineCapacity = 2.5;
    private final BigDecimal priceBrutto = new BigDecimal(100000);
    private final BigDecimal initialPayment = new BigDecimal(6000);
    private final LocalDate expectedStartDate = LocalDate.now();

    @Before
    public void setup() {
        initMockMvc();
        car = Car.builder()
                .model(model)
                .productionYear(2016)
                .color(color)
                .priceBrutto(priceBrutto)
                .vin(VIN)
                .engineCapacity(engineCapacity)
                .enginePower(220L)
                .petrol(Petrol.DIESEL)
                .mileage(156000L)
                .whichOwner(1)
                .numberPlate(numberPlate)
                .status(CarStatus.FOR_SALE)
                .build();
        leasing = Leasing.builder()
                .installments(40L)
                .expectedStartDate(expectedStartDate)
                .car(Car.builder().id(1L).build())
                .initialPayment(initialPayment)
                .build();
        admin = User.builder()
                .username("admin")
                .pesel(RandomStringUtils.randomNumeric(11))
                .email("abc@gmail.com")
                .roles(Role.adminRoles())
                .status(UserStatus.ACTIVE)
                .build();
        user = User.builder()
                .username("user")
                .pesel(RandomStringUtils.randomNumeric(11))
                .email("abc@gmail.com")
                .roles(Role.userRoles())
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    public void leaseCar_Positive_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        mockMvc.perform(authenticatedToken(post("/api/leasing"), user)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(leasing)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/leasing")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionType").value(TransactionType.LEASING_INITIAL_PAYMNET.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(PaymentStatus.WAITING.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transaction.status").value(LeasingStatus.WAITING.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transaction.car.id").value(car.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transaction.car.status").value(CarStatus.LEASED.name()))
                .andReturn();
    }

    @Test
    public void leaseCar_NotForLease_Test() throws Exception {
        car.setStatus(CarStatus.FOR_RENT);
        carRepository.save(car);
        userRepository.save(user);
        mockMvc.perform(authenticatedToken(post("/api/leasing"), user)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(leasing)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(notForLeaseError))
                .andReturn();
    }

    @Test
    public void leaseCar_Unauthorized_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        mockMvc.perform(post("/api/leasing")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(leasing)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void cancelLeasing_Positive_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        leasing.setStatus(LeasingStatus.WAITING);
        leasing.setUser(user);
        leasingRepository.save(leasing);
        mockMvc.perform(authenticatedToken(post("/api/leasing/cancel/" + leasing.getId()), admin)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(LeasingStatus.CANCELLED.name()))
                .andReturn();
    }

    @Test
    public void cancelLeasing_NotAdmin_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        leasing.setStatus(LeasingStatus.ACCEPTED);
        leasing.setUser(user);
        leasingRepository.save(leasing);
        mockMvc.perform(authenticatedToken(post("/api/leasing/cancel/" + leasing.getId()), user)
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void cancelLeasing_Unauthorized_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        leasing.setStatus(LeasingStatus.ACCEPTED);
        leasing.setUser(user);
        leasingRepository.save(leasing);
        mockMvc.perform(post("/api/leasing/cancel/" + leasing.getId())
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void finishLeasing_Positive_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        leasing.setStatus(LeasingStatus.ACCEPTED);
        leasing.setUser(user);
        leasingRepository.save(leasing);
        mockMvc.perform(authenticatedToken(post("/api/leasing/finish/" + leasing.getId()), admin)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(LeasingStatus.FINISHED.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.car.status").value(CarStatus.FOR_SALE.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.endDate").exists())
                .andReturn();
    }

    @Test
    public void finishLeasing_Unauthorized_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        leasing.setStatus(LeasingStatus.ACCEPTED);
        leasing.setUser(user);
        leasingRepository.save(leasing);
        mockMvc.perform(post("/api/leasing/finish/" + leasing.getId())
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void getLeasing_Admin_Test() throws Exception {
        getLeasingTestForUser(admin);
    }

    private void getLeasingTestForUser(User principal) throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        leasing.setStatus(LeasingStatus.ACCEPTED);
        leasing.setUser(user);
        leasingRepository.save(leasing);
        mockMvc.perform(authenticatedToken(get("/api/leasing/" + leasing.getId()), principal)
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getLeasing_NotOwner_Test() throws Exception {
        User testUser = User.builder()
                .username(RandomStringUtils.randomAlphabetic(8))
                .roles(Role.userRoles())
                .status(UserStatus.ACTIVE)
                .build();
        carRepository.save(car);
        userRepository.save(user);
        leasing.setStatus(LeasingStatus.ACCEPTED);
        leasing.setUser(user);
        leasingRepository.save(leasing);
        mockMvc.perform(authenticatedToken(get("/api/leasing/" + leasing.getId()), testUser)
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void getLeasing_Unauthorized_Test() throws Exception {
        carRepository.save(car);
        userRepository.save(user);
        leasing.setStatus(LeasingStatus.ACCEPTED);
        leasing.setUser(user);
        leasingRepository.save(leasing);
        mockMvc.perform(get("/api/leasing/" + leasing.getId())
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}
