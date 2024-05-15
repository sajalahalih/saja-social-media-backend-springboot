package com.project.demo.Reel;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReelsRepositry extends JpaRepository<Reels,Long>{
    List<Reels> findByUserId(Long userId);
}
