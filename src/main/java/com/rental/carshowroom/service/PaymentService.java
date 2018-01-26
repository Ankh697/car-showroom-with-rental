package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.model.Sale;
import com.rental.carshowroom.model.Transaction;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.PaymentStatus;
import com.rental.carshowroom.model.enums.SaleStatus;
import com.rental.carshowroom.model.enums.TransactionType;
import com.rental.carshowroom.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@PropertySource("classpath:payment.properties")
public class PaymentService {
    private PaymentRepository paymentRepository;

    @Value("${number}")
    private String accountNumber;

    @Value("${name}")
    private String name;

    @Value("${address}")
    private String address;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment preparePaymentForSale(Sale sale) {
        return paymentRepository.save(Payment.builder()
                .accountNumber(accountNumber)
                .address(address)
                .companyName(name)
                .transaction(sale)
                .status(PaymentStatus.WAITING)
                .value(sale.getPrice())
                .transactionType(TransactionType.BUY).build());
    }

    public List<Payment> listAll() {
        return paymentRepository.findAll();
    }

    public List<Payment> listAllWaiting() {
        return paymentRepository.findAllByStatus(PaymentStatus.WAITING);
    }

    public Payment acceptPayment(Long id) {
        Payment payment = findPayment(id);
        payment.setAcceptedDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.ACCEPTED);
        switch (payment.getTransactionType()) {
            case BUY: {
                updateAcceptedSale(payment.getTransaction());
                break;
            }
            case RENT:
                break;
            case LEASING:
                break;
            case PENALTY:
                break;
        }
        return paymentRepository.save(payment);
    }

    private void updateAcceptedSale(Transaction transaction) {
        Sale sale = (Sale) transaction;
        sale.setSoldDate(LocalDateTime.now());
        sale.setStatus(SaleStatus.ACCEPTED);
    }

    public Payment declinePayment(Long id) {
        Payment payment = findPayment(id);
        payment.setStatus(PaymentStatus.DECLINED);
        switch (payment.getTransactionType()) {
            case BUY: {
                updateDeclinedSale(payment.getTransaction());
                break;
            }
            case RENT:
                break;
            case LEASING:
                break;
            case PENALTY:
                break;
        }
        return paymentRepository.save(payment);
    }

    private void updateDeclinedSale(Transaction transaction) {
        Sale sale = (Sale) transaction;
        sale.setStatus(SaleStatus.REJECTED);
        sale.getCar().setStatus(CarStatus.FOR_SALE);
    }

    private Payment findPayment(Long id) throws NotFoundException {
        Payment payment = paymentRepository.findOne(id);
        if (payment != null) {
            return payment;
        } else {
            throw new NotFoundException(NotFoundExceptionCode.PAYMENT_NOT_FOUND);
        }
    }
}
