package com.prj2.booksta.model.dto;

import jakarta.validation.constraints.NotNull;

public record ReadingSessionUpdate (
        Integer startPage,

        @NotNull(message = "End page is required")
        Integer endPage,

        String note
) {}
