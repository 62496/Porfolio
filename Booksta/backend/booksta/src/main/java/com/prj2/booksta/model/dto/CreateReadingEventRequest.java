package com.prj2.booksta.model.dto;

import com.prj2.booksta.model.ReadingEventType;

public record CreateReadingEventRequest(
        ReadingEventType eventType
) {}
