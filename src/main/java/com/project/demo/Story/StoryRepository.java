package com.project.demo.Story;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

 

public interface StoryRepository  extends JpaRepository<Story,Long> {
    
    List<Story> findByUserId(Long userId);
}
