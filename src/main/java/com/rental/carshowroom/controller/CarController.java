package com.rental.carshowroom.controller;

import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/car")
public class CarController {
    private CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Car>> showAll() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCar(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Car> addCar(@RequestBody @Valid Car car) {
        Car addedCar = carService.addCar(car);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(addedCar.getId()).toUri();
        return ResponseEntity.created(location).body(addedCar);
    }

    @GetMapping("/buy")
    public ResponseEntity<List<Car>> getAllAviableCarsToLeaseOrBuy() {
        return ResponseEntity.ok(carService.findAllAvailableCarsToLeaseOrBuy());
    }

    @GetMapping("/rent")
    public ResponseEntity<List<Car>> getAllAvaibleCarsToRent() {
        return ResponseEntity.ok(carService.findAllAvailableCarsToRent());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody @Valid Car car) {
        carService.validateIfCarExist(id);
        return ResponseEntity.ok(carService.updateCar(car, id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity deleteCar(@PathVariable Long id) {
        carService.validateIfCarExist(id);
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/status/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Car> editCarStatus(@RequestBody @Valid Car car, @PathVariable Long id) {
        carService.validateIfCarExist(id);
        return ResponseEntity.ok(carService.updateCarStatus(id, car.getStatus()));
    }

}