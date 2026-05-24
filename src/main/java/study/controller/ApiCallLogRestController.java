package study.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import study.mapper.ApiCallLogMapper;
import study.model.ApiCallLog;

@RestController
@RequestMapping("/api/calllogs")
public class ApiCallLogRestController {

    private final ApiCallLogMapper apiCallLogMapper;

    public ApiCallLogRestController(ApiCallLogMapper apiCallLogMapper) {
        this.apiCallLogMapper = apiCallLogMapper;
    }

    @GetMapping
    public List<ApiCallLog> findAll() {
        return apiCallLogMapper.findAll();
    }

    @GetMapping("/{id}")
    public ApiCallLog findById(@PathVariable("id") Long id) {
        return apiCallLogMapper.findById(id);
    }

    @GetMapping("/count/today")
    public Integer countToday() {
        return apiCallLogMapper.countToday();
    }
}
