package com.rental.carshowroom.service.payment;

import com.rental.carshowroom.model.Leasing;
import com.rental.carshowroom.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public class LeasingInstallmentsPaymentManager implements PaymentManager {
    @Override
    public void updateAccepted(Transaction transaction) {
        Leasing leasing = (Leasing) transaction;
        leasing.setInstallmentsPaid(leasing.getInstallmentsPaid() + 1);
    }

    @Override
    public void updateDeclined(Transaction transaction) {
        //TODO: send email notification
    }
}