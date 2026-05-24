package study.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import study.dto.response.LoginResponse;
import study.mapper.AccountMapper;
import study.model.Account;
import study.security.JwtUtil;

public class AccountServiceTest {

    @Mock // 本物の代わりに動く「身代わり」
    private AccountMapper accountMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks // Mockを注入してテスト対象のインスタンスを作成
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void logIn_成功時にトークンを返すこと() {
        // --- 準備 (Given) ---
        String username = "testUser";
        String password = "rawPassword";
        Account mockUser = new Account();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword("encodedPassword");
        mockUser.setRole("USER");

        // Mockの振る舞いを定義
        when(accountMapper.findByUsername(username)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(1L, username, "USER")).thenReturn("mock-jwt-token");

        // --- 実行 (When) ---
        LoginResponse response = accountService.logIn(username, password);

        // --- 検証 (Then) ---
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals(username, response.getUsername());
    }

    @Test
    void logIn_ユーザー不在時に例外を投げること() {
        // 準備：Mapperがnullを返すように設定
        when(accountMapper.findByUsername("unknown")).thenReturn(null);

        // 実行 & 検証
        assertThrows(RuntimeException.class, () -> {
            accountService.logIn("unknown", "password");
        }, "ユーザーが存在しません");
    }
}