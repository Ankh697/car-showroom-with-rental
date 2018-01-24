package com.rental.carshowroom.exception;

import com.rental.carshowroom.exception.enums.NotFoundExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotFoundException extends RuntimeException {
    private NotFoundExceptionCode code;
}
