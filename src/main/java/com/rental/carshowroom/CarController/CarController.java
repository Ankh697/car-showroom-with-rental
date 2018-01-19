package com.rental.carshowroom.CarController;

import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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
        return ResponseEntity.ok(carService.addCar(car));
    }

    @GetMapping("/buy")
    public ResponseEntity<List<Car>> getAllAviableCarsToLeaseOrBuy() {
        return ResponseEntity.ok(carService.findAllAviableCarsToLeaseOrBuy());
    }

    @GetMapping("/rent")
    public ResponseEntity<List<Car>> getAllAvaibleCarsToRent() {
        return ResponseEntity.ok(carService.findAllAvaibleCarsToRent());
    }

    @PutMapping("/id")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody @Valid Car car) {
        return ResponseEntity.ok(carService.updateCar(car, id));
    }

    @DeleteMapping("/id")
    public ResponseEntity<Map<String, String>> deleteCar(@PathVariable Long id) {
        Map<String, String> errors = carService.validateDeleteOrUpdateCar(id);
        if (errors.isEmpty()) {
            carService.deleteCar(id);
            return ResponseEntity.noContent().build();
        } else return ResponseEntity.badRequest().body(errors);
    }

}