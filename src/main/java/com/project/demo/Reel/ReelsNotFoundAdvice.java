package com.project.demo.Reel;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ReelsNotFoundAdvice {
    
      @ResponseBody
  @ExceptionHandler(ReelsNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String ReelsNotFoundHandler(ReelsNotFoundException ex) {
    return ex.getMessage();
  }
}
