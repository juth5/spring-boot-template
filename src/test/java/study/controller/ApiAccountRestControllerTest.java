package study.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import study.service.AccountService;
import study.service.QaLogService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// テスト対象のコントローラーを指定します
@WebMvcTest(ApiAccountRestController.class) 
public class ApiAccountRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // コントローラーが依存しているServiceをモック（偽物）にします
    @MockBean
    private AccountService accountService;
    @MockBean
    private QaLogService qaLogService;

    @MockBean
    private study.security.JwtUtil jwtUtil;


    @Test
    @DisplayName("アカウント作成API：正常に作成され 'created' が返ること")
    void shouldCreateAccountSuccessfully() throws Exception {
        // 1. 送信するリクエストボディ（JSON）を準備
        // ※実際のリクエストクラスのフィールド名に合わせて調整してください
        String jsonRequest = """
                {
                    "username": "test_user",
                    "password": "password123"
                }
                """;

        // 2. 疑似的なHTTP POSTリクエストを実行
        mockMvc.perform(post("/api/account/create")
                .with(csrf()) // ★これが必要！Spring SecurityのCSRF保護をパスさせます
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                // 3. 結果の検証
                .andExpect(status().isOk()) // HTTP 200 OK であること
                .andExpect(content().string("created")); // 戻り値の文字列が一致すること

        // 4. Serviceメソッドが正しく呼ばれたか確認
        // コントローラー内部で引数が正しく渡されているかをチェックします
        verify(accountService, times(1))
            .createAccount("test_user", "password123");
    }
}