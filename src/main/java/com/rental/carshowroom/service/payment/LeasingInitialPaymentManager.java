package com.rental.carshowroom.service.payment;

import com.rental.carshowroom.model.Leasing;
import com.rental.carshowroom.model.Transaction;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.LeasingStatus;
import org.springframework.stereotype.Service;

@Service
public class LeasingInitialPaymentManager implements PaymentManager {
    @Override
    public void updateAccepted(Transaction transaction) {
        Leasing leasing = (Leasing) transaction;
        leasing.setLeasingStatus(LeasingStatus.ACCEPTED);
    }

    @Override
    public void updateDeclined(Transaction transaction) {
        Leasing leasing = (Leasing) transaction;
        leasing.setLeasingStatus(LeasingStatus.REJECTED);
        leasing.getCar().setStatus(CarStatus.FOR_SALE);
    }
}
