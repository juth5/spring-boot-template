package study.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import study.mapper.ApiCallLogMapper;
import study.model.ApiCallLog;

import java.util.List;

@RestController
@RequestMapping("/api/calllogs")
public class ApiCallLogRestController {

    @Autowired
    private ApiCallLogMapper apiCallLogMapper;

    // 全件取得（GET /api/calllogs）
    @GetMapping
    public List<ApiCallLog> findAll() {
        return apiCallLogMapper.findAll();
    }

    // 単一取得（GET /api/calllogs/{id}）
    @GetMapping("/{id}")
    public ApiCallLog findById(@PathVariable("id") Long id) {
        return apiCallLogMapper.findById(id);
    }

    // 今日の呼び出し回数を取得
    @GetMapping("/count/today")
    public Integer countToday() {
        return apiCallLogMapper.countToday();
    }

    

    // 新規登録（POST /api/calllogs）
    // @PostMapping
    // public void insert(@RequestBody ApiCallLog log) {
    //     apiCallLogMapper.insert(log);
    // }
}

