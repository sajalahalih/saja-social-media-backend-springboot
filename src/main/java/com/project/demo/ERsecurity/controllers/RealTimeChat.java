package com.project.demo.ERsecurity.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.project.demo.Message.Message;


@Controller
public class RealTimeChat {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/chat/{groupId}")
    public Message sendToUser( @Payload Message message,
                               @DestinationVariable String groupId){
                                System.out.println("Sending message to user:------------------- " + groupId);

            simpMessagingTemplate.convertAndSendToUser(groupId, "/private", message);
        return message;
       

    }
}
