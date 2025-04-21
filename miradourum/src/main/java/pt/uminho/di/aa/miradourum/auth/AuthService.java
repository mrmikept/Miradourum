package pt.uminho.di.aa.miradourum.auth;

import org.springframework.stereotype.Service;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.services.UserService;

@Service
public class AuthService {
    private JwtService jwtService;
    private UserService userService;

    public AuthService(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public String login(String email, String password) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return null;
        }
        if (!userService.checkPassword(password, user.getPassword())) {
            return null;
        }

        return jwtService.generateToken(user);
    }

    public User register(String email, String username, String password, Integer role) {
        if (userService.emailExists(email)) {
            return null;
        }
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        userService.saveUser(user);
        return user;
    }
}
