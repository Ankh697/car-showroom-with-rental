package com.rental.carshowroom.CarController;

import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/car")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping
    public ResponseEntity<List<Car>> showAll() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id)
    {
        return ResponseEntity.ok(carService.getCar(id));
    }

    @PostMapping
    public ResponseEntity<?> addCar(@RequestBody @Valid Car car)
    {
        return ResponseEntity.ok(carService.addCar(car));
    }

    @GetMapping("/buy")
    public ResponseEntity<List<Car>> getAllAviableCarsToLeaseOrBuy()
    {
        return ResponseEntity.ok(carService.findAllAviableCarsToLeaseOrBuy());
    }

    @GetMapping("/rent")
    public ResponseEntity<List<Car>> getAllAvaibleCarsToRent()
    {
        return ResponseEntity.ok(carService.findAllAvaibleCarsToRent());
    }

    @PutMapping("/id")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody @Valid Car car){
        return ResponseEntity.ok(carService.updateCar(car, id));
    }

    @DeleteMapping("/id")
    public String deleteCar(@PathVariable Long id){
        return carService.deleteCar(id);
    }

}