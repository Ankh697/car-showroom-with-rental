package com.rental.carshowroom.repository;

import com.rental.carshowroom.model.Car;
import com.rental.carshowroom.model.enums.CarStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CarRepository extends PagingAndSortingRepository<Car, Long> {
    List<Car> findAll();

    @Query("UPDATE cars c SET c.status = :status WHERE c.id = :id")
    @Transactional
    @Modifying
    void updateStatus(@Param("id") Long id, @Param("status") CarStatus status);

    List<Car> findAllByStatus(CarStatus carStatus);
}
