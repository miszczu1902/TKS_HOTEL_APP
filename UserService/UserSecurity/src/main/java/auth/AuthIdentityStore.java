package auth;

import domain.model.User;
import service.port.infrasturcture.UserInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.ws.rs.core.Context;
import java.util.*;

@ApplicationScoped
public class AuthIdentityStore implements IdentityStore {

    @Inject
    private UserInfServicePort userInfServicePort;

    @Context
    private SecurityContext securityContext;

    @Override
    public int priority() {
        return 70;
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return EnumSet.of(ValidationType.VALIDATE);
    }

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        User user = new ArrayList<>(userInfServicePort.getAllUsers().stream().filter(user1 -> user1.getUsername().equals(
                validationResult.getCallerPrincipal().getName())).toList()).get(0);
        return new HashSet<>(Collections.singleton(user.getRole().toString()));
    }

    public CredentialValidationResult validate(UsernamePasswordCredential credential) {
        User user = userInfServicePort.getByUsernameAndPassword(credential.getCaller(),
                credential.getPasswordAsString());
        if (user != null && user.getIsActive()) {
            return new CredentialValidationResult(user.getUsername(), new HashSet<>(Collections.singleton(user.getRole().toString())));
        }
        return CredentialValidationResult.INVALID_RESULT;
    }
}
