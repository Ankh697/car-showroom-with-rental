package com.rental.carshowroom.validator;

import com.rental.carshowroom.annotation.NotPast;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

@Component
public class DateNotInPastValidator implements ConstraintValidator<NotPast, LocalDate> {

    @Override
    public void initialize(NotPast notPast) {
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        return date == null || !date.isBefore(LocalDate.now());
    }
}
