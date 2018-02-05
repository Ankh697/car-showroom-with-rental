package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.Leasing;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.LeasingStatus;
import com.rental.carshowroom.repository.LeasingRepository;
import com.rental.carshowroom.validator.CarValidator;
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

    private final String STATUS_KEY = "status";
    private final String INSTALLMENTS_KEY = "installments";

    private LeasingRepository leasingRepository;
    private CarService carService;
    private PaymentService paymentService;
    private CarValidator carValidator;

    @Autowired
    public LeasingService(LeasingRepository leasingRepository, CarService carService, PaymentService paymentService, CarValidator carValidator) {
        this.leasingRepository = leasingRepository;
        this.carService = carService;
        this.paymentService = paymentService;
        this.carValidator = carValidator;
    }

    public List<Leasing> showAllLeasings() {
        return leasingRepository.findAll();
    }

    public boolean checkLeasingExist(Long id) throws NotFoundException {
        if (!leasingRepository.exists(id)) {
            throw new NotFoundException(NotFoundExceptionCode.LEASING_NOT_FOUND);
        }
        return true;
    }

    public Leasing findLeasing(Long id) throws NotFoundException {
        Leasing leasing = leasingRepository.findOne(id);
        if (leasing != null) {
            return leasing;
        } else {
            throw new NotFoundException(NotFoundExceptionCode.LEASING_NOT_FOUND);
        }
    }


    public Leasing addLeasing(Leasing leasing) {
        Leasing preparedLeasing = prepareLeasing(leasing);
        if (preparedLeasing.getInitialPayment().compareTo(BigDecimal.ZERO) > 0) {
            paymentService.preparePaymentForLeasing(preparedLeasing);
        }
        return preparedLeasing;
    }

    private Leasing prepareLeasing(Leasing leasing) {
        leasing.setCar(carService.getCar(leasing.getCar().getId()));
        leasing.setEndDate(leasing.getExpectedStartDate().plusMonths(leasing.getInstallments()));
        leasing.setLeasingStatus(LeasingStatus.WAITING);
        return leasingRepository.save(leasing);
    }

    public void deleteLeasing(Long id) {
        leasingRepository.delete(id);
    }

    public Leasing updateLeasing(Leasing leasing, Long id) throws NotFoundException {
        checkLeasingExist(id);
        leasing.setId(id);
        return leasingRepository.save(leasing);
    }

    public Leasing updateLeasingStatus(LeasingStatus leasingStatus, Long id) {
        Leasing leasing = findLeasing(id);
        leasing.setLeasingStatus(leasingStatus);
        return leasingRepository.save(leasing);
    }

    public List<Leasing> listAllLeasingBetweenTwoDates(LocalDate startOfLease, LocalDate endOfLease) {
        return leasingRepository.findAllLeaseBetweenDates(startOfLease, endOfLease);
    }

    public void validateLeasingInitiallPayment(Leasing leasing, Map<String, String> errors) {
        if (leasing.getInitialPayment().compareTo(carService.getCar(leasing.getCar().getId()).getPriceBrutto().multiply(new BigDecimal(0.3))) > 0) {
            errors.put("initialPayment", initialPaymentError);
        }
    }

    public Map<String, String> validateLeasing(Leasing leasing) {
        Map<String, String> errors = new HashMap<>();
        Car car = carService.getCar(leasing.getCar().getId());
        validateLeasingLenghth(car.getProductionYear(), leasing.getInstallments(), errors);
        validateLeasingStatus(car, errors);
        validateLeasingInitiallPayment(leasing, errors);
        return errors;
    }


    public List<Leasing> findAllLeasingsByStatus(LeasingStatus leasingStatus) {
        return leasingRepository.findAllByLeasingStatus(leasingStatus);
    }

    public BigDecimal calculateLeasing(Leasing leasing) {
        leasing.setCar(carService.getCar(leasing.getCar().getId()));
        BigDecimal priceBrutto = leasing.getCar().getPriceBrutto();
        return priceBrutto.setScale(2, BigDecimal.ROUND_UP).multiply(BigDecimal.valueOf(calculatePaymentForAgeGroup(leasing.getCar().getProductionYear()))
                .min(leasing.getInitialPayment()))
                .divide(BigDecimal.valueOf(leasing.getInstallments()), BigDecimal.ROUND_HALF_UP);
    }

    public double calculatePaymentForAgeGroup(int productionYear) {
        int age = calculateTimeBetweenProductionYearAndNow(productionYear);
        if (age < 3) {
            return 0.4;
        } else if (age < 5) {
            return 0.5;
        } else {
            return 0.65;
        }
    }

    public int calculateTimeBetweenProductionYearAndNow(int productionYear) {
        return LocalDate.now().getYear() - productionYear;
    }

    public Map<String, String> validateLeasingStatus(Car car, Map<String, String> errors) throws NotFoundException {
        if (!carValidator.validateIfStatusCorrectForOperation(car, CarStatus.FOR_SALE)) {
            errors.put(STATUS_KEY, carNotForLease);
        }
        return errors;
    }

    public Map<String, String> validateLeasingLenghth(int productionYear, Long installments, Map<String, String> errors) throws NotFoundException {
        if (calculateTimeBetweenProductionYearAndNow(productionYear) + (installments / 12) > 10) {
            errors.put(INSTALLMENTS_KEY, leasingLenghth);
        }
        return errors;
    }

}
