package study.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import study.model.QaLog;

@Mapper
public interface QaLogMapper {
    //ユーザーidから取得
    List<QaLog> findByUserId(@Param("userId") Long userId);
}
