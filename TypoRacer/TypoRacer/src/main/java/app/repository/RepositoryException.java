package app.repository;
/**
 * Exception personnalisée utilisée pour gérer les erreurs liées aux accès en base de données.
 */
public class RepositoryException extends RuntimeException {
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
