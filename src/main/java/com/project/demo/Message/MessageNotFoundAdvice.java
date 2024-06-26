package com.project.demo.Message;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class MessageNotFoundAdvice {
    
    
    
       @ResponseBody
  @ExceptionHandler(MessageNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String MessageNotFoundHandler(MessageNotFoundException ex) {
    return ex.getMessage();
  }
 }
