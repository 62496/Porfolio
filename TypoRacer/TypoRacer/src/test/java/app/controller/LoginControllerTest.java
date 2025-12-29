package app.controller;

import app.dto.User;
import app.service.GameConfigService;
import app.service.UserService;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    private LoginController controller;
    private UserService userServiceMock;
    private GameConfigService gameConfigServiceMock;

    @BeforeEach
    void setup() {
        controller = new LoginController();

        // Mocks
        userServiceMock = mock(UserService.class);
        gameConfigServiceMock = mock(GameConfigService.class);

        controller.setUserService(userServiceMock);
        controller.setGameConfigService(gameConfigServiceMock);


    }



    @Test
    void testSetUserServiceAndGameConfigService() {
        LoginController ctrl = new LoginController();
        ctrl.setUserService(userServiceMock);
        ctrl.setGameConfigService(gameConfigServiceMock);

        assertNotNull(userServiceMock);
        assertNotNull(gameConfigServiceMock);
    }
}
