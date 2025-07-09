package com.sladamos.book.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BorrowedByRequiredValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BorrowedByRequired {
    String message() default "Field 'borrowedBy' must not be blank when status is BORROWED";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
