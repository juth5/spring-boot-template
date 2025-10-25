package study.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import study.mapper.ApiCallLogMapper;

@Service
public class ChatGptService {


  @Autowired
  private ApiCallLogMapper apiCallLogMapper;

  public void callChatGpt() {
      Integer count = apiCallLogMapper.countToday();

      if (count >= 5) {
          System.out.println("今日の呼び出し回数が上限に達しました。");
          return;
      }
      






  }
  
}
