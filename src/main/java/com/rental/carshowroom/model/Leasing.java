package com.rental.carshowroom.model;


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

@EqualsAndHashCode(callSuper = true)
@Entity(name = "leasings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leasing extends Transaction {

    @NotNull
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal pricePerOneInstallment;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate startOfLease;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate endOfLease;

    @Enumerated(EnumType.STRING)
    @NotNull
    private LeasingStatus leasingStatus = LeasingStatus.WAITING;

    @Min(24)
    @NotNull
    private Long installments;


}
