package app.model;

import app.dto.Texte;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TexteTest {

    @Test
    void testTexteCreationAndAccessors() {
        Texte texte = new Texte(1, "Titre Test", "Contenu du texte", "Auteur");

        assertEquals(1, texte.id());
        assertEquals("Titre Test", texte.title());
        assertEquals("Contenu du texte", texte.content());
        assertEquals("Auteur", texte.creator());
    }

    @Test
    void testToStringReturnsTitle() {
        Texte texte = new Texte(1, "Un Titre", "Contenu", "Auteur");
        assertEquals("Un Titre", texte.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Texte texte1 = new Texte(1, "Titre", "Contenu", "Auteur");
        Texte texte2 = new Texte(1, "Titre", "Contenu", "Auteur");

        assertEquals(texte1, texte2);
        assertEquals(texte1.hashCode(), texte2.hashCode());
    }
}

