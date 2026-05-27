package iu.piisj.facilitymanager.user;

import iu.piisj.facilitymanager.auth.AuthController;
import iu.piisj.facilitymanager.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class UserAdminController implements Serializable {

    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthController authController;

    private List<User> users;

    @PostConstruct
    public void init() {
        loadUsers();
    }

    private void loadUsers() {
        users = userRepository.findAll();
    }

    public void changeRole(Long id, UserRole newRole) {
        if (isSelf(id)) {
            warn("Eigene Rolle kann nicht geändert werden.");
            return;
        }
        User user = userRepository.findById(id);
        if (user != null) {
            user.setRole(newRole);
            userRepository.save(user);
            loadUsers();
            info("Rolle von " + user.getUsername() + " auf " + newRole + " gesetzt.");
        }
    }

    public void toggleActive(Long id) {
        if (isSelf(id)) {
            warn("Eigenes Konto kann nicht deaktiviert werden.");
            return;
        }
        User user = userRepository.findById(id);
        if (user != null) {
            user.setActive(!user.isActive());
            userRepository.save(user);
            loadUsers();
        }
    }

    private boolean isSelf(Long id) {
        return id.equals(authController.getCurrentUser().getId());
    }

    private void info(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    private void warn(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null));
    }

    public List<User> getUsers() { return users; }
    public UserRole[] getRoles() { return UserRole.values(); }
}
