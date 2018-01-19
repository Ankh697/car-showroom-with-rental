package com.rental.carshowroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Data
@ToString
@EqualsAndHashCode
public class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    protected AbstractEntity() {
        this.id = null;
    }

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date createdAt = new Date();

    @LastModifiedDate
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date updatedAt = new Date();
}
