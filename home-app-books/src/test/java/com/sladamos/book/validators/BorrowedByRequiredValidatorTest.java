package com.sladamos.book.validators;

import com.sladamos.book.Book;
import com.sladamos.book.BookStatus;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.Test;

import static jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

class BorrowedByRequiredValidatorTest {

    private final BorrowedByRequiredValidator validator = new BorrowedByRequiredValidator();

    @Test
    void shouldMarkBookAsInvalidWhenBookIsBorrowedAndBorrowedByIsEmpty() {
        Book book = Book.builder().status(BookStatus.BORROWED).build();
        String defaultMessage = "Field 'borrowedBy' must not be blank when status is BORROWED";

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        NodeBuilderCustomizableContext nodeBuilderCustomizableContext = mock(NodeBuilderCustomizableContext.class);
        when(context.getDefaultConstraintMessageTemplate()).thenReturn(defaultMessage);
        when(context.buildConstraintViolationWithTemplate(defaultMessage)).thenReturn(builder);
        when(builder.addPropertyNode("borrowedBy")).thenReturn(nodeBuilderCustomizableContext);

        boolean isValid = validator.isValid(book, context);

        assertAll(
                () -> assertThat(isValid).isFalse(),
                () -> verify(context).disableDefaultConstraintViolation(),
                () -> verify(context).buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()),
                () -> verify(builder).addPropertyNode("borrowedBy"),
                () -> verify(nodeBuilderCustomizableContext).addConstraintViolation()
        );
    }

    @Test
    void shouldMarkBookAsValidWhenBookIsBorrowedAndBorrowedByIsNotEmpty() {
        Book book = Book.builder()
                .status(BookStatus.ON_SHELF)
                .borrowedBy("RandomPerson")
                .build();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        boolean isValid = validator.isValid(book, context);

        assertThat(isValid).isTrue();
    }

    @Test
    void shouldMarkBookAsValidWhenBookIsNotBorrowed() {
        Book book = Book.builder().status(BookStatus.ON_SHELF).build();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        boolean isValid = validator.isValid(book, context);

        assertThat(isValid).isTrue();
    }

    @Test
    void shouldMarkBookAsValidWhenBookIsSetToNull() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        boolean isValid = validator.isValid(null, context);

        assertThat(isValid).isTrue();
    }

}