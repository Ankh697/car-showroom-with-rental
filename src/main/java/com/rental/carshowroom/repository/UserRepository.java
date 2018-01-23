package com.rental.carshowroom.repository;

import com.rental.carshowroom.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    boolean existsByUsername(String username);
    List<User> findAll();
}
