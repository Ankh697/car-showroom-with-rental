package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        return carRepository.findOne(id);
    }

    private Car findCar(Long id) throws NotFoundException {
        Car car = carRepository.findOne(id);
        if (car != null) {
            return car;
        } else {
            throw new NotFoundException(NotFoundExceptionCode.CAR_NOT_FOUND);
        }
    }

    private boolean checkCarExist(Long id) throws NotFoundException {
        if (!carRepository.exists(id)) {
            throw new NotFoundException(NotFoundExceptionCode.CAR_NOT_FOUND);
        }
        return true;
    }


    public Car addCar(Car car) {
        return carRepository.save(car);
    }

    public void deleteCar(Long id) throws NotFoundException {
        checkCarExist(id);
        carRepository.delete(id);
    }

    public List<Car> findAllAviableCarsToLeaseOrBuy() {
        return carRepository.findAllByStatus(CarStatus.FOR_SALE);
    }

    public List<Car> findAllAvaibleCarsToRent() {
        return carRepository.findAllByStatus(CarStatus.FOR_RENT);
    }


    public Car updateCar(Car car, Long id) throws NotFoundException {
        checkCarExist(id);
        car.setId(id);
        return carRepository.save(car);
    }

    public Car updateStatus(CarStatus carStatus, Long id) {
        Car car = findCar(id);
        car.setStatus(carStatus);
        return carRepository.save(car);
    }

    public Map<String, String> validateDeleteOrUpdateCar(Long id) throws NotFoundException {
        Map<String, String> errors = new LinkedHashMap<>();
        findCar(id);
        return errors;
    }


}
