package com.sladamos.book.validators;

import com.sladamos.book.Book;
import com.sladamos.book.BookStatus;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BorrowedByRequiredValidator implements ConstraintValidator<BorrowedByRequired, Book> {

    @Override
    public boolean isValid(Book book, ConstraintValidatorContext context) {
        if (book == null) return true;

        if (book.getStatus() == BookStatus.BORROWED && StringUtils.isEmpty(book.getBorrowedBy())) {

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("borrowedBy")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}