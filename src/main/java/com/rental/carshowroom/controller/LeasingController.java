package com.rental.carshowroom.controller;

import com.rental.carshowroom.model.Leasing;
import com.rental.carshowroom.model.Payment;
import com.rental.carshowroom.service.CarService;
import com.rental.carshowroom.service.LeasingService;
import com.rental.carshowroom.util.LeasingUtil;
import com.rental.carshowroom.validator.groups.CalculateLeasingValidationGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leasing")
public class LeasingController {

    private LeasingService leasingService;
    private CarService carService;

    @Autowired
    public LeasingController(LeasingService leasingService, CarService carService) {
        this.leasingService = leasingService;
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<Leasing>> showAll() {
        return ResponseEntity.ok(leasingService.showAllLeasings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Leasing> getLeasingById(@PathVariable Long id) {
        return ResponseEntity.ok(leasingService.findLeasing(id));
    }

    @PostMapping
    public ResponseEntity<?> addLeasing(@RequestBody @Valid Leasing leasing) {
        Map<String, String> errors = leasingService.validateLeasing(leasing);
        if(errors.isEmpty()){
            return ResponseEntity.badRequest().body(errors);
        }
        Payment payment = leasingService.addLeasing(leasing);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(payment.getTransaction().getId()).toUri();
        return ResponseEntity.created(location).body(payment);
    }

    @DeleteMapping
    public ResponseEntity deleteLeasing(@PathVariable Long id)
    {
        leasingService.deleteLeasing(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Leasing> updateLeasing(@PathVariable Long id, @RequestBody @Valid Leasing leasing) {
        return ResponseEntity.ok(leasingService.updateLeasing(leasing, id));
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<Leasing> editLeasingStatus(@RequestBody @Valid Leasing leasing, Long id) {
        return ResponseEntity.ok(leasingService.updateLeasingStatus(leasing.getLeasingStatus(), id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Leasing>> getLeasingsBetweenTwoDates(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                              @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(leasingService.listAllLeasingBetweenTwoDates(start, end));
    }

    @PostMapping("/calculate")
    public ResponseEntity<BigDecimal> calculateLeasing(@RequestBody @Validated({CalculateLeasingValidationGroup.class}) Leasing leasing)
    {
        return ResponseEntity.ok(LeasingUtil.calculateLeasing(leasing, carService.getCar(leasing.getCar().getId())));
    }
}
