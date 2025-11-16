package study.model;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QaLog {
    private Long id;
    private Long userId;
    private String question;
    private String answer;
    private OffsetDateTime createdAt;
}
