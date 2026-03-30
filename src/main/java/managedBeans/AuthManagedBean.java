package managedBeans;

import client.UserClient;
import entities.Users;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Serializable;

@Named("authBean")
@SessionScoped
public class AuthManagedBean implements Serializable {

    @Inject
    private SecurityContext securityContext;

    @Inject
    private FacesContext facesContext;

    @Inject
    private ExternalContext externalContext;

    private UserClient userClient = new UserClient();
    private Users currentUser;
    private Users newUser = new Users();
    private String username;
    private String password;
    private String selectedRoleId = "2"; // Default to Buyer
    private boolean loggedIn = false;

    public void login() {
        AuthenticationStatus result = authenticate();
        if (result == AuthenticationStatus.SUCCESS) {
            loggedIn = true;
            // Fetch the full user object from DB after successful auth
            try {
                currentUser = userClient.getUserByUsername(entities.Users.class, username);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (isAdmin()) {
                redirect("admin.xhtml");
            } else {
                redirect("index.xhtml");
            }
        } else if (result == AuthenticationStatus.SEND_FAILURE) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid credentials"));
        } else if (result == AuthenticationStatus.SEND_CONTINUE) {
            facesContext.responseComplete();
        }
    }

    public String register() {
        try {
            userClient.registerUser(newUser, selectedRoleId);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration Successful", "You can now log in."));
            newUser = new Users(); // Reset
            return "login?faces-redirect=true";
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration Failed", e.getMessage()));
            return null;
        }
    }

    private AuthenticationStatus authenticate() {
        return securityContext.authenticate(
                (HttpServletRequest) externalContext.getRequest(),
                (HttpServletResponse) externalContext.getResponse(),
                AuthenticationParameters.withParams().credential(new UsernamePasswordCredential(username, password))
        );
    }

    public String logout() {
        externalContext.invalidateSession();
        return "index?faces-redirect=true";
    }

    private void redirect(String page) {
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/" + page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAdmin() {
        return securityContext.isCallerInRole("admin");
    }

    public boolean isUser() {
        return securityContext.isCallerInRole("user");
    }

    public boolean isSeller() {
        return securityContext.isCallerInRole("seller") || securityContext.isCallerInRole("admin");
    }

    // Getters and Setters
    public Users getCurrentUser() { return currentUser; }
    public void setCurrentUser(Users currentUser) { this.currentUser = currentUser; }
    public Users getNewUser() { return newUser; }
    public void setNewUser(Users newUser) { this.newUser = newUser; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getSelectedRoleId() { return selectedRoleId; }
    public void setSelectedRoleId(String selectedRoleId) { this.selectedRoleId = selectedRoleId; }
    public boolean isLoggedIn() { return loggedIn; }
}
