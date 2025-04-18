package com.se.aiconomy.server;

import com.se.aiconomy.server.model.entity.User;
import com.se.aiconomy.server.service.UserService;
import com.se.aiconomy.server.service.impl.UserServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
    private static final Logger log = LoggerFactory.getLogger(User.class);
    private static final String TEST_USER_ID = "U2025041409";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "123456";
    private static JSONStorageService jsonStorageService;
    private static UserService userService;

    @BeforeAll
    static void setup() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        userService = new UserServiceImpl(jsonStorageService);
    }

    @BeforeEach
    void cleanUp() {
        jsonStorageService.findAll(User.class)
            .forEach(u -> jsonStorageService.delete(u, User.class));
        log.info("Cleaned up all users before test");
    }

    @Test
    @Order(1)
    void testRegisterAndLogin() {
        User user = createSampleUser();
        userService.register(user);

        User loggedIn = userService.login(TEST_EMAIL, TEST_PASSWORD);
        Assertions.assertEquals(TEST_USER_ID, loggedIn.getId());
        Assertions.assertEquals(TEST_EMAIL, loggedIn.getEmail());
        log.info("Successfully test register and login");
    }

    @Test
    @Order(2)
    void testDuplicateEmailRegistration() {
        User user = createSampleUser();
        userService.register(user);

        User duplicate = createSampleUser();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> userService.register(duplicate));
        Assertions.assertEquals("Email already exists", exception.getMessage());
        log.info("Successfully test duplicate email registration");
    }

    @Test
    @Order(3)
    void testInvalidLogin() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
            () -> userService.login("nonexistent@example.com", "wrong"));
        Assertions.assertEquals("Invalid email or password", exception.getMessage());
        log.info("Successfully test invalid login");
    }

    @Test
    @Order(4)
    void testUpdateUser() {
        User user = createSampleUser();
        userService.register(user);

        user.setPhone("7654321");
        userService.updateUser(user);

        User updated = userService.getUserById(TEST_USER_ID);
        Assertions.assertEquals("7654321", updated.getPhone());
        log.info("Successfully test update user");
    }

    @Test
    @Order(5)
    void testDeleteUser() {
        User user = createSampleUser();
        userService.register(user);

        userService.deleteUserById(TEST_USER_ID);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
            () -> userService.getUserById(TEST_USER_ID));
        Assertions.assertEquals("User not found with id: " + TEST_USER_ID, exception.getMessage());
        log.info("Successfully test delete user");
    }

    @Test
    @Order(6)
    void testGetUserById() {
        User user = createSampleUser();
        userService.register(user);

        User foundUser = userService.getUserById(TEST_USER_ID);
        Assertions.assertEquals(TEST_USER_ID, foundUser.getId());
        Assertions.assertEquals(TEST_EMAIL, foundUser.getEmail());
        log.info("Successfully test get user by ID");
    }

    @Test
    @Order(7)
    void testGetAllUsers() {
        User user = createSampleUser();
        userService.register(user);

        List<User> allUsers = userService.getAllUsers();
        Assertions.assertFalse(allUsers.isEmpty());
        Assertions.assertEquals(1, allUsers.size());
        Assertions.assertEquals(TEST_USER_ID, allUsers.getFirst().getId());
        log.info("Successfully test get all users");
    }

    private User createSampleUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);
        user.setFirstName("Test");
        user.setLastName("User");
        return user;
    }
}
