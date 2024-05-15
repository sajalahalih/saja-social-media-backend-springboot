package com.project.demo.Reel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ReelsAssembler  implements  RepresentationModelAssembler <Reels,EntityModel<Reels>>  {

   
    @Override
    public EntityModel<Reels> toModel(Reels reel) {
        return EntityModel.of(reel, //
        linkTo(methodOn(ReelsController.class).getReelById(reel.getId(),reel.getUser().getId())).withSelfRel(),
        linkTo(methodOn(ReelsController.class).findAllReels()).withRel("reels"));
   }
    
}
