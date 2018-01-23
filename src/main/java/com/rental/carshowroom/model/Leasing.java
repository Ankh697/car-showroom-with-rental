/*
package com.rental.carshowroom.model;


import com.rental.carshowroom.model.enums.LeasingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity(name = "leasing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leasing {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Car car;

    @Min(1)
    @NotNull
    private Long pricePerOneInstallment;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Pattern(regexp = Patterns.DATE) //TODO MESSAGE
    private LocalDateTime startOfLease;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Pattern(regexp = Patterns.DATE) //TODO MESSAGE
    private LocalDateTime endOfLease;

    @Enumerated(EnumType.STRING)
    @NotNull
    private LeasingStatus leasingStatus = LeasingStatus.WAITING;


}
*/
