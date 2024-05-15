package com.project.demo.Story;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class StoryNotFoundAdvice {

    
       @ResponseBody
  @ExceptionHandler(StoryNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String StoryNotFoundHandler(StoryNotFoundException ex) {
    return ex.getMessage();
  }
    
}
