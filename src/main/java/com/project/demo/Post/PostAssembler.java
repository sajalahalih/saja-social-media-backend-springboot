package com.project.demo.Post;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;




@Component
public class PostAssembler implements  RepresentationModelAssembler <Post,EntityModel<Post>>  {

    @Override
    public EntityModel<Post> toModel(Post post) {
        return EntityModel.of(post, //
        linkTo(methodOn(PostController.class).getPostById(post.getId())).withSelfRel(),
        linkTo(methodOn(PostController.class).getPosts()).withRel("posts"));
   }
    
}
