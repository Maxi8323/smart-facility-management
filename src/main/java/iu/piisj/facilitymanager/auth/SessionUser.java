package iu.piisj.facilitymanager.auth;

import iu.piisj.facilitymanager.user.UserRole;

import java.io.Serializable;

public class SessionUser implements Serializable {

    private final Long id;
    private final String username;
    private final String fullName;
    private final UserRole role;

    public SessionUser(Long id, String username, String fullName, UserRole role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public UserRole getRole() { return role; }
}
