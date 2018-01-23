package com.rental.carshowroom.model;


import com.rental.carshowroom.model.enums.LeasingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "leasings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leasing extends AbstractEntity {

    @ManyToOne
    private Car car;

    @Min(1)
    @NotNull
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
    private Long installment;


}
