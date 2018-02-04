package com.rental.carshowroom.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rental.carshowroom.model.enums.SaleStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale extends Transaction {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Patterns.DATETIME)
    private LocalDateTime soldDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Patterns.DATETIME)
    private LocalDateTime requestedDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Patterns.DATETIME)
    private LocalDateTime receivedDate;
    @NotNull
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    @NotNull
    private SaleStatus status = SaleStatus.ORDERED;

    @Builder
    public Sale(Long id, Car car, User user, LocalDateTime soldDate, BigDecimal price, SaleStatus status, LocalDateTime requestedDate, LocalDateTime receivedDate) {
        super(id, car, user);
        this.soldDate = soldDate;
        this.price = price;
        this.status = status;
        this.requestedDate = requestedDate;
        this.receivedDate = receivedDate;
    }
}
