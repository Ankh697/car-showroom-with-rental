package com.rental.carshowroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rental.carshowroom.AbstractWebIntegrationTest;
import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.Role;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.Petrol;
import com.rental.carshowroom.model.enums.UserStatus;
import com.rental.carshowroom.repository.CarRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DataJpaTest
@AutoConfigureMockMvc
public class CarControllerIntegrationTest extends AbstractWebIntegrationTest {
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private ObjectMapper mapper;

    private Car car;
    private User admin;
    private User user;


    private final String VIN = "2AYNC6JM673S14892";
    private final String model = "Audi";
    private final String numberPlate = RandomStringUtils.randomAlphanumeric(7);
    private final String color = "black";
    private final Double engineCapacity = 2.5;
    private final BigDecimal priceBrutto = new BigDecimal(100000);

    @Before
    public void setup() {
        initMockMvc();
        car = Car.builder()
                .model(model)
                .productionYear(2009)
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
        admin = User.builder()
                .username("admin")
                .roles(Role.adminRoles())
                .status(UserStatus.ACTIVE)
                .build();
        user = User.builder()
                .username("user")
                .roles(Role.userRoles())
                .status(UserStatus.ACTIVE)
                .build();
    }

    private void saveTestCar() {
        carRepository.save(car);
        carRepository.save(Car.builder()
                .model(model)
                .productionYear(2009)
                .color(color)
                .priceBrutto(priceBrutto)
                .vin(VIN)
                .engineCapacity(engineCapacity)
                .enginePower(220L)
                .petrol(Petrol.GASOLINE)
                .mileage(156000L)
                .whichOwner(1)
                .numberPlate(numberPlate)
                .status(CarStatus.FOR_SALE)
                .build());
    }

    @Test
    public void addCar_Positive_Test() throws Exception {
        mockMvc.perform(authenticatedToken(post("/api/car"), admin)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(car)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    public void addCar_NoAuthorize_Test() throws Exception {
        mockMvc.perform(post("/api/car")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(car)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void addCar_NotAdmin_test() throws Exception {
        carRepository.save(car);
        mockMvc.perform(authenticatedToken(post("/api/car"), user)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(car)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void listAllCars_Positive_test() throws Exception {
        saveTestCar();
        MockHttpServletResponse response = mockMvc.perform(authenticatedToken(get("/api/car"), admin))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        Car[] cars = mapper.readValue(response.getContentAsString(), Car[].class);
        assertEquals(2, cars.length);
    }

    @Test
    public void listAllCars_NotAdmin_test() throws Exception {
        mockMvc.perform(authenticatedToken(get("/api/car"), user))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void listAllCars_NoAuthorize_test() throws Exception {
        mockMvc.perform(get("/api/car"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void updateCar_Admin_Test() throws Exception {
        checkPositiveUpdate(admin);
    }

    private void checkPositiveUpdate(User principal) throws Exception {
        carRepository.save(car);
        mockMvc.perform(authenticatedToken(put("/api/car/" + car.getId()), principal)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(car)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.used").value(car.getUsed()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.whichOwner").value(car.getWhichOwner()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(car.getStatus().name()))
                .andReturn();
    }

    @Test
    public void updateCar_Unauthorized_Test() throws Exception {
        carRepository.save(car);
        mockMvc.perform(put("/api/car/" + car.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(car)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void updateCar_NotAdmin_test() throws Exception {
        carRepository.save(car);
        mockMvc.perform(authenticatedToken(put("/api/car/" + car.getId()), user)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(car)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void updateCar_ChangeStatus_Test() throws Exception {
        carRepository.save(car);
        car.setModel(model);
        mockMvc.perform(authenticatedToken(put("/api/car/" + car.getId()), admin)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(car)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(car.getStatus().name()))
                .andReturn();
    }

    @Test
    public void deleteCar_Admin_Test() throws Exception {
        carRepository.save(car);
        mockMvc.perform(authenticatedToken(delete("/api/car/" + car.getId()), admin)
                .with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn();
        assertNull(carRepository.findOne(car.getId()));
    }

    @Test
    public void deleteCar_NotAdmin_test() throws Exception {
        carRepository.save(car);
        mockMvc.perform(authenticatedToken(delete("/api/car/" + car.getId()), user)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(car)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void deleteCar_Unauthorized_Test() throws Exception {
        carRepository.save(car);
        mockMvc.perform(delete("/api/car/" + car.getId())
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}
