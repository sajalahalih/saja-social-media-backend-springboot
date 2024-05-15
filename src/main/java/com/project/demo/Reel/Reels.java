package com.project.demo.Reel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cglib.core.Local;

import com.project.demo.Comment.Comment;
import com.project.demo.User.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Reels {

    @Id
    @GeneratedValue
    private Long id;

    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;
   
    @NotBlank(message = "Video URL is required")
    @Pattern(regexp = "^(https?|ftp)://[\\w\\d\\-\\./]+$", message = "Invalid video URL format")
      private String video;

    @ManyToOne
    @NotNull(message = "User is required")
    private User user;

     @OneToMany
    private List<User> liked =new ArrayList();

    @OneToMany
    private List<Comment> comments=new ArrayList<>();

    public Reels(String title, String video, User user) {
        this.title = title;
        this.video = video;
        this.user = user;
        this.liked=new ArrayList<>();
        this.comments=new ArrayList<>();
    }
    

    


    
}

