package com.codingplatform.service;

import com.codingplatform.dao.UserDAO;
import com.codingplatform.model.Student;
import com.codingplatform.model.User;
import com.codingplatform.exception.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private MockUserDAO mockUserDAO;

    @BeforeEach
    public void setup() {
        mockUserDAO = new MockUserDAO();
        userService = new UserServiceImpl(mockUserDAO);
    }

    @Test
    public void testValidRegistration() throws DatabaseException {
        User user = new Student();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        
        boolean result = userService.registerUser(user);
        assertTrue(result);
        assertEquals(1, mockUserDAO.usersCreated);
        assertNotEquals("password123", user.getPassword(), "Password should be hashed");
        assertTrue(user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$") || user.getPassword().startsWith("$2y$"), "Password should be BCrypt hashed");
    }

    @Test
    public void testDuplicateEmail() throws DatabaseException {
        User user1 = new Student();
        user1.setName("Jane Doe");
        user1.setEmail("jane@example.com");
        user1.setPassword("password123");
        userService.registerUser(user1);
        
        User user2 = new Student();
        user2.setName("John Doe");
        user2.setEmail("jane@example.com");
        user2.setPassword("password123");
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(user2);
        });
        assertEquals("Email is already registered.", ex.getMessage());
    }

    @Test
    public void testInvalidRole() {
        User user = new Student();
        user.setName("John Doe");
        user.setEmail("john2@example.com");
        user.setPassword("password123");
        user.setRole("INVALID");
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(user);
        });
        assertEquals("Invalid role selected.", ex.getMessage());
    }

    @Test
    public void testRequiredFieldValidation() {
        User user = new Student();
        user.setName("");
        user.setEmail("john3@example.com");
        user.setPassword("password123");
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(user);
        });
        assertEquals("Name is required.", ex.getMessage());
    }
}

// Simple manual mock for UserDAO
class MockUserDAO implements UserDAO {
    int usersCreated = 0;
    private java.util.Map<String, User> db = new java.util.HashMap<>();

    @Override
    public User getUserById(int id) { return null; }

    @Override
    public User getUserByEmail(String email) {
        return db.get(email);
    }

    @Override
    public boolean createUser(User user) {
        db.put(user.getEmail(), user);
        usersCreated++;
        return true;
    }

    @Override
    public java.util.List<User> getAllUsers() { return null; }

    @Override
    public java.util.List<User> getRecentUsers(int limit) { return null; }

    @Override
    public boolean updateUserRole(int userId, String newRole) { return false; }

    @Override
    public int countAdmins() { return 0; }

    @Override
    public boolean updateUserProfile(int userId, String name, String email) { return false; }
}
