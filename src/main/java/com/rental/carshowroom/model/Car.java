package com.rental.carshowroom.model;


import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.Petrol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car extends AbstractEntity {
    @NotBlank
    private String model;
    @Min(1900)
    private Integer productionYear;
    @Min(0)
    private Long whichOwner;
    @Min(0)
    private Long mileage;

    @NotNull
    @Pattern(regexp = Patterns.VIN, message = "{msg.validation.car.vin.pattern}")
    private String vin;
    @NotBlank
    private String numberPlate;

    @NotBlank
    private String color;

    @Min(0)
    @NotNull
    private Long priceNetto;

    @Min(0)
    @NotNull
    private Long priceBrutto;

    @NotNull
    private Long engineCapacity;

    @NotNull
    private Long enginePower;

    @Enumerated(EnumType.STRING)
    @NotNull
    private CarStatus status = CarStatus.DISACTIVE;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Petrol petrol;



}