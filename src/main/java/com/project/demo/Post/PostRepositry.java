package com.project.demo.Post;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.demo.User.User;


public interface PostRepositry extends JpaRepository<Post,Long>{
    
    List<Post> findByUser_Id(Long userId);
    List<Post> findByUserIn(List<User> users);
}