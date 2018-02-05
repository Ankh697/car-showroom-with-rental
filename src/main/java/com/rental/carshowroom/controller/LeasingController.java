package com.rental.carshowroom.controller;

import com.rental.carshowroom.model.Leasing;
import com.rental.carshowroom.model.enums.LeasingStatus;
import com.rental.carshowroom.service.LeasingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/leasing")
public class LeasingController {

    private LeasingService leasingService;

    @Autowired
    public LeasingController(LeasingService leasingService) {
        this.leasingService = leasingService;
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
    public ResponseEntity<Leasing> addLeasing(@RequestBody @Valid Leasing leasing) {
        Leasing addLeasing = leasingService.addLeasing(leasing);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(addLeasing.getId()).toUri();
        return ResponseEntity.created(location).body(addLeasing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLeasing(@PathVariable Long id)
    {
        leasingService.deleteLeasing(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Leasing> updateLeasing(@PathVariable Long id, @RequestBody @Valid Leasing leasing) {
        return ResponseEntity.ok(leasingService.updateLeasing(leasing, id));
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<Leasing> editLeasingStatus(@RequestBody @Valid Leasing leasing, Long id) {
        return ResponseEntity.ok(leasingService.updateLeasingStatus(leasing.getLeasingStatus(), id));
    }
//TODO connect searching method
    @GetMapping("/search")
    public ResponseEntity<List<Leasing>> getLeasingsBetweenTwoDates(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                              @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(leasingService.listAllLeasingBetweenTwoDates(start, end));
    }

    @GetMapping("/status")
    public ResponseEntity<List<Leasing>> getLeasingsByStatus(@RequestParam("status") LeasingStatus leasingStatus) {
        return ResponseEntity.ok(leasingService.findAllLeasingsByStatus(leasingStatus));
    }

    @PostMapping("/calculate")
    public ResponseEntity<BigDecimal> calculateLeasing(@RequestBody Leasing leasing)
    {
        return ResponseEntity.ok(leasingService.calculateLeasing(leasing));
    }
}
