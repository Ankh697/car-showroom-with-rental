package com.rental.carshowroom.repository;

import com.rental.carshowroom.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    boolean existsByUsername(String username);
    List<User> findAll();
}
