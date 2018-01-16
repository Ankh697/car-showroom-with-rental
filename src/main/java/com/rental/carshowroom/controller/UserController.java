package com.rental.carshowroom.controller;

import com.rental.carshowroom.exception.NotFoundException;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody @Valid User user) {
        Map<String, String> errors = userService.validateAddUser(user);
        if (errors.isEmpty()) {
            return ResponseEntity.ok(userService.addUser(user));
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @GetMapping
    public ResponseEntity<List<User>> listAllUsers() {
        return ResponseEntity.ok(userService.listAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid User user) throws NotFoundException {
        Map<String, String> errors = userService.validateUpdateUser(user, id);
        if (errors.isEmpty()) {
            return ResponseEntity.ok(userService.updateUser(user, id));
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCar(@PathVariable Long id) throws NotFoundException {
        Map<String, String> errors = userService.validateDeleteUser(id);
        if (errors.isEmpty()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } else return ResponseEntity.badRequest().body(errors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(userService.getUser(id));
    }
}
