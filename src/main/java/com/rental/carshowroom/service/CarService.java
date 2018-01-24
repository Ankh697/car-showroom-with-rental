package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.model.Sale;
import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:validationmessages.properties")
public class CarService {
    private final String STATUS_KEY = "userStatus";

    @Value("${msg.validation.car.notforsale")
    private String carNotForSale;

    private PaymentService paymentService;
    private SaleService saleService;
    private CarRepository carRepository;

    @Autowired
    public CarService(PaymentService paymentService, SaleService saleService, CarRepository carRepository) {
        this.paymentService = paymentService;
        this.saleService = saleService;
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

    public Payment buyCar(Long id) {
        updateCarStatus(id, CarStatus.SOLD);
        Sale sale = saleService.prepareSale(findCar(id), null);
        return paymentService.preparePaymentForSale(sale);
    }

    public Car updateCarStatus(Long id, CarStatus status) {
        carRepository.updateStatus(id, status);
        return carRepository.findOne(id);
    }

    public Map<String, String> validateBuy(Long id) throws NotFoundException {
        Map<String, String> errors = new LinkedHashMap<>();
        if (validateIfStatusCorrectForOperation(findCar(id), CarStatus.FOR_SALE)) {
            errors.put(STATUS_KEY, carNotForSale);
        }
        return errors;
    }

    private boolean validateIfStatusCorrectForOperation(Car car, CarStatus status) {
        return status.equals(car.getStatus());
    }
}
