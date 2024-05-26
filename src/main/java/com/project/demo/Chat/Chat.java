package com.project.demo.Chat;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.project.demo.Message.Message;
import com.project.demo.User.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter @Setter @NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Chat name is required")
    private String chatName;
    private String chatImage;

    @ManyToMany
    @NotNull(message = "At least one user must be associated with the chat")
    
      private List<User> users=new ArrayList<>();

    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "chat")
    private List<Message> messages=new ArrayList<>();

    public Chat(String chatName, String chatImage, List<User> users) {
        this.chatName = chatName;
        this.chatImage = chatImage;
        this.users = users;
        this.timestamp = LocalDateTime.now();
    }
    

    
}

