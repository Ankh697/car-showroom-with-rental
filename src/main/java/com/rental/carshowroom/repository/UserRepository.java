package com.rental.carshowroom.repository;

import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.enums.UserStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    List<User> findAll();

    @Query("UPDATE User u SET u.nameAndSurname = ?#{#nameAndSurname}, u.pesel = ?#{#pesel}, u.status = ?#{#status} WHERE u.id = ?#{#id}")
    @Modifying
    @Transactional
    void updateUser(Long id, String nameAndSurname, String pesel, UserStatus status);
}
