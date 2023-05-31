package jwt.port;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface JwtPort {
    Jws<Claims> parseJWT(String jwt);
}
