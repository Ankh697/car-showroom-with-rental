package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.model.Rent;
import com.rental.carshowroom.model.Sale;
import com.rental.carshowroom.model.Transaction;
import com.rental.carshowroom.model.enums.*;
import com.rental.carshowroom.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public Payment preparePaymentForRent(Rent rent) {
        return paymentRepository.save(Payment.builder()
                .accountNumber(accountNumber)
                .address(address)
                .companyName(name)
                .transaction(rent)
                .status(PaymentStatus.WAITING)
                .value(rent.getCostPerDay().multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(rent.getStartDate(), rent.getEndDate()))))
                .transactionType(TransactionType.RENT).build());
    }

    public List<Payment> listAll() {
        return paymentRepository.findAll();
    }

    public List<Payment> listAllWaiting() {
        return paymentRepository.findAllByStatus(PaymentStatus.WAITING);
    }

    public Payment acceptPayment(Long id) throws NotFoundException {
        Payment payment = findPayment(id);
        payment.setAcceptedDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.ACCEPTED);
        switch (payment.getTransactionType()) {
            case BUY: {
                updateAcceptedSale(payment.getTransaction());
                break;
            }
            case RENT: {
                updateAcceptedRent(payment.getTransaction());
                break;
            }
            case LEASING:
                break;
            case PENALTY:
                break;
        }
        return paymentRepository.save(payment);
    }

    public Payment declinePayment(Long id) throws NotFoundException {
        Payment payment = findPayment(id);
        payment.setStatus(PaymentStatus.DECLINED);
        switch (payment.getTransactionType()) {
            case BUY: {
                updateDeclinedSale(payment.getTransaction());
                break;
            }
            case RENT:
                updateDeclinedRent(payment.getTransaction());
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

    private void updateDeclinedSale(Transaction transaction) {
        Sale sale = (Sale) transaction;
        sale.setStatus(SaleStatus.REJECTED);
        sale.getCar().setStatus(CarStatus.FOR_SALE);
    }

    private void updateAcceptedRent(Transaction transaction) {
        Rent rent = (Rent) transaction;
        rent.setStatus(RentStatus.CONFIRMED);
    }

    private void updateDeclinedRent(Transaction transaction) {
        Rent rent = (Rent) transaction;
        rent.setStatus(RentStatus.CANCELLED);
        rent.getCar().setStatus(CarStatus.FOR_RENT);
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
