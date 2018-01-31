package com.rental.carshowroom.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class Transaction extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TransactionIdGenerator")
    @TableGenerator(table = "SEQUENCES", name = "TransactionIdGenerator")
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @NotNull
    private Car car;
    @ManyToOne
    private User user;

    public Transaction(Long id, Car car, User user) {
        this.id = id;
        this.car = car;
        this.user = user;
    }
}
