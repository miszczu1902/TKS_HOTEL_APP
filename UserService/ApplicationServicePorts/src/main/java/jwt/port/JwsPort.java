package jwt.port;

import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface JwsPort {
    String generateJws(String payload) throws JOSEException;
    boolean verify(String payload) throws ParseException, JOSEException;
}
