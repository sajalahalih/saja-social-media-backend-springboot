package com.project.demo.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.demo.Post.Post;
import com.project.demo.User.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Comment {

    private @Id @GeneratedValue Long id;
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "User is required")
    @ManyToOne
    private User user;

    @NotNull(message = "Post is required")
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;


    @ManyToMany
    private List<User> liked=new ArrayList<>();
    private LocalDateTime createdAt;


    @JsonIgnore
       @OneToMany(mappedBy = "parentComment")  // new field for replies
    private List<Comment> replies = new ArrayList<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_comment_id")  // new field for replies
    private Comment parentComment;




    public Comment(String content, User user, Post post, List<User> liked) {
        this.content = content;
        this.user = user;
        this.post = post;
        this.liked = liked;
        this.createdAt = LocalDateTime.now();
    }

    
    


    


    
}
