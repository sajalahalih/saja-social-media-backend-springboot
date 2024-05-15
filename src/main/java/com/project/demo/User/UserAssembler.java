package com.project.demo.User;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.server.RepresentationModelAssembler;



@Component
public class UserAssembler  implements RepresentationModelAssembler <User,EntityModel<User>> {

    @Override
    public EntityModel<User> toModel(User user) {
        return EntityModel.of(user, //
        linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
        linkTo(methodOn(UserController.class).getUsers()).withRel("users"));
   }

}
