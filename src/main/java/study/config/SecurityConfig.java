package study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import study.security.JwtAuthenticationFilter;


@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }


    // @Bean
    // public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    //     UserDetails user = User.withUsername("user")
    //             .password(passwordEncoder.encode("password"))
    //             .roles("USER")
    //             .build();

    //     return new InMemoryUserDetailsManager(user);
    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.disable()) // ← API では必須
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/account/create").permitAll()
            .requestMatchers("/api/account/logIn").permitAll()
            .anyRequest().authenticated()
        )
            //UsernamePasswordAuthenticationFilter より前に JWT をチェックするフィルターを差し込め
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            //セッションをつくらない設定
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint((req, res, e) -> {
                    System.out.println("Unauthorized error: " + e.getMessage());
                    // 🔥 JWTが無い / 無効ならここ
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("""
                    { "message": "ログインが必要です", "isLogin": false }
                    """);
                    res.getWriter().flush();
                })
            )
          .formLogin(form -> form.disable()); // ← REST API は formLogin 無効にする
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}
