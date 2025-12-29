package app.model;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tests unitaires pour OcrModel
 */
class OcrModelTest {

    private OcrModel ocrModel;
    private Tesseract mockTesseract;

    @BeforeEach
    void setUp() {
        System.out.println("setUp");
        // Créer l'instance à tester
        ocrModel = new OcrModel();

        // Remplacer la vraie instance de Tesseract par un mock
        mockTesseract = mock(Tesseract.class);
        try {
            java.lang.reflect.Field tesseractField = OcrModel.class.getDeclaredField("tesseract");
            tesseractField.setAccessible(true);
            tesseractField.set(ocrModel, mockTesseract);
        } catch (Exception e) {
            fail("Erreur lors de l'injection du mock: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("scan() devrait retourner un message d'erreur si aucun fichier n'est sélectionné")
    void testScanNoFileSelected() throws TesseractException {
        System.out.println("testScanNoFileSelected");
        // Arrange - fileToScan est null par défaut

        // Action
        String result = ocrModel.scan();

        // Assert
        assertEquals("Erreur : aucune image sélectionnée.", result,
                "Le message d'erreur pour aucun fichier sélectionné devrait être retourné");
        // Vérifier qu'aucune interaction n'a eu lieu avec Tesseract
        verifyNoInteractions(mockTesseract);
    }

    @Test
    @DisplayName("scan() devrait configurer correctement Tesseract et appeler doOCR")
    void testScanWithFile() throws TesseractException {
        System.out.println("testScanWithFile");
        // Arrange
        String imagePath = "test-image.png";
        ocrModel.setFileToScan(imagePath);

        // Configurer le mock pour retourner un texte extrait
        when(mockTesseract.doOCR(any(File.class))).thenReturn("Texte extrait de l'image");

        // Action
        String result = ocrModel.scan();

        // Assert
        assertEquals("Texte extrait de l'image", result,
                "Le résultat devrait être le texte extrait de l'image");

        // Vérifier que Tesseract a été correctement configuré
        verify(mockTesseract).setLanguage("fra");
        verify(mockTesseract).doOCR(any(File.class));
    }

    @Test
    @DisplayName("scan() devrait gérer les exceptions TesseractException")
    void testScanWithTesseractException() throws TesseractException {
        System.out.println("testScanWithTesseractException");
        // Arrange
        String imagePath = "test-image.png";
        ocrModel.setFileToScan(imagePath);

        // Configurer le mock pour lancer une exception
        when(mockTesseract.doOCR(any(File.class))).thenThrow(new TesseractException());

        // Action
        String result = ocrModel.scan();

        // Assert
        assertEquals("Erreur d'extraction OCR", result,
                "Le message d'erreur pour une exception Tesseract devrait être retourné");
    }

    @Test
    @DisplayName("setFileToScan() devrait définir correctement le chemin du fichier")
    void testSetFileToScan() {
        System.out.println("testSetFileToScan");
        // Arrange
        String imagePath = "test-image.png";
        Path expectedPath = Paths.get(imagePath);

        // Action
        ocrModel.setFileToScan(imagePath);

        // Assert - Vérifier que fileToScan a été correctement défini
        try {
            java.lang.reflect.Field fileToScanField = OcrModel.class.getDeclaredField("fileToScan");
            fileToScanField.setAccessible(true);
            Path actualPath = (Path) fileToScanField.get(ocrModel);

            assertEquals(expectedPath, actualPath, "Le chemin du fichier devrait être correctement défini");
        } catch (Exception e) {
            fail("Erreur lors de l'accès au champ fileToScan: " + e.getMessage());
        }
    }
}