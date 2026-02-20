package com.prj2.booksta.model.dto;

public record ReadingSessionCreate (
        String isbn,
        Integer startPage
) {
}
