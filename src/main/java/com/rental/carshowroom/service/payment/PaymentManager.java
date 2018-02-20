package com.rental.carshowroom.service.payment;

import com.rental.carshowroom.model.Transaction;

public interface PaymentManager {
    void updateAccepted(Transaction transaction);

    void updateDeclined(Transaction transaction);
}
