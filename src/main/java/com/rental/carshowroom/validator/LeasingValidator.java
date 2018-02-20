package com.rental.carshowroom.validator;

import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.Leasing;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.util.LeasingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class LeasingValidator {

    @Value("${msg.validation.leasing.initialpayment.value")
    private String initialPaymentError;
    @Value("${msg.validation.car.notforlease")
    private String carNotForLease;
    @Value("${msg.validation.leasing.leasinglenght")
    private String leasingLenghth;

    private CarValidator carValidator;

    @Autowired
    public LeasingValidator(CarValidator carValidator) {
        this.carValidator = carValidator;
    }

    public void validateLeasingInitialPayment(Leasing leasing, Car car, Map<String, String> errors) {
        if (leasing.getInitialPayment().compareTo(car.getPriceBrutto().multiply(BigDecimal.valueOf(0.3))) > 0) {
            errors.put("initialPayment", initialPaymentError);
        }
    }

    public void validateLeasingStatus(Car car, Map<String, String> errors) {
        if (!carValidator.validateIfStatusCorrectForOperation(car, CarStatus.FOR_SALE)) {
            errors.put("status", carNotForLease);
        }
    }

    public void validateLeasingLength(int productionYear, Long installments, Map<String, String> errors) {
        if (LeasingUtil.calculateTimeBetweenProductionYearAndNow(productionYear) + (installments / 12) > 10) {
            errors.put("installments", leasingLenghth);
        }
    }
}
