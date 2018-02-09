package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.model.Sale;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.SaleStatus;
import com.rental.carshowroom.repository.SaleRepository;
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
public class SaleService {
    private SaleRepository saleRepository;
    private PaymentService paymentService;
    private CarValidator carValidator;
    private CarService carService;
    private UserService userService;

    @Value("${msg.validation.car.notforsale")
    private String carNotForSale;

    private final String STATUS_KEY = "status";

    @Autowired
    public SaleService(SaleRepository saleRepository, PaymentService paymentService, CarValidator carValidator, CarService carService, UserService userService) {
        this.saleRepository = saleRepository;
        this.paymentService = paymentService;
        this.carValidator = carValidator;
        this.carService = carService;
        this.userService = userService;
    }

    public Sale prepareSale(Car car) {
        return saleRepository.save(Sale.builder()
                .price(car.getPriceBrutto())
                .requestedDate(LocalDateTime.now())
                .status(SaleStatus.ORDERED)
                .car(car)
                .user(userService.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName()))
                .build());
    }

    public Payment buyCar(Long id) throws NotFoundException {
        Sale sale = prepareSale(carService.getCar(id));
        sale.getCar().setStatus(CarStatus.SOLD);
        return paymentService.preparePaymentForSale(sale);
    }

    public Map<String, String> validateBuy(Car car) throws NotFoundException {
        Map<String, String> errors = new LinkedHashMap<>();
        if (!carValidator.validateIfStatusCorrectForOperation(car, CarStatus.FOR_SALE)) {
            errors.put(STATUS_KEY, carNotForSale);
        }
        return errors;
    }
}
