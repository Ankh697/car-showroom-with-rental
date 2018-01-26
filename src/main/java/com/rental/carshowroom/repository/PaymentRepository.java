package com.rental.carshowroom.repository;

import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.model.enums.PaymentStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long> {
    List<Payment> findAll();
    List<Payment> findAllByStatus(PaymentStatus status);
}
