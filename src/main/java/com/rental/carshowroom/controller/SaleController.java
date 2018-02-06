package com.rental.carshowroom.controller;

import com.rental.carshowroom.service.CarService;
import com.rental.carshowroom.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/buy")
public class SaleController {
    private CarService carService;
    private SaleService saleService;

    @Autowired
    public SaleController(CarService carService, SaleService saleService) {
        this.carService = carService;
        this.saleService = saleService;
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> buyCar(@PathVariable Long id) {
        Map<String, String> errors = saleService.validateBuy(carService.getCar(id));
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok(saleService.buyCar(id));
    }
}
