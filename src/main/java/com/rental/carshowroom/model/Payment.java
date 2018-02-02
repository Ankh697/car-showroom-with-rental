package com.rental.carshowroom.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rental.carshowroom.model.enums.PaymentStatus;
import com.rental.carshowroom.model.enums.TransactionType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Patterns.DATETIME)
    private LocalDateTime acceptedDate;
    @NotNull
    @NumberFormat(style= NumberFormat.Style.CURRENCY)
    private BigDecimal value;
    @ManyToOne(cascade = CascadeType.MERGE)
    private Transaction transaction;
    @NotNull
    private String accountNumber;
    private String factureNumber;
    @Transient
    private String companyName;
    @Transient
    private String address;
}
