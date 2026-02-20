package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AuthorReportTest {

    @Test
    void testSetAndGetAuthor() {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("Victor");
        author.setLastName("Hugo");

        AuthorReport report = new AuthorReport();

        report.setAuthor(author);

        assertNotNull(report.getAuthor());
        assertEquals("Victor", report.getAuthor().getFirstName());
        assertEquals(author, report.getAuthor());
    }

    @Test
    void testInheritanceAndType() {
        AuthorReport report = new AuthorReport();
        assertTrue(report instanceof Report, "AuthorReport doit hériter de Report");
    }
}