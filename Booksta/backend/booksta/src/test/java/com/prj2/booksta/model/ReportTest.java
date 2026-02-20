package com.prj2.booksta.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ReportTest {

    @Test
    void testDefaultStatusIsPending() {
        Report report = new Report();
        assertEquals(ReportStatus.PENDING, report.getReportStatus(),
                "Le statut par défaut doit être PENDING");
    }

    @Test
    void testSettersAndGetters() {
        Report report = new Report();
        User user = new User();
        user.setEmail("reporter@test.com");

        report.setUser(user);
        report.setSubject("Problème");
        report.setMessageReport("Contenu offensant");
        report.setReportStatus(ReportStatus.RESOLVED);

        assertEquals(user, report.getUser());
        assertEquals("Problème", report.getSubject());
        assertEquals("Contenu offensant", report.getMessageReport());
        assertEquals(ReportStatus.RESOLVED, report.getReportStatus());
    }
}