package jwt.port;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface JwtPort {
    String generateJWT(String login, String role);
    Jws<Claims> parseJWT(String jwt);
}
