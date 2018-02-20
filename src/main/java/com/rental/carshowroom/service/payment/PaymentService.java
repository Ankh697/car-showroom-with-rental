package com.rental.carshowroom.service.payment;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Leasing;
import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.model.Rent;
import com.rental.carshowroom.model.Sale;
import com.rental.carshowroom.model.enums.PaymentStatus;
import com.rental.carshowroom.model.enums.TransactionType;
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
    private PaymentManagerFactory paymentManagerFactory;

    @Value("${number}")
    private String accountNumber;

    @Value("${name}")
    private String name;

    @Value("${address}")
    private String address;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, PaymentManagerFactory paymentManagerFactory) {
        this.paymentRepository = paymentRepository;
        this.paymentManagerFactory = paymentManagerFactory;
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

    public Payment preparePaymentForLeasing(Leasing leasing) {
        return paymentRepository.save(Payment.builder()
                .accountNumber(accountNumber)
                .address(address)
                .companyName(name)
                .transaction(leasing)
                .status(PaymentStatus.WAITING)
                .value(leasing.getInitialPayment())
                .transactionType(TransactionType.LEASING_INITIAL_PAYMNET).build());
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
        paymentManagerFactory.getPaymentManagerForTransactionType(payment.getTransactionType()).updateAccepted(payment.getTransaction());
        return paymentRepository.save(payment);
    }

    public Payment declinePayment(Long id) {
        Payment payment = findPayment(id);
        payment.setStatus(PaymentStatus.DECLINED);
        paymentManagerFactory.getPaymentManagerForTransactionType(payment.getTransactionType()).updateDeclined(payment.getTransaction());
        return paymentRepository.save(payment);
    }


    private Payment findPayment(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new NotFoundException(NotFoundExceptionCode.PAYMENT_NOT_FOUND));
    }
}
