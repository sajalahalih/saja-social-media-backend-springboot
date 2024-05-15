package com.project.demo.Post;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class PostNotFoundAdvice {
    
       @ResponseBody
  @ExceptionHandler(PostNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String UserNotFoundHandler(PostNotFoundException ex) {
    return ex.getMessage();
  }
}
