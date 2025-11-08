package study.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {
    //レコードを1件追加
    void insertAccount();
}
