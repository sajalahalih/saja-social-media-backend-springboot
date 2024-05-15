package com.project.demo.Story;

public class StoryNotFoundException extends RuntimeException  {
    
    public StoryNotFoundException(Long id){
        super("Could not find Story "+id);
    }
    
}
