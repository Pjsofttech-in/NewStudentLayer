package Layer.NewStudentManagement.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class InternalJwtProvider {

    @Value("${internal.jwt.secret}")
    private String secret;

    public String generateInternalToken() {
        return Jwts.builder()
                .setIssuer("student-service")
                .claim("type", "INTERNAL")
                .claim("scope", "PAYMENT_ACCESS")
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000) // 24 hours
                )
                .signWith(
                        Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256
                )


                .compact();
    }
}
