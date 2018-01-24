package com.rental.carshowroom.service;

import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.Sale;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.enums.SaleStatus;
import com.rental.carshowroom.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SaleService {
    private SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public Sale prepareSale(Car car, User user) {
        return saleRepository.save(Sale.builder()
                .price(car.getPriceBrutto())
                .requestedDate(LocalDateTime.now())
                .status(SaleStatus.ORDERED)
                .car(car)
                .user(user)
                .build());
    }
}
