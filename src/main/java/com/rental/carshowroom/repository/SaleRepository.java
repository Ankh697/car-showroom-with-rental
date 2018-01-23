package com.rental.carshowroom.repository;

import com.rental.carshowroom.model.Sale;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends PagingAndSortingRepository<Sale, Long> {

}
