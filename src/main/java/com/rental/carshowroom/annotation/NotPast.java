package com.rental.carshowroom.annotation;


import com.rental.carshowroom.validator.DateNotInPastValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = DateNotInPastValidator.class)
@Documented
public @interface NotPast {
    String message() default "{msg.validation.date.notpast}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
