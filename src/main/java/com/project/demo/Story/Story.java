package com.project.demo.Story;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.project.demo.User.User;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Story {
    
    private @Id @GeneratedValue Long id;

    private  String caption;
    private String image;
    private String video;

  
    @ManyToOne
    @NotNull(message = "User is required")
    private User user;
    private LocalDateTime createdAt;

     @OneToMany
    private List<User> liked =new ArrayList();

    public Story(String caption, String image, String video, User user,  List<User> liked) {
        this.caption = caption;
        this.image = image;
        this.video = video;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.liked = liked;
    }







}

