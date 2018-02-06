package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Role;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.enums.RoleType;
import com.rental.carshowroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@PropertySource("classpath:validationmessages.properties")
public class UserService {
    private UserRepository userRepository;

    @Value("${msg.validation.user.notfound}")
    private String userNotFound;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        user.setRoles(Collections.singleton(new Role(RoleType.ROLE_USER)));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
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

    public User findUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException(userNotFound);
        }
    }

    public User getUser(Long id) throws NotFoundException {
        return findUser(id);
    }

    public void deleteUser(Long id) {
        userRepository.delete(id);
    }

    public boolean isProperUser(Long id) {
        return findUser(id).getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
