package com.rental.carshowroom.repository;

import com.rental.carshowroom.model.VerificationToken;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends PagingAndSortingRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
}
