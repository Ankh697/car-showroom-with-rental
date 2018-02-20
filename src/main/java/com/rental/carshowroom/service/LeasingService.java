package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.Leasing;
import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.LeasingStatus;
import com.rental.carshowroom.repository.LeasingRepository;
import com.rental.carshowroom.service.payment.PaymentService;
import com.rental.carshowroom.validator.LeasingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:validationmessages.properties")
public class LeasingService {

    @Value("${msg.validation.leasing.initialpayment.value")
    private String initialPaymentError;
    @Value("${msg.validation.car.notforlease")
    private String carNotForLease;
    @Value("${msg.validation.leasing.leasinglenght")
    private String leasingLenghth;

    private LeasingRepository leasingRepository;
    private CarService carService;
    private PaymentService paymentService;
    private LeasingValidator leasingValidator;

    @Autowired
    public LeasingService(LeasingRepository leasingRepository, CarService carService, PaymentService paymentService, LeasingValidator leasingValidator) {
        this.leasingRepository = leasingRepository;
        this.carService = carService;
        this.paymentService = paymentService;
        this.leasingValidator = leasingValidator;
    }

    public List<Leasing> showAllLeasings() {
        return leasingRepository.findAll();
    }

    public Leasing findLeasing(Long id) {
        return leasingRepository.findById(id).orElseThrow(() -> new NotFoundException(NotFoundExceptionCode.LEASING_NOT_FOUND));
    }

    public Payment addLeasing(Leasing leasing) {
        Leasing preparedLeasing = prepareLeasing(leasing);
        if (preparedLeasing.getInitialPayment().compareTo(BigDecimal.ZERO) > 0) {
            return paymentService.preparePaymentForLeasing(preparedLeasing);
        } else {
            return Payment.builder().transaction(preparedLeasing).build();
        }
    }

    private Leasing prepareLeasing(Leasing leasing) {
        leasing.setCar(carService.getCar(leasing.getCar().getId()));
        leasing.setEndDate(leasing.getExpectedStartDate().plusMonths(leasing.getInstallments()));
        leasing.setLeasingStatus(LeasingStatus.WAITING);
        return leasingRepository.save(leasing);
    }

    public List<Leasing> listAllLeasingBetweenTwoDates(LocalDate startOfLease, LocalDate endOfLease) {
        return leasingRepository.findAllLeaseBetweenDates(startOfLease, endOfLease);
    }

    public Map<String, String> validateLeasing(Leasing leasing) {
        Map<String, String> errors = new HashMap<>();
        Car car = carService.getCar(leasing.getCar().getId());
        leasingValidator.validateLeasingLength(car.getProductionYear(), leasing.getInstallments(), errors);
        leasingValidator.validateLeasingStatus(car, errors);
        leasingValidator.validateLeasingInitialPayment(leasing, car, errors);
        return errors;
    }

    public Leasing cancelLeasing(Long id) {
        Leasing leasing = findLeasing(id);
        leasing.setLeasingStatus(LeasingStatus.CANCELLED);
        leasing.getCar().setStatus(CarStatus.FOR_SALE);
        return leasingRepository.save(leasing);
    }

    public Leasing finishLeasing(Long id) {
        Leasing leasing = findLeasing(id);
        leasing.setEndDate(LocalDate.now());
        leasing.getCar().setStatus(CarStatus.FOR_SALE);
        return leasingRepository.save(leasing);
    }
}
