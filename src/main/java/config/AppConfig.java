package config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.annotation.FacesConfig;
import jakarta.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

@FacesConfig
@ApplicationScoped
@CustomFormAuthenticationMechanismDefinition(
    loginToContinue = @LoginToContinue(
        loginPage = "/login.xhtml",
        errorPage = "",
        useForwardToLogin = false
    )
)
@DatabaseIdentityStoreDefinition(
    dataSourceLookup = "jdbc/revive",
    callerQuery = "select password from users where username = ?",
    groupsQuery = "select r.rolename from role_master r join users u on r.roleid = u.roleid where u.username = ?",
    hashAlgorithm = Pbkdf2PasswordHash.class
)
public class AppConfig {
}
