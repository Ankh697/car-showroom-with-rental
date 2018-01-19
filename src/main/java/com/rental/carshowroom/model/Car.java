package com.rental.carshowroom.model;


import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.Petrol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car extends AbstractEntity {
    private String model;
    private Integer productionYear;
    private Long whichOwner;
    private Long mileage;

    @Pattern(regexp ="/^(?<wmi>[A-HJ-NPR-Z\\d]{3})(?<vds>[A-HJ-NPR-Z\\d]{5})(?<check>[\\dX])(?<vis>(?<year>[A-HJ-NPR-Z\\d])(?<plant>[A-HJ-NPR-Z\\d])(?<seq>[A-HJ-NPR-Z\\d]{6}))$/", message = "Invalid VIN!")
    private String VIN;
    @NotNull
    private String registrationNumber;

    private String color;
    @NotNull
    @Pattern(regexp = "\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?", message = "Type the price netto!")
    private Long priceNetto;
    @NotNull
    @Pattern(regexp = "\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?", message = "Type the price brutto!")
    private Long priceBrutto;

    @NotNull
    private Long engineCapacity;

    @NotNull
    private Long enginePower;

    @Enumerated(EnumType.STRING)
    @NotNull
    private CarStatus status;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Petrol petrol;




}