package com.rental.carshowroom.repository;

import com.rental.carshowroom.model.Rent;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentRepository extends PagingAndSortingRepository<Rent, Long> {
    Optional<Rent> findById(Long id);
}
