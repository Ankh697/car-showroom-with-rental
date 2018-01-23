package com.rental.carshowroom.repository;

import com.rental.carshowroom.model.Leasing;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeasingRepository extends PagingAndSortingRepository<Leasing, Long> {
    List<Leasing> findAll();

    List<Leasing> findAllByLeasingStatus();

    List<Leasing> findAllByStartOfLeaseBetween(LocalDate start, LocalDate end);
}
