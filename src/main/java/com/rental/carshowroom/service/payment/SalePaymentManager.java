package com.rental.carshowroom.service.payment;

import com.rental.carshowroom.model.Sale;
import com.rental.carshowroom.model.Transaction;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.SaleStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SalePaymentManager implements PaymentManager {
    @Override
    public void updateAccepted(Transaction transaction) {
        Sale sale = (Sale) transaction;
        sale.setSoldDate(LocalDateTime.now());
        sale.setStatus(SaleStatus.ACCEPTED);
    }

    @Override
    public void updateDeclined(Transaction transaction) {
        Sale sale = (Sale) transaction;
        sale.setStatus(SaleStatus.REJECTED);
        sale.getCar().setStatus(CarStatus.FOR_SALE);
    }
}
