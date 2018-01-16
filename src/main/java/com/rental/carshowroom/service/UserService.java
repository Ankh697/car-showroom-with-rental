package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:validation_messages.properties")
public class UserService {
    private final String USERNAME_KEY = "username";

    @Value("msg.validation.user.username.notunique")
    private String usernameNotUnique;

    @Value("msg.validation.user.username.noteditable")
    private String usernameNotEditable;

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public Map<String, String> validateAddUser(User user) {
        Map<String, String> errors = Collections.emptyMap();
        validateUsernameEquality(user, errors);
        return errors;
    }

    private void validateUsernameEquality(User user, Map<String, String> errors) {
        if (userRepository.existsByUsername(user.getUsername())) {
            errors.put(USERNAME_KEY, usernameNotUnique);
        }
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user, Long id) {
        user.setId(id);
        return userRepository.save(user);
    }

    public Map<String, String> validateUpdateUser(User user, Long id) {
        Map<String, String> errors = Collections.emptyMap();
        validateUsernameEdited(user, id, errors);
        return errors;
    }

    private void validateUsernameEdited(User user, Long id, Map<String, String> errors) throws NotFoundException {
        if(!user.getUsername().equals(findUser(id).getUsername())) {
            errors.put(USERNAME_KEY, usernameNotEditable);
        }
    }

    private User findUser(Long id) throws NotFoundException {
        User user = userRepository.findOne(id);
        if(user != null) {
            return user;
        }
        else {
            throw new NotFoundException(NotFoundExceptionCode.USER_NOT_FOUND);
        }
    }

    public User getUser(Long id) throws NotFoundException {
        return findUser(id);
    }

    public Map<String, String> validateDeleteUser(Long id) throws NotFoundException {
        Map<String, String> errors = Collections.emptyMap();
        findUser(id); //will be changed after implement security
        return errors;
    }

    public void deleteUser(Long id) {
        userRepository.delete(id);
    }
}
