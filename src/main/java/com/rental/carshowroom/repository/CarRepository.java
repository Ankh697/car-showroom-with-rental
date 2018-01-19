package com.rental.carshowroom.repository;

import com.rental.carshowroom.model.Car;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends PagingAndSortingRepository<Car, Long> {
    List<Car> findAll();

}
