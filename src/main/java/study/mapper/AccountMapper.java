package study.mapper;

import org.apache.ibatis.annotations.Mapper;

import study.model.Account;

@Mapper
public interface AccountMapper {
    //レコードを1件追加
    void insertAccount(Account account);
}
