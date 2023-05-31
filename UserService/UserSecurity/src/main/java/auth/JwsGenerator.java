package auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import jwt.port.JwsPort;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.text.ParseException;

@ApplicationScoped
public class JwsGenerator implements JwsPort {

    @Inject
    @ConfigProperty(name = "jwt.secret")
    private String SECRET;

    public String generateJws(String payload) throws JOSEException {
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(payload));
        JWSSigner signer = new MACSigner(SECRET);
        jwsObject.sign(signer);
        return jwsObject.serialize();
    }

    @Override
    public boolean verify(String payload) throws ParseException, JOSEException {
        JWSObject jwsObject = JWSObject.parse(payload);
        JWSVerifier verifier = new MACVerifier(SECRET);
        return jwsObject.verify(verifier);
    }
}