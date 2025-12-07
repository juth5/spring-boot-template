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

                //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "トークンが無効です。ログインし直してください。");
                //return;
            }

            // username を中から取得
            String username = jwtUtil.extractUsername(token);
            Long userId = jwtUtil.extractUserId(token);   // ← 追加
            String role = jwtUtil.extractRole(token);     // ← 追加

            UserPrincipal principal = new UserPrincipal(
                    userId,
                    username,
                    role
            );
            // token が有効な場合
            if (username != null && jwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null, // ← principal に userId を入れると便利
                                List.of(new SimpleGrantedAuthority(role))
                        );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // ✅ Spring Security に「この人は認証済み」と伝える
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 次のフィルターへ
        filterChain.doFilter(request, response);
    }
}
