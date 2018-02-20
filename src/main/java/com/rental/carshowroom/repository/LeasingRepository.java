package com.rental.carshowroom.repository;

import com.rental.carshowroom.model.Leasing;
import com.rental.carshowroom.model.enums.LeasingStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeasingRepository extends PagingAndSortingRepository<Leasing, Long> {
    List<Leasing> findAll();

    List<Leasing> findAllByLeasingStatus(LeasingStatus status);

    @Query("SELECT l from leasings l WHERE (l.startDate BETWEEN ?1 AND ?2) OR (l.endDate BETWEEN ?1 AND ?2)")
    List<Leasing> findAllLeaseBetweenDates(LocalDate startDate, LocalDate endDate);

    Optional<Leasing> findById(Long id);
}
