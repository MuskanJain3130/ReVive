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
import java.util.Date;

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
    private String selectedRoleId = "2"; // Default to User (Buyer)
    private boolean loggedIn = false;
    
    // OTP Fields
    private String inputOtp;
    private String generatedOtp;
    private boolean otpSent = false;
    private boolean otpVerified = false;

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

    public void sendOtp() {
        if (newUser.getPhone() == null || newUser.getPhone().trim().isEmpty()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please enter a phone number first."));
            return;
        }
        
        // Generate a 6-digit OTP
        generatedOtp = String.valueOf((int) (Math.random() * 900000) + 100000);
        otpSent = true;
        
        // Actually send the SMS to the phone number
        sendRealSms(newUser.getPhone(), generatedOtp);
        
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "OTP Sent", "The OTP has been sent to your phone number."));
    }

    private void sendRealSms(String phone, String otp) {
        String message = "Your ReVive verification code is: " + otp;
        util.SmsService.sendSms(phone, message);
    }

    public void verifyOtp() {
        if (generatedOtp != null && generatedOtp.equals(inputOtp)) {
            otpVerified = true;
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Verified", "Phone number verified successfully."));
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid OTP", "The code you entered is incorrect."));
        }
    }

    public String register() {
        if (!otpVerified) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please verify your phone number first."));
            return null;
        }
        
        try {
            // Check if username already exists
            try {
                entities.Users existingUser = userClient.getUserByUsername(entities.Users.class, newUser.getUsername());
                if (existingUser != null && existingUser.getUsername() != null) {
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username Taken", "This username already exists. Please choose a unique username for login."));
                    return null;
                }
            } catch (Exception ex) {
                // Ignore exception if user is not found (which means username is available)
            }

            newUser.setCreatedAt(new Date());
            userClient.registerUser(newUser, selectedRoleId);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration Successful", "You can now log in."));
            newUser = new Users(); // Reset
            otpSent = false;
            otpVerified = false;
            return "login?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();

            facesContext.addMessage(null,
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Registration Failed",
                    e.toString()
                )
            );

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
        return currentUser != null
            && currentUser.getRoleid() != null
            && Integer.valueOf(1).equals(currentUser.getRoleid().getRoleid());
    }

    public boolean isUser() {
        return currentUser != null
            && currentUser.getRoleid() != null
            && Integer.valueOf(2).equals(currentUser.getRoleid().getRoleid());
    }

    public boolean isSeller() {
        return isUser(); // Buyers and Sellers both use Role 2
    }

    public boolean isBuyer() {
        return isUser();
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
    
    public String getInputOtp() { return inputOtp; }
    public void setInputOtp(String inputOtp) { this.inputOtp = inputOtp; }
    public boolean isOtpSent() { return otpSent; }
    public boolean isOtpVerified() { return otpVerified; }
}
