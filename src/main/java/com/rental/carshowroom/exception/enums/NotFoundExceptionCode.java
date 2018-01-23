package com.rental.carshowroom.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NotFoundExceptionCode {
    CAR_NOT_FOUND("msg.validation.car.notfound"),
    USER_NOT_FOUND("msg.validation.user.notfound"),
    LEASING_NOT_FOUND("msg.validation.leasing.notfound"),
    PAYMENT_NOT_FOUND("msg.validation.payment.notfound"),
    SALE_NOT_FOUND("msg.validation.sale.notfound");

    private String descriptionProperty;

}
