package com.rental.carshowroom.controller;

import com.rental.carshowroom.model.Rent;
import com.rental.carshowroom.service.CarService;
import com.rental.carshowroom.service.RentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> rentCar(@RequestBody @Valid Rent rent) {
        Map<String, String> errors = rentService.validateRent(carService.getCar(rent.getCar().getId()));
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok(rentService.rentCar(rent));
    }

    @PatchMapping("/confirm/{id}")
    public ResponseEntity<Rent> confirmRent(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rentService.confirmRent(id));
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<Rent> cancelRent(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rentService.cancelRent(id));
    }

    @PatchMapping("/finish/{id}")
    public ResponseEntity<Rent> finishRent(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rentService.finishRent(id));
    }

    @PatchMapping("/collect/{id}")
    public ResponseEntity<Rent> confirmCollect(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rentService.confirmCollect(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rent> getRent(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rentService.findRent(id));
    }
}
