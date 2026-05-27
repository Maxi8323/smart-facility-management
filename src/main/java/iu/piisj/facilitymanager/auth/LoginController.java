package iu.piisj.facilitymanager.auth;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@ViewScoped
public class LoginController implements Serializable {

    @Inject
    private AuthController authController;

    private String username;
    private String password;

    public String submitLogin() {
        boolean authenticated = authController.login(username, password);
        if (!authenticated) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Login fehlgeschlagen",
                            "Benutzername oder Passwort ist falsch.")
            );
            return null;
        }
        return "/tickets.xhtml?faces-redirect=true";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
