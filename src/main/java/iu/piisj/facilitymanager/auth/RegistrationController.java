package iu.piisj.facilitymanager.auth;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@ViewScoped
public class RegistrationController implements Serializable {

    @Inject
    private AuthService authService;

    @Inject
    private AuthController authController;

    private String username;
    private String email;
    private String fullName;
    private String password;
    private String confirmPassword;

    public String submitRegistration() {
        if (!password.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Registrierung fehlgeschlagen",
                            "Passwort und Bestätigung stimmen nicht überein.")
            );
            return null;
        }

        RegistrationResult result = authService.register(username, email, fullName, password);

        if (result == RegistrationResult.USERNAME_EXISTS) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Registrierung fehlgeschlagen",
                            "Benutzername ist bereits vergeben.")
            );
            return null;
        }

        if (result == RegistrationResult.EMAIL_EXISTS) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Registrierung fehlgeschlagen",
                            "E-Mail-Adresse ist bereits vergeben.")
            );
            return null;
        }

        authController.login(username, password);
        return "/tickets.xhtml?faces-redirect=true";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
