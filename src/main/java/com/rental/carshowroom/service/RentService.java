package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.model.Rent;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.RentStatus;
import com.rental.carshowroom.repository.RentRepository;
import com.rental.carshowroom.service.payment.PaymentService;
import com.rental.carshowroom.validator.CarValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@PropertySource("classpath:validationmessages.properties")
public class RentService {
    private RentRepository rentRepository;
    private PaymentService paymentService;
    private CarValidator carValidator;
    private UserService userService;
    private CarService carService;

    @Value("${msg.validation.car.notforrent}")
    private String carNotForRent;

    @Autowired
    public RentService(RentRepository rentRepository, PaymentService paymentService, CarValidator carValidator, UserService userService, CarService carService) {
        this.rentRepository = rentRepository;
        this.paymentService = paymentService;
        this.carValidator = carValidator;
        this.userService = userService;
        this.carService = carService;
    }

    public Payment rentCar(Rent rent) {
        rent.setCar(carService.getCar(rent.getCar().getId()));
        rent.getCar().setStatus(CarStatus.RENTED);
        return paymentService.preparePaymentForRent(prepareRent(rent));
    }

    private Rent prepareRent(Rent rent) {
        rent.setStatus(RentStatus.RESERVED);
        rent.setCostPerDay(rent.getCar().getRentCostPerDay());
        rent.setUser(userService.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
        return rentRepository.save(rent);
    }

    public Map<String, String> validateRent(Car car) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (!carValidator.validateIfStatusCorrectForOperation(car, CarStatus.FOR_RENT)) {
            errors.put("status", carNotForRent);
        }
        return errors;
    }

    public Rent confirmRent(Long id) {
        Rent rent = findRent(id);
        rent.setStatus(RentStatus.CONFIRMED);
        return rentRepository.save(rent);
    }

    public Rent cancelRent(Long id) {
        Rent rent = findRent(id);
        rent.setStatus(RentStatus.CANCELLED);
        return rentRepository.save(rent);
    }

    public Rent finishRent(Long id) {
        Rent rent = findRent(id);
        rent.setStatus(RentStatus.FINISHED);
        rent.setReturnDate(LocalDateTime.now());
        rent.getCar().setStatus(CarStatus.FOR_RENT);
        return rentRepository.save(rent);
    }

    public Rent collectCar(Long id) {
        Rent rent = findRent(id);
        rent.setBorrowDate(LocalDateTime.now());
        return rentRepository.save(rent);
    }

    public Rent findRent(Long id) {
        return rentRepository.findById(id).orElseThrow(() -> new NotFoundException(NotFoundExceptionCode.RENT_NOT_FOUND));
    }

    public boolean isOwner(Long id) {
        return findRent(id).getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
