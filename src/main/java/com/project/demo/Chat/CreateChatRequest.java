package com.project.demo.Chat;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter
@NoArgsConstructor
public class CreateChatRequest {

    private Long userId;

    public CreateChatRequest(Long userId) {
        this.userId = userId;
    }

    
    
}
