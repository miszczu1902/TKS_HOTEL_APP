package jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jwt.port.JwtPort;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class JWTParser implements JwtPort {
    @Inject
    @ConfigProperty(name = "jwt.secret")
    private String SECRET;

    @Override
    public Jws<Claims> parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(jwt);
    }
}
