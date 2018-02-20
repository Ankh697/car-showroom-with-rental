package com.rental.carshowroom.service;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import com.rental.carshowroom.model.Role;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.enums.RoleType;
import com.rental.carshowroom.model.enums.UserStatus;
import com.rental.carshowroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@PropertySource("classpath:validationmessages.properties")
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private VerificationTokenService verificationTokenService;

    @Value("${msg.validation.user.notfound}")
    private String userNotFound;

    @Autowired
    public UserService(UserRepository userRepository, VerificationTokenService verificationTokenService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.verificationTokenService = verificationTokenService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User addUser(User user) {
        user.setStatus(UserStatus.ACTIVE);
        return saveNewUser(user);
    }

    public void register(User user, String appUrl) {
        user.setStatus(UserStatus.INACTIVE);
        saveNewUser(user);
        verificationTokenService.sendConfirmationEmail(user, appUrl);
    }

    private User saveNewUser(User user) {
        user.setRoles(Collections.singleton(new Role(RoleType.ROLE_USER)));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user, Long id) {
        User userInDb = findUser(id);
        userInDb.setEmail(user.getEmail());
        userInDb.setStatus(user.getStatus());
        userInDb.setPesel(user.getPesel());
        userInDb.setNameAndSurname(user.getNameAndSurname());
        return userRepository.save(userInDb);
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(NotFoundExceptionCode.USER_NOT_FOUND));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(NotFoundExceptionCode.USER_NOT_FOUND));
    }

    public User getUser(Long id) {
        return findUser(id);
    }

    public void deleteUser(Long id) {
        userRepository.delete(id);
    }

    public boolean isProperUser(Long id) {
        return findUser(id).getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}

