package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ReadingProgressTest {

    @Test
    void testInitializeTotalPages() {
        ReadingProgress progress = new ReadingProgress();

        progress.initializeTotalPages(300L);
        assertEquals(300L, progress.getTotalPages());

        progress.initializeTotalPages(null);
        assertEquals(0L, progress.getTotalPages());
    }

    @Test
    void testSetCurrentPage_ClampingLogic() {
        ReadingProgress progress = new ReadingProgress();
        progress.initializeTotalPages(100L);

        progress.setCurrentPage(50L);
        assertEquals(50L, progress.getCurrentPage());

        progress.setCurrentPage(-10L);
        assertEquals(0L, progress.getCurrentPage());

        progress.setCurrentPage(150L);
        assertEquals(100L, progress.getCurrentPage());
    }

    @Test
    void testSetCurrentPage_StatusUpdateLogic() {
        ReadingProgress progress = new ReadingProgress();
        progress.initializeTotalPages(100L);

        progress.setCurrentPage(50L);
        assertEquals(ReadingStatus.READING, progress.getStatus());

        progress.setCurrentPage(100L);
        assertEquals(ReadingStatus.FINISHED, progress.getStatus());

        progress.setCurrentPage(999L);
        assertEquals(ReadingStatus.FINISHED, progress.getStatus());
    }

    @Test
    void testGetProgressPercent() {
        ReadingProgress progress = new ReadingProgress();
        progress.initializeTotalPages(200L);

        progress.setCurrentPage(100L);
        assertEquals(50, progress.getProgressPercent());

        progress.initializeTotalPages(300L);
        progress.setCurrentPage(100L);
        assertEquals(33, progress.getProgressPercent());

        progress.setCurrentPage(200L);
        assertEquals(67, progress.getProgressPercent());
    }

    @Test
    void testGetProgressPercent_DivisionByZero() {
        ReadingProgress progress = new ReadingProgress();
        progress.initializeTotalPages(0L);

        int percent = progress.getProgressPercent();

        assertEquals(0, percent);
    }
}