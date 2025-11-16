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
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)   // 追加
                .claim("role", role)       // 追加
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)       // 署名
                .compact();
    }

    // ✅ token から username を取り出す
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // ✅ token が正しいかチェック
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
