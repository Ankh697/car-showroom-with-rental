package com.rental.carshowroom.controller;

import com.rental.carshowroom.model.User;
import com.rental.carshowroom.service.UserService;
import com.rental.carshowroom.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private UserService userService;
    private VerificationTokenService verificationTokenService;

    @Autowired
    public UserController(UserService userService, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> addUser(@RequestBody @Valid User user) {
        User addedUser = userService.addUser(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(addedUser.getId()).toUri();
        return ResponseEntity.created(location).body(addedUser);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> listAllUsers() {
        return ResponseEntity.ok(userService.listAllUsers());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and @userService.isProperUser(#id))")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody @Valid User user) {
        return ResponseEntity.ok(userService.updateUser(user, id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("(hasRole('ROLE_USER') and @userService.isProperUser(#id)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and @userService.isProperUser(#id))")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PostMapping("/registration")
    public ResponseEntity registerUserAccount(
            @RequestBody @Valid User user, HttpServletRequest request) {
        String appUrl = "http://" + request.getServerName() + ":" +
                request.getServerPort() + request.getContextPath();
        userService.register(user, appUrl);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/registration/confirm")
    public ResponseEntity<User> confirmRegistration(@RequestParam("token") String token) {
        User user = verificationTokenService.activateAccountWithToken(token);
        return ResponseEntity.ok(userService.updateUser(user, user.getId()));
    }
}

