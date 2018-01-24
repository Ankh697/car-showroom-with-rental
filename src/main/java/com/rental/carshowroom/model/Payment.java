package com.rental.carshowroom.model;

import com.rental.carshowroom.model.enums.PaymentStatus;
import com.rental.carshowroom.model.enums.TransactionType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.WAITING;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime acceptedDate;
    private Double value;
    @ManyToOne(cascade = CascadeType.ALL)
    private Transaction transaction;
    private String accountNumber;
    private String factureNumber;
    @Transient
    private String companyName;
    @Transient
    private String address;
}
