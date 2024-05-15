package com.project.demo.User;

public class UserNotFoundException extends RuntimeException  {
    
    public UserNotFoundException(Long id){
        super("Could not find User "+id);
    }
}
