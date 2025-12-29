package com.prj2.booksta.model.dto;

import java.util.List;

public record ResolveBookReport(
        String action,
        String isbn,
        Integer publishingYear,
        Long pages,
        String bookTitle,
        String description,
        List<Long> authors,
        List<Long> subjects
) {
}
