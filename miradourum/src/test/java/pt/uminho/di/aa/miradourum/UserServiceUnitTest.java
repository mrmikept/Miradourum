package pt.uminho.di.aa.miradourum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.repositories.UserRepository;
import pt.uminho.di.aa.miradourum.services.UserService;

import java.util.List;

@SpringBootTest
public class UserServiceUnitTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        // Clear any existing data
        userRepository.deleteAll();

        // Add test users
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        // Set other required fields

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        // Set other required fields

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    public void testGetUsers() {
        List<User> users = userService.getUsers();
        Assertions.assertEquals(2, users.size());
    }
}