package com.project.demo.Message;

public class MessageNotFoundException extends RuntimeException  {
    
    public MessageNotFoundException(Long messageId){
        super("Could not find Message "+messageId);
    }

}
    