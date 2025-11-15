package study.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import study.model.Account;

@Mapper
public interface AccountMapper {
    //レコードを1件追加
    void insertAccount(Account account);
    //ユーザー名から取得
    Account findByUsername(String username);
    //全件取得
    List<Account> getAccounts();
}
