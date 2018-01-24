package com.rental.carshowroom.controller;

import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Payment>> listAllWaiting() {
        return ResponseEntity.ok(paymentService.listAllWaiting());
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<Payment> accept(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.acceptPayment(id));
    }

    @PostMapping("/decline/{id}")
    public ResponseEntity<Payment> decline(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.declinePayment(id));
    }
}
