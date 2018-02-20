package com.rental.carshowroom.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.rental.carshowroom.annotation.NotPast;
import com.rental.carshowroom.model.enums.LeasingStatus;
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
@Builder
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
    private LeasingStatus leasingStatus = LeasingStatus.WAITING;

    @Min(value = 24, groups = {CalculateLeasingValidationGroup.class})
    @NotNull(groups = {CalculateLeasingValidationGroup.class})
    private Long installments;

    @Min(value = 0, groups = {CalculateLeasingValidationGroup.class})
    private Long installmentsPaid;

    @Min(value = 0, groups = {CalculateLeasingValidationGroup.class})
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal initialPayment;

    public interface CalculateLeasingValidationGroup {}
}
