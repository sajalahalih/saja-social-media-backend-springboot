package com.project.demo.Post;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.demo.Comment.Comment;
import com.project.demo.User.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Post {
    

    private @Id @GeneratedValue Long id;
    @Size(max = 255)
    private  String caption;
   // @Pattern(regexp = "^(https?|ftp)://[\\w\\d\\-\\./]+$", message = "Invalid video URL format")
  
    private String image;
   // @Pattern(regexp = "^(https?|ftp)://[\\w\\d\\-\\./]+$", message = "Invalid video URL format")
  
    private String video;

  
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
   // @JsonManagedReference
   //@JsonIgnore
   // nothing is the originall
    
      // @JsonIgnoreProperties("posts")
    private User user;
    private LocalDateTime createdAt;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<User> liked =new ArrayList();

   // @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
        @JsonManagedReference
   private List<Comment> comments=new ArrayList<>();

    public Post(String caption, String image, String video, User user) {
        this.caption = caption;
        this.image = image;
        this.video = video;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.liked = new ArrayList();
        this.comments = new ArrayList();
    }

 
   

   

    

}
