package com.project.demo.Message;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;



@Component
public class MessageAssembler implements  RepresentationModelAssembler <Message,EntityModel<Message>>  {

    @Override
    public EntityModel<Message> toModel(Message message) {
        return EntityModel.of(message, //
        linkTo(methodOn(MessageController.class).getMessageById(message.getId(),message.getChat().getId())).withSelfRel(),
        linkTo(methodOn(MessageController.class).getMessages()).withRel("messages"));
   }
    
   
    
}
