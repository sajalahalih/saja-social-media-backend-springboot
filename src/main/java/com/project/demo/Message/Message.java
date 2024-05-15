
package com.project.demo.Message;


import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.demo.Chat.Chat;
import com.project.demo.User.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Content is required")
    private String content;
    private String image;
    private String video;
 
    @NotNull(message = "User is required")
    @ManyToOne
    private User user;

    @NotNull(message = "Chat is required")
    @JsonIgnore
    @ManyToOne
    private Chat chat;

    private LocalDateTime timestamp;

    public Message(String content, String image, User user, Chat chat) {
        this.content = content;
        this.image = image;
        this.user = user;
        this.chat = chat;
        this.timestamp = LocalDateTime.now();
        
    }




    


    



    
}
