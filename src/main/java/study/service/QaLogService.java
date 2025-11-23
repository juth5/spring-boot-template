package study.service;
import java.util.List;
import org.springframework.stereotype.Service;
import study.mapper.QaLogMapper;
import study.model.QaLog;

@Service
public class QaLogService {
    private final QaLogMapper qaLogMapper;

    public QaLogService(QaLogMapper qaLogMapper) {
        this.qaLogMapper = qaLogMapper;
    }

    public List<QaLog> findByUserId(Long userId) {
        List<QaLog> logs = qaLogMapper.findByUserId(userId);
        return logs;
    }
}



