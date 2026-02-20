package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BookReportTest {

    @Test
    void testSetAndGetBook() {
        Book book = new Book();
        book.setIsbn("978-3-16-148410-0");
        book.setTitle("Le Petit Prince");

        BookReport report = new BookReport();

        report.setBook(book);

        assertNotNull(report.getBook());
        assertEquals("Le Petit Prince", report.getBook().getTitle());
        assertEquals(book, report.getBook());
    }

    @Test
    void testInheritanceAndType() {
        BookReport report = new BookReport();
        assertTrue(report instanceof Report, "BookReport doit hériter de la classe Report");
    }
}