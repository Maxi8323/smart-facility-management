package iu.piisj.facilitymanager.auth;

import iu.piisj.facilitymanager.repository.UserRepository;
import iu.piisj.facilitymanager.user.User;
import iu.piisj.facilitymanager.user.UserRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class AuthService {

    @Inject
    private UserRepository userRepository;

    public SessionUser authenticate(String username, String plainPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.isActive()) {
            return null;
        }
        if (!BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
            return null;
        }
        return new SessionUser(user.getId(), user.getUsername(), user.getFullName(), user.getRole());
    }

    public RegistrationResult register(String username, String email,
                                       String fullName, String plainPassword) {
        if (userRepository.findByUsername(username) != null) {
            return RegistrationResult.USERNAME_EXISTS;
        }
        if (userRepository.findByEmail(email) != null) {
            return RegistrationResult.EMAIL_EXISTS;
        }
        String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        userRepository.save(new User(username, hash, email, fullName, UserRole.REPORTER));
        return RegistrationResult.SUCCESS;
    }

    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
}
