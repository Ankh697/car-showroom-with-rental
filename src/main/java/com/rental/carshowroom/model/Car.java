package com.rental.carshowroom.model;


import com.rental.carshowroom.model.enums.CarStatus;
import com.rental.carshowroom.model.enums.Petrol;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
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
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal priceNetto;
    @Min(0)
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal priceBrutto;
    @Min(0)
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal rentCostPerDay;
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