package com.prj2.booksta.repository.projections;
import com.prj2.booksta.model.ReadingEventType;
import java.time.Instant;

public interface BookWithLatestReadingEventView {
    String getIsbn();
    String getTitle();
    Integer getPublishingYear();
    Long getPages();
    String getImageUrl();
    ReadingEventType getLatestEventType();
    Instant getLatestEventOccurredAt();
}
