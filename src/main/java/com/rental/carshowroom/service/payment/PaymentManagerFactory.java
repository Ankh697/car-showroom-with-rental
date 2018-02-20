package com.rental.carshowroom.service.payment;

import com.rental.carshowroom.model.enums.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentManagerFactory {
    @Autowired
    private LeasingInitialPaymentManager leasingInitialPaymentManager;
    @Autowired
    private LeasingInstallmentsPaymentManager leasingInstallmentsPaymentManager;
    @Autowired
    private SalePaymentManager salePaymentManager;
    @Autowired
    private RentPaymentManager rentPaymentManager;

    public PaymentManager getPaymentManagerForTransactionType(TransactionType type) {
        switch (type) {
            case BUY:
                return salePaymentManager;
            case RENT:
                return rentPaymentManager;
            case LEASING_INITIAL_PAYMNET:
                return leasingInitialPaymentManager;
            case LEASING_INSTALLMENT:
                return leasingInstallmentsPaymentManager;
            default:
                return null;
        }
    }
}
