package com.prj2.booksta.model.dto;

import com.prj2.booksta.model.Author;

import java.util.List;

public record BookWithLatestReadingEvent(
        String isbn,
        String title,
        Integer publishingYear,
        Long pages,
        String imageUrl,
        BookReadingEvent latestReadingEvent
) {}
