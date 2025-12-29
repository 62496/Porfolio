package app.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryExceptionTest {

    @Test
    void testRepositoryExceptionMessageAndCause() {
        Throwable cause = new RuntimeException("Cause de l'erreur");
        RepositoryException exception = new RepositoryException("Message personnalisé", cause);

        assertEquals("Message personnalisé", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}

