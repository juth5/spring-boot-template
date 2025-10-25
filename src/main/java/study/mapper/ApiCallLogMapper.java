package study.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import study.model.ApiCallLog;
@Mapper
public interface ApiCallLogMapper {
    // すべてのログを取得
    List<ApiCallLog> findAll();

    // IDでログを取得
    ApiCallLog findById(Long id);

    // 今日の日付の件数をカウント
    Integer countToday();
}