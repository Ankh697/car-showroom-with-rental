package com.rental.carshowroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@ToString
@EqualsAndHashCode
public class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id;

    protected AbstractEntity() {
        this.id = null;
    }

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    @JsonIgnore
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "updated_at")
    @JsonIgnore
    private LocalDateTime updatedAt = LocalDateTime.now();
}
