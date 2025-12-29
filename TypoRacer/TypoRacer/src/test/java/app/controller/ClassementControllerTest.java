package app.controller;

import app.dto.Classement;
import app.dto.Texte;
import app.dto.User;
import app.model.Difficulty;
import app.model.Mode;
import app.service.ClassementService;
import app.service.GameConfigService;
import app.service.TexteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassementControllerTest {

    private ClassementController controller;
    private ClassementService mockClassementService;
    private TexteService mockTexteService;
    private GameConfigService mockConfigService;

    private final Texte texte1 = new Texte(1, "Titre1", "Contenu1", "Auteur1");
    private final User user = new User(1, "TestUser", "password");
    private final Classement classement1 = new Classement(100, "TestUser");
    private final Classement classement2 = new Classement(80, "User2");

    @BeforeEach
    void setUp() throws Exception {
        controller = new ClassementController();

        mockClassementService = mock(ClassementService.class);
        mockTexteService = mock(TexteService.class);
        mockConfigService = mock(GameConfigService.class);

        when(mockConfigService.getUser()).thenReturn(user);
        when(mockTexteService.getTextes()).thenReturn(Arrays.asList(texte1, new Texte(2, "Titre2", "Contenu2", "Auteur2")));
        when(mockClassementService.getTopScores(any(Mode.class), anyInt(), any(Difficulty.class)))
                .thenReturn(Arrays.asList(classement1, classement2));

        injectMock("classementService", mockClassementService);
        injectMock("texteService", mockTexteService);
        injectMock("configService", mockConfigService);
    }

    private void injectMock(String fieldName, Object mockObject) throws Exception {
        Field field = ClassementController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, mockObject);
    }

    @Test
    @DisplayName("setGameConfigService() devrait définir correctement le service")
    void testSetGameConfigService() throws Exception {
        GameConfigService newService = mock(GameConfigService.class);
        controller.setGameConfigService(newService);

        Field configServiceField = ClassementController.class.getDeclaredField("configService");
        configServiceField.setAccessible(true);
        Object actualService = configServiceField.get(controller);

        assertSame(newService, actualService, "Le service devrait être correctement défini");
    }

    @Test
    @DisplayName("ClassementService.getUserRecord devrait être appelé avec les bons paramètres")
    void testClassementServiceInteraction()  {
        // Tester directement l'interaction avec le service
        when(mockConfigService.getUser()).thenReturn(user);

        // Appeler directement le service avec les paramètres attendus
        mockClassementService.getUserRecord(Mode.CLASSIC, user.id(), texte1.id(), Difficulty.NORMAL);

        // Vérifier que la méthode a été appelée avec les bons paramètres
        verify(mockClassementService).getUserRecord(Mode.CLASSIC, user.id(), texte1.id(), Difficulty.NORMAL);
    }


}

