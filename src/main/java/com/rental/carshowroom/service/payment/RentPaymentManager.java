package com.rental.carshowroom.service.payment;

import com.rental.carshowroom.model.Rent;
import com.rental.carshowroom.model.Transaction;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.RentStatus;
import org.springframework.stereotype.Service;

@Service
public class RentPaymentManager implements PaymentManager {
    @Override
    public void updateAccepted(Transaction transaction) {
        Rent rent = (Rent) transaction;
        rent.setStatus(RentStatus.CONFIRMED);
    }

    @Override
    public void updateDeclined(Transaction transaction) {
        Rent rent = (Rent) transaction;
        rent.setStatus(RentStatus.CANCELLED);
        rent.getCar().setStatus(CarStatus.FOR_RENT);
    }
}
