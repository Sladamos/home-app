package com.sladamos.book.validators;

import com.sladamos.book.model.BookEntity;
import com.sladamos.book.model.BookStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.ObjectUtils;

public class BorrowedByRequiredValidator implements ConstraintValidator<BorrowedByRequired, BookEntity> {

    @Override
    public boolean isValid(BookEntity book, ConstraintValidatorContext context) {
        if (book == null) return true;

        if (book.getStatus() == BookStatus.BORROWED && ObjectUtils.isEmpty(book.getBorrowedBy())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("borrowedBy")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}