package security;

import io.jsonwebtoken.Claims;
import jwt.JWTParser;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class AuthMechanism implements HttpAuthenticationMechanism {

    @Inject
    private JWTParser parser;

    @Inject
    @ConfigProperty(name = "jwt.auth")
    private String AUTHORIZATION;

    @Inject
    @ConfigProperty(name = "jwt.bearer")
    private String BEARER;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest httpServletRequest,
                                                HttpServletResponse httpServletResponse,
                                                HttpMessageContext httpMessageContext) {
        String header = httpServletRequest.getHeader(AUTHORIZATION);
        Set<String> role = new HashSet<>();
        if (header != null) {
            if (header.startsWith(BEARER)) {
                try {
                    String token = header.replace(BEARER, "");
                    Claims claims = parser.parseJWT(token).getBody();
                    role.add(claims.get("role", String.class));
                    return httpMessageContext.notifyContainerAboutLogin(claims.getSubject(), role);
                } catch (Exception e) {
                    return httpMessageContext.responseUnauthorized();
                }
            }
        } else {
            role.add("GUEST");
        }
        return httpMessageContext.notifyContainerAboutLogin("guest", role);
    }
}