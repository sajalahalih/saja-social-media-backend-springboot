package com.project.demo.User;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.demo.Post.Post;
import com.project.demo.Reel.Reels;
import com.project.demo.Story.Story;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor

@Table(name = "\"user\"", 
    uniqueConstraints = { 
      @UniqueConstraint(columnNames = "userName"),
      @UniqueConstraint(columnNames = "email") 
    })
public class User {
    
     
    private  @Id @GeneratedValue Long id;

    private String firstName;
    
    private String lastName;

   @Column(unique = true)
   @NotBlank
  @Size(max = 20)
    private String userName;

    private Gender gender;

    private String image;

    @NotBlank
  @Size(max = 100)
    private String email;
  
    @NotBlank
    @Size(max = 120)
    private String password;

   // private String bio;
   @JsonIgnore
   @ManyToMany
    private List<User> followers=new ArrayList<>();
    @JsonIgnore
    @ManyToMany(mappedBy = "followers")
    private List<User> following=new ArrayList<>();

     @JsonIgnore
     @OneToMany(mappedBy = "user")
     private List<Post> savedPosts=new ArrayList<>();

     
    //  @OneToMany(mappedBy = "user")

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
     private List<Post> posts = new ArrayList<>();

     @JsonIgnore
     @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
      private List<Reels> reels = new ArrayList<>();

      @JsonIgnore
      @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
       private List<Story> stories = new ArrayList<>();

    //  @JsonIgnore
    // @Builder.Default
    // @ToString.Exclude
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        
    public User( String userName, String email, String password) {
   
      this.userName = userName;
      this.email = email;
      this.password = password;
    
  }
  public User( String userName, String email, String password, String firstName, String lastName) {
   
    this.userName = userName;
    this.email = email;
    this.password = password;
    this.firstName=firstName;
    this.lastName=lastName;
  
}



    
}
