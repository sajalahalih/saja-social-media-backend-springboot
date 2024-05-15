package com.project.demo.Reel;

public class ReelsNotFoundException extends RuntimeException  {
    
    public ReelsNotFoundException(Long id,Long userId){
        super("Could not find reel "+id+" for user "+userId);
    }
    
}
