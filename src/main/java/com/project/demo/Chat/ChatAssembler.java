package com.project.demo.Chat;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


@Component
public class ChatAssembler implements  RepresentationModelAssembler <Chat,EntityModel<Chat>>  {

    @Override
    public EntityModel<Chat> toModel(Chat chat) {
        return EntityModel.of(chat, //
        linkTo(methodOn(ChatController.class).getChatById(chat.getId())).withSelfRel(),
        linkTo(methodOn(ChatController.class).getChats()).withRel("comments"));
   }
    
}
