package com.rental.carshowroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public abstract class AbstractEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonIgnore
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonIgnore
    private LocalDateTime updatedAt = LocalDateTime.now();
}
