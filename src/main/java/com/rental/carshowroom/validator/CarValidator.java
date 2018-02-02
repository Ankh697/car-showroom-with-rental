package com.rental.carshowroom.validator;

import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.enums.CarStatus;
import org.springframework.stereotype.Service;

@Service
public class CarValidator {
    public boolean validateIfStatusCorrectForOperation(Car car, CarStatus status) {
        return status.equals(car.getStatus());
    }
}
