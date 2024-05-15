package com.project.demo.Comment;

public class CommentNotFoundException extends RuntimeException  {
    
    public CommentNotFoundException(Long id,Long chatId){
        super("Could not find Comment "+id+" for chat "+chatId);
    }
    
}
