package study.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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


        System.out.println("aaaaaaaaaaaaaaaaaaaaaaa");
        System.out.println(authHeader);


        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // "Bearer " を取り除く
            String token = authHeader.substring(7);

            // username を中から取得
            String username = jwtUtil.extractUsername(token);

            // token が有効な場合
            if (username != null && jwtUtil.validateToken(token)) {

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                null // 権限（roles）を使うならここ
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
