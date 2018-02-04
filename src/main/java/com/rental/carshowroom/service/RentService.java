package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.model.Rent;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.RentStatus;
import com.rental.carshowroom.repository.RentRepository;
import com.rental.carshowroom.validator.CarValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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

    @Value("${msg.validation.car.notforrent")
    private String carNotForRent;

    private final String STATUS_KEY = "status";

    @Autowired
    public RentService(RentRepository rentRepository, PaymentService paymentService, CarValidator carValidator) {
        this.rentRepository = rentRepository;
        this.paymentService = paymentService;
        this.carValidator = carValidator;
    }

    public Payment rentCar(Rent rent) {
        Rent preparedRent = prepareRent(rent);
        rent.getCar().setStatus(CarStatus.RENTED);
        return paymentService.preparePaymentForRent(preparedRent);
    }

    private Rent prepareRent(Rent rent) {
        return rentRepository.save(Rent.builder()
                .status(RentStatus.RESERVED)
                .car(rent.getCar())
                .startDate(rent.getStartDate())
                .endDate(rent.getEndDate())
                .costPerDay(rent.getCar().getRentCostPerDay())
                .build());
    }

    public Map<String, String> validateRent(Car car) throws NotFoundException {
        Map<String, String> errors = new LinkedHashMap<>();
        if (!carValidator.validateIfStatusCorrectForOperation(car, CarStatus.FOR_RENT)) {
            errors.put(STATUS_KEY, carNotForRent);
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

    public Rent confirmCollect(Long id) {
        Rent rent = findRent(id);
        rent.setBorrowDate(LocalDateTime.now());
        return rentRepository.save(rent);
    }

    public Rent findRent(Long id) {
        Rent rent = rentRepository.findOne(id);
        if (rent != null) {
            return rent;
        } else {
            throw new NotFoundException(NotFoundExceptionCode.RENT_NOT_FOUND);
        }
    }
}
