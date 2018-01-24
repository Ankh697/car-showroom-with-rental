package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PropertySource("classpath:validationmessages.properties")
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user, Long id) {
        userRepository.updateUser(id, user.getNameAndSurname(), user.getPesel(), user.getStatus());
        return userRepository.findOne(id);
    }

    private User findUser(Long id) throws NotFoundException {
        User user = userRepository.findOne(id);
        if (user != null) {
            return user;
        } else {
            throw new NotFoundException(NotFoundExceptionCode.USER_NOT_FOUND);
        }
    }

    public User getUser(Long id) throws NotFoundException {
        return findUser(id);
    }

    public void deleteUser(Long id) {
        userRepository.delete(id);
    }
}
