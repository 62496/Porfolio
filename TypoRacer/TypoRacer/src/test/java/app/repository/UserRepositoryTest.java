package app.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import app.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Tests unitaires pour UserRepository
 */
class UserRepositoryTest {

    private UserDao userDao;
    private UserRepository userRepository;
    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Création des données de test
        testUser1 = new User(1, "TestUser1", "password1");
        testUser2 = new User(2, "TestUser2", "password2");

        // Créer le mock avant instanciation de l'objet testé
        userDao = mock(UserDao.class);

        // Configure les comportements des mocks avant création
        when(userDao.findAll()).thenReturn(Arrays.asList(testUser1, testUser2));

        // Instancie la classe testée avec le mock
        userRepository = new UserRepository(userDao);
    }

    @Test
    void testCheckData() {
        System.out.println("testCheckData");
        // Arrange
        User user = new User(1, "TestUser1", "password1");
        when(userDao.checkData(user)).thenReturn(Optional.of(user));

        // Action
        Optional<User> result = userRepository.checkData(user);

        // Assert
        assertTrue(result.isPresent(), "L'utilisateur devrait être trouvé");
        assertEquals(user, result.get(), "L'utilisateur retourné devrait correspondre");
        verify(userDao).checkData(user);
    }

    @Test
    void testSaveUser() {
        System.out.println("testSaveUser");
        // Arrange
        User newUser = new User(0, "NewUser", "newPassword");
        int expectedId = 3;
        when(userDao.saveUser(newUser)).thenReturn(expectedId);

        // Action
        int generatedId = userRepository.saveUser(newUser);

        // Assert
        assertEquals(expectedId, generatedId, "L'ID généré devrait correspondre");
        verify(userDao).saveUser(newUser);

        // Vérifier que l'utilisateur est ajouté au cache
        try {
            java.lang.reflect.Field cacheField = UserRepository.class.getDeclaredField("userCache");
            cacheField.setAccessible(true);
            Map<Integer, User> cache = (Map<Integer, User>) cacheField.get(userRepository);
            User cachedUser = cache.get(expectedId);

            assertNotNull(cachedUser, "L'utilisateur devrait être présent dans le cache");
            assertEquals(expectedId, cachedUser.id(), "L'ID de l'utilisateur en cache devrait correspondre");
            assertEquals("NewUser", cachedUser.name(), "Le nom de l'utilisateur en cache devrait correspondre");
        } catch (Exception e) {
            fail("Impossible d'accéder au cache: " + e.getMessage());
        }
    }

    @Test
    void testDelete() {
        System.out.println("testDelete");
        // Arrange
        int userId = 1;

        // Action
        userRepository.delete(userId);

        // Assert
        verify(userDao).deleteById(userId);

        // Vérifier que l'utilisateur est supprimé du cache
        try {
            java.lang.reflect.Field cacheField = UserRepository.class.getDeclaredField("userCache");
            cacheField.setAccessible(true);
            Map<Integer, User> cache = (Map<Integer, User>) cacheField.get(userRepository);

            assertFalse(cache.containsKey(userId), "L'utilisateur ne devrait plus être présent dans le cache");
        } catch (Exception e) {
            fail("Impossible d'accéder au cache: " + e.getMessage());
        }
    }

    @Test
    void testLoadCache() {
        System.out.println("testLoadCache");
        // Le cache est chargé dans le constructeur, donc nous vérifions simplement
        // qu'il contient les utilisateurs retournés par findAll

        try {
            java.lang.reflect.Field cacheField = UserRepository.class.getDeclaredField("userCache");
            cacheField.setAccessible(true);
            Map<Integer, User> cache = (Map<Integer, User>) cacheField.get(userRepository);

            assertEquals(2, cache.size(), "Le cache devrait contenir deux utilisateurs");
            assertTrue(cache.containsKey(1), "Le cache devrait contenir l'utilisateur d'ID 1");
            assertTrue(cache.containsKey(2), "Le cache devrait contenir l'utilisateur d'ID 2");
            assertEquals("TestUser1", cache.get(1).name(), "Le nom de l'utilisateur 1 devrait correspondre");
            assertEquals("TestUser2", cache.get(2).name(), "Le nom de l'utilisateur 2 devrait correspondre");
        } catch (Exception e) {
            fail("Impossible d'accéder au cache: " + e.getMessage());
        }

        verify(userDao).findAll();
    }
    @Test
    void testClose() {
        System.out.println("testClose");

        // Mock ConnectionManager avec une classe utilitaire ou dummy si possible
        // Ici on vérifie simplement que l'appel ne lève pas d'exception
        assertDoesNotThrow(() -> userRepository.close(), "L'appel à close() ne doit pas lever d'exception");
    }
}

