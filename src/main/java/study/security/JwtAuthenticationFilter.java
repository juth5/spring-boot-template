package study.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Authorization ヘッダー取得
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // "Bearer " を取り除く
            String token = authHeader.substring(7);
            // token が "" or null → 認証せずスルー
            if (token == null 
                    || token.isBlank() 
                    || token.equalsIgnoreCase("null") 
                    || !token.contains(".")) {
                System.out.println("トークンが不正です。");
                throw new AuthenticationException("JWT invalid") {};
            }

            String username = null;
            Long userId = null;
            String role = null;

            try {
                // 1. ここで解析（期限切れなら例外が発生して catch へ飛ぶ）
                username = jwtUtil.extractUsername(token);
                userId = jwtUtil.extractUserId(token);
                role = jwtUtil.extractRole(token);

                // 2. 解析に成功し、さらに有効な場合のみ「認証済み」にする
                if (username != null && jwtUtil.validateToken(token)) {
                    UserPrincipal principal = new UserPrincipal(
                            userId,
                            username,
                            role
                    );

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    null,
                                    List.of(new SimpleGrantedAuthority(role))
                            );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // ✅ ここでセットされると「ログイン済み」になる
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            catch (Exception e) {
                // 🔥 ポイント：ここをログ出力だけにして、throw しない！
                System.out.println("JWTの検証に失敗（期限切れ等）: " + e.getMessage());
                // 何もしない（SecurityContextHolder にセットしない）まま catch を抜ける
            }
        }
        // 次のフィルターへ
        filterChain.doFilter(request, response);
    }
}
