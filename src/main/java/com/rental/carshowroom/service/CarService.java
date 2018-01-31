package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PropertySource("classpath:validationmessages.properties")
public class CarService {
    private CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCar(Long id) throws NotFoundException {
        return findCar(id);
    }

    private Car findCar(Long id) throws NotFoundException {
        Car car = carRepository.findOne(id);
        if (car != null) {
            return car;
        } else {
            throw new NotFoundException(NotFoundExceptionCode.CAR_NOT_FOUND);
        }
    }

    public Car addCar(Car car) {
        return carRepository.save(car);
    }

    public void deleteCar(Long id) {
        carRepository.delete(id);
    }

    public List<Car> findAllAviableCarsToLeaseOrBuy() {
        return carRepository.findAllByStatus(CarStatus.FOR_SALE);
    }

    public List<Car> findAllAvaibleCarsToRent() {
        return carRepository.findAllByStatus(CarStatus.FOR_RENT);
    }


    public Car updateCar(Car car, Long id) {
        car.setId(id);
        return carRepository.save(car);
    }

    public Car updateCarStatus(Long id, CarStatus status) {
        carRepository.updateStatus(id, status);
        return carRepository.findOne(id);
    }
}
