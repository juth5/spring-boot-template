package study.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import study.mapper.ApiCallLogMapper;
import study.model.ApiCallLog;

@Controller
public class HomeController {
	@Autowired ApiCallLogMapper apiCallLogMapper;

	@GetMapping("/")
	public String home() {
		List<ApiCallLog> logs = apiCallLogMapper.findAll();
		ApiCallLog log = apiCallLogMapper.findById(4L);

		return "index";
	}
}
