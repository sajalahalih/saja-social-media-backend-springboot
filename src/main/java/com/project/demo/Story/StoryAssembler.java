package com.project.demo.Story;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.project.demo.Post.Post;
import com.project.demo.Post.PostController;

@Component
public class StoryAssembler  implements  RepresentationModelAssembler <Story,EntityModel<Story>>  {

    @Override
    public EntityModel<Story> toModel(Story story) {
        return EntityModel.of(story, //
        linkTo(methodOn(StoryController.class).getStoryById(story.getId(),story.getUser().getId())).withSelfRel(),
        linkTo(methodOn(StoryController.class).getStories()).withRel("stories"));
   }
    
}
