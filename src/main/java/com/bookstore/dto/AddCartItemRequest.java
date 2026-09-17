package com.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(

        @NotNull
        Long bookId,

        @NotNull
        @Min(1)
        Integer quantity
) {
}
