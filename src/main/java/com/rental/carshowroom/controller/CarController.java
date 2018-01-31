package com.rental.carshowroom.controller;

import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Car>> showAll() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCar(id));
    }

    @PostMapping
    public ResponseEntity<Car> addCar(@RequestBody @Valid Car car) {
        Car addedCar = carService.addCar(car);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(addedCar.getId()).toUri();
        return ResponseEntity.created(location).body(addedCar);
    }

    @GetMapping("/forbuy")
    public ResponseEntity<List<Car>> getAllAviableCarsToLeaseOrBuy() {
        return ResponseEntity.ok(carService.findAllAviableCarsToLeaseOrBuy());
    }

    @GetMapping("/forrent")
    public ResponseEntity<List<Car>> getAllAvaibleCarsToRent() {
        return ResponseEntity.ok(carService.findAllAvaibleCarsToRent());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody @Valid Car car) {
        return ResponseEntity.ok(carService.updateCar(car, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<Car> editCarStatus(@RequestBody @Valid Car car, @PathVariable Long id) {
        return ResponseEntity.ok(carService.updateCarStatus(id, car.getStatus()));
    }

}