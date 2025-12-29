package app.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthTest {

    private final Auth auth = new Auth();

    @Test
    void testValidateForm_AllFieldsFilled_ShouldReturnTrue() {
        assertTrue(auth.validateForm("Alice", "123", "secret"), "Tous les champs remplis devraient renvoyer true");
    }

    @Test
    void testValidateForm_EmptyName_ShouldReturnFalse() {
        assertFalse(auth.validateForm("", "123", "secret"), "Nom vide devrait renvoyer false");
    }

    @Test
    void testValidateForm_EmptyId_ShouldReturnFalse() {
        assertFalse(auth.validateForm("Alice", "", "secret"), "ID vide devrait renvoyer false");
    }

    @Test
    void testValidateForm_EmptyPassword_ShouldReturnFalse() {
        assertFalse(auth.validateForm("Alice", "123", ""), "Mot de passe vide devrait renvoyer false");
    }

    @Test
    void testValidateForm_AllFieldsEmpty_ShouldReturnFalse() {
        assertFalse(auth.validateForm("", "", ""), "Tous les champs vides devraient renvoyer false");
    }
}

