package auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jwt.port.JwtPort;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;

@ApplicationScoped
public class JwtGenerator implements JwtPort {

    @Inject
    @ConfigProperty(name = "jwt.secret")
    private String SECRET;

    @Inject
    @ConfigProperty(name = "jwt.time")
    private long timeout;

    @Override
    public String generateJWT(String login, String role) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date())
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + timeout))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    @Override
    public Jws<Claims> parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(jwt);
    }
}
