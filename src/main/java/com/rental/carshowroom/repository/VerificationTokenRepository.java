package com.rental.carshowroom.repository;

import com.rental.carshowroom.model.VerificationToken;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends PagingAndSortingRepository<VerificationToken, Long> {
    VerificationToken findByToken(String VerificationToken);
    VerificationToken findByUser(String user);
}
