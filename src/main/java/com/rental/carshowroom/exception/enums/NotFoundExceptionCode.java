package com.rental.carshowroom.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NotFoundExceptionCode {
    CAR_NOT_FOUND("msg.validation.car.notfound");

    private String descriptionProperty;
}