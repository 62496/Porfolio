package com.prj2.booksta.model.dto;

import com.prj2.booksta.model.ReadingEventType;

import java.time.Instant;

public record BookReadingEvent (
        ReadingEventType eventType,
        Instant occurredAt
) {}