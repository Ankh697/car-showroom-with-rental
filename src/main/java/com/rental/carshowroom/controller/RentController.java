package com.rental.carshowroom.controller;

import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.model.Rent;
import com.rental.carshowroom.service.CarService;
import com.rental.carshowroom.service.RentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/rent")
public class RentController {
    private RentService rentService;
    private CarService carService;

    @Autowired
    public RentController(RentService rentService, CarService carService) {
        this.rentService = rentService;
        this.carService = carService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> rentCar(@RequestBody @Valid Rent rent) {
        Map<String, String> errors = rentService.validateRent(carService.getCar(rent.getCar().getId()));
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        Payment payment = rentService.rentCar(rent);
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(payment.getTransaction().getId()).toUri())
                .body(payment);
    }

    //TODO: validation rent statuses

    @PostMapping("/confirm/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Rent> confirmRent(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rentService.confirmRent(id));
    }

    @PostMapping("/cancel/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Rent> cancelRent(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rentService.cancelRent(id));
    }

    @PostMapping("/finish/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Rent> finishRent(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rentService.finishRent(id));
    }

    @PostMapping("/collect/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Rent> collectCar(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rentService.collectCar(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("(hasRole('ROLE_USER') and @rentService.isOwner(#id)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Rent> getRent(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rentService.findRent(id));
    }
}
