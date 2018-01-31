package com.rental.carshowroom.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rental.carshowroom.annotation.NotPast;
import com.rental.carshowroom.model.enums.RentStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "rents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rent extends Transaction {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Patterns.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotPast
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Patterns.DATE)
    private LocalDate endDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Patterns.DATETIME)
    private LocalDateTime returnDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Patterns.DATETIME)
    private LocalDateTime borrowDate;
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal costPerDay;
    private Integer penalty;
    @Enumerated(EnumType.STRING)
    private RentStatus status = RentStatus.RESERVED;

    @AssertTrue(message = "{msg.validation.date.endbeforestart}")
    private boolean isValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }

    @Builder
    public Rent(Long id, Car car, User user, LocalDate startDate, LocalDate endDate, LocalDateTime returnDate, LocalDateTime borrowDate, BigDecimal costPerDay, Integer penalty, RentStatus status) {
        super(id, car, user);
        this.startDate = startDate;
        this.endDate = endDate;
        this.returnDate = returnDate;
        this.borrowDate = borrowDate;
        this.costPerDay = costPerDay;
        this.penalty = penalty;
        this.status = status;
    }
}
