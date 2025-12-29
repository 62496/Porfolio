package com.prj2.booksta.model.dto;

import java.util.List;

public record UpdateBook(
        Integer publishingYear,
        Long pages,
        String bookTitle,
        String description,
        List<Long> authors,
        List<Long> subjects,
        String imageUrl
) {
}
