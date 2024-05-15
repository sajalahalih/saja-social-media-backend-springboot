package com.project.demo.Post;

public class PostNotFoundException extends RuntimeException  {
    
    public PostNotFoundException(Long id){
        super("Could not find post "+id);
    }
}
