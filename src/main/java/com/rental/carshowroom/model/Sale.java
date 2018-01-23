package com.rental.carshowroom.model;

import com.rental.carshowroom.model.enums.SaleStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale extends Transaction {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime soldDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime requestedDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime receivedDate;
    @NotNull
    private Double price;
    @NotNull
    @Enumerated(EnumType.STRING)
    private SaleStatus status = SaleStatus.ORDERED;

    @Builder
    public Sale(Long id, Car car, User user, LocalDateTime soldDate, Double price, SaleStatus status, LocalDateTime requestedDate, LocalDateTime receivedDate) {
        super(id, car, user);
        this.soldDate = soldDate;
        this.price = price;
        this.status = status;
        this.requestedDate = requestedDate;
        this.receivedDate = receivedDate;
    }
}
