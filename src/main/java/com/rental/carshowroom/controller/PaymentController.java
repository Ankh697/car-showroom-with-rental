package com.rental.carshowroom.controller;

import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.service.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<Payment>> listAll() {
        return ResponseEntity.ok(paymentService.listAll());
    }

    @GetMapping("/waiting")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Payment>> listAllWaiting() {
        return ResponseEntity.ok(paymentService.listAllWaiting());
    }

    @PostMapping("/accept/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Payment> accept(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.acceptPayment(id));
    }

    @PostMapping("/decline/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Payment> decline(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.declinePayment(id));
    }
}
