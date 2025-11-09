package study.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    // ✅ 学習用：起動するたびに作られる鍵（後で固定化に変更）
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // ✅ 有効期限（例：1日）
    private final long EXPIRATION = 1000 * 60 * 60 * 24;

    // ✅ JWTを作成して返す
    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)       // 署名
                .compact();
    }
}
