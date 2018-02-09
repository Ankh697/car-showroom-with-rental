package com.rental.carshowroom.util;

import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.Leasing;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LeasingUtil {

    public static BigDecimal calculateLeasing(Leasing leasing, Car car) {
        BigDecimal priceBrutto = car.getPriceBrutto();
        return priceBrutto.setScale(2, BigDecimal.ROUND_UP).multiply(calculatePaymentForAgeGroup(car.getProductionYear())
                .min(leasing.getInitialPayment()))
                .divide(BigDecimal.valueOf(leasing.getInstallments()), BigDecimal.ROUND_HALF_UP);
    }

    private static BigDecimal calculatePaymentForAgeGroup(int productionYear) {
        int age = calculateTimeBetweenProductionYearAndNow(productionYear);
        if (age <= 3) {
            return BigDecimal.valueOf(0.4);
        } else if (age < 5) {
            return BigDecimal.valueOf(0.5);
        } else {
            return BigDecimal.valueOf(0.65);
        }
    }

    public static int calculateTimeBetweenProductionYearAndNow(int productionYear) {
        return LocalDate.now().getYear() - productionYear;
    }
}
