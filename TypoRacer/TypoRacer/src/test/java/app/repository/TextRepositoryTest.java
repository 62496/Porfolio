package app.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import app.dto.Texte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Tests unitaires pour TextRepository
 */
class TextRepositoryTest {

    private TextDao textDao;
    private TextRepository textRepository;
    private Texte texte1;
    private Texte texte2;

    @BeforeEach
    void setUp() {
        texte1 = new Texte(1, "Titre1", "Contenu1", "Auteur1");
        texte2 = new Texte(2, "Titre2", "Contenu2", "Auteur2");

        // Créer le mock avant instanciation de l'objet testé
        textDao = mock(TextDao.class);

        // Configure les comportements des mocks avant création
        when(textDao.findAll()).thenReturn(Arrays.asList(texte1, texte2));

        // Instancie la classe testée avec le mock

        textRepository = new TextRepository();
        try {
            java.lang.reflect.Field textDaoField = TextRepository.class.getDeclaredField("textDao");
            textDaoField.setAccessible(true);
            textDaoField.set(textRepository, textDao);
        } catch (Exception e) {
            fail("Impossible d'injecter le mock TextDao: " + e.getMessage());
        }
    }

    @Test
    void testGetTextes() throws SQLException {
        System.out.println("testGetTextes");
        // Arrange
        List<Texte> expected = Arrays.asList(texte1, texte2);

        // Action
        List<Texte> result = textRepository.getTextes();

        // Assert
        assertEquals(expected, result, "Les textes retournés devraient correspondre à ceux du mock");
        verify(textDao, times(1)).findAll();
    }
    @Test
    void testGetAllTitres() throws SQLException {
        // Arrange
        List<String> expectedTitres = Arrays.asList("Titre1", "Titre2");
        when(textDao.getAllTitres()).thenReturn(expectedTitres);

        // Action
        List<String> result = textRepository.getAllTitres();

        // Assert
        verify(textDao).getAllTitres();
        assertEquals(expectedTitres, result);
    }

    @Test
    void testAddTexte() throws SQLException {
        System.out.println("testAddTexte");
        // Arrange
        Texte nouveauTexte = new Texte(3, "NouveauTitre", "NouveauContenu", "NouvelAuteur");

        // Action
        textRepository.addTexte(nouveauTexte);

        // Assert
        verify(textDao, times(1)).insert(nouveauTexte);
    }
}

