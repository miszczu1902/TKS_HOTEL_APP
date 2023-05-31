package rest.controllers;

import auth.AuthIdentityStore;
import auth.JwtGenerator;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import rest.dto.LoginDto;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/auth")
@RequestScoped
@Counted(name = "authCounter", description = "Auth Service counter")
@Timed(name = "authServiceCallTimer")
public class AuthController {

    @Context
    private SecurityContext securityContext;

    @Inject
    private AuthIdentityStore authIdentityStore;

    @Inject
    private JwtGenerator jwtGenerator;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("GUEST")
    @Retry(delay = 100, maxDuration = 1000, jitter = 100, maxRetries = 5)
    public Response login(LoginDto loginDto) {
        UsernamePasswordCredential usernamePasswordCredential = new UsernamePasswordCredential(loginDto.getUsername(), loginDto.getPassword());
        CredentialValidationResult credentialValidationResult = authIdentityStore.validate(usernamePasswordCredential);

        if (credentialValidationResult.getStatus().equals(CredentialValidationResult.Status.VALID)) {
            String jwt = jwtGenerator.generateJWT(loginDto.getUsername(), credentialValidationResult.getCallerGroups().iterator().next());
            return Response.ok().entity(jwt).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).build();
    }
}
