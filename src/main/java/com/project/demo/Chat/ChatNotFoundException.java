package com.project.demo.Chat;

public class ChatNotFoundException extends RuntimeException  {
    
    public ChatNotFoundException(Long chatid){
        super("Could not find chat "+chatid);
    }
    
}
