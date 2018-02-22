package com.rental.carshowroom.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.rental.carshowroom.annotation.NotPast;
import com.rental.carshowroom.model.enums.LeasingStatus;
import com.rental.carshowroom.validator.groups.CalculateLeasingValidationGroup;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "leasings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leasing extends Transaction {

    @Min(1)
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal pricePerOneInstallment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Patterns.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    @NotPast
    private LocalDate expectedStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Patterns.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Patterns.DATETIME)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime returnDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Patterns.DATETIME)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @Enumerated(EnumType.STRING)
    private LeasingStatus status = LeasingStatus.WAITING;

    @Min(value = 24, groups = {CalculateLeasingValidationGroup.class})
    @NotNull(groups = {CalculateLeasingValidationGroup.class})
    private Long installments;

    @Min(value = 0, groups = {CalculateLeasingValidationGroup.class})
    private Long installmentsPaid;

    @Min(value = 0, groups = {CalculateLeasingValidationGroup.class})
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal initialPayment;


    @Builder
    public Leasing(Long id, Car car, User user, BigDecimal pricePerOneInstallment, LocalDate expectedStartDate, LocalDate endDate, LocalDateTime returnDate, LocalDateTime startDate, LeasingStatus status, Long installments, Long installmentsPaid, BigDecimal initialPayment) {
        super(id, car, user);
        this.pricePerOneInstallment = pricePerOneInstallment;
        this.expectedStartDate = expectedStartDate;
        this.endDate = endDate;
        this.returnDate = returnDate;
        this.startDate = startDate;
        this.status = status;
        this.installments = installments;
        this.installmentsPaid = installmentsPaid;
        this.initialPayment = initialPayment;
    }
}
