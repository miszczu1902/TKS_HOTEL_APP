package security;

import auth.JwtGenerator;
import io.jsonwebtoken.Claims;

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

    public static final String AUTHORIZATION = "Authorization";

    public static final String BEARER = "Bearer ";

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
                    Claims claims = generator.parseJWT(token).getBody();
                    role.add(claims.get("role", String.class));
                    return httpMessageContext.notifyContainerAboutLogin(claims.getSubject(), role);
                } catch (Exception e) {
                    return httpMessageContext.responseUnauthorized();
                }
            }
        }
        role.add("GUEST");
        return httpMessageContext.notifyContainerAboutLogin("guest", role);
    }
}