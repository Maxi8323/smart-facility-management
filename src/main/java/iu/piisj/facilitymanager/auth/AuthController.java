package iu.piisj.facilitymanager.auth;

import iu.piisj.facilitymanager.user.UserRole;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class AuthController implements Serializable {

    public static final String SESSION_USER_KEY = "AUTH_USER";

    @Inject
    private AuthService authService;

    private SessionUser currentUser;

    public boolean login(String username, String password) {
        SessionUser user = authService.authenticate(username, password);
        if (user == null) {
            return false;
        }
        currentUser = user;
        FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().put(SESSION_USER_KEY, currentUser);
        return true;
    }

    public String logout() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.invalidateSession();
        currentUser = null;
        return "/login.xhtml?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == UserRole.ADMIN;
    }

    public boolean isTechnician() {
        return currentUser != null && currentUser.getRole() == UserRole.TECHNICIAN;
    }

    public boolean isReporter() {
        return currentUser != null && currentUser.getRole() == UserRole.REPORTER;
    }

    public boolean isAdminOrTechnician() {
        return isAdmin() || isTechnician();
    }

    public SessionUser getCurrentUser() {
        return currentUser;
    }
}
