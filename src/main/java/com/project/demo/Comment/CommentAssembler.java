package com.project.demo.Comment;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
@Component
public class CommentAssembler  implements  RepresentationModelAssembler <Comment,EntityModel<Comment>>  {

    @Override
    public EntityModel<Comment> toModel(Comment comment) {
        return EntityModel.of(comment, //
        linkTo(methodOn(CommentController.class).getCommentById(comment.getId(),comment.getPost().getId(),comment.getUser().getId())).withSelfRel(),
        linkTo(methodOn(CommentController.class).getComments()).withRel("comments"));
   }
    
}
