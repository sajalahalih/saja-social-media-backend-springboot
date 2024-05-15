package com.project.demo.Reel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.project.demo.Message.Message;
import com.project.demo.Post.Post;
import com.project.demo.User.User;
import com.project.demo.User.UserNotFoundException;
import com.project.demo.User.UserRepository;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
public class ReelsController {
    @Autowired
    ReelsRepositry reelsRepositry;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ReelsAssembler assembler;
   
 
      public ReelsController(ReelsRepositry reelsRepository, ReelsAssembler reelsAssembler,UserRepository userRepository) {
       this. reelsRepositry=reelsRepository;
       this.assembler=reelsAssembler;
       this.userRepository=userRepository;
    }

    @GetMapping("/reels")
    public CollectionModel<EntityModel<Reels>> findAllReels(){
       List<EntityModel<Reels>> reels=reelsRepositry.findAll().stream()
           .map(assembler::toModel)
           .collect(Collectors.toList());


           return CollectionModel.of(reels,linkTo(methodOn(ReelsController.class).findAllReels()).withSelfRel());
         
    } 

     @GetMapping("/reels/user/{userId}")
    public CollectionModel<EntityModel<Reels>> findUsersReels(@PathVariable Long userId){
        User user=userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));
        List<EntityModel<Reels>> reels=reelsRepositry.findByUserId(userId).stream()
        .map(assembler::toModel)
        .collect(Collectors.toList());;


        return CollectionModel.of(reels,linkTo(methodOn(ReelsController.class).findUsersReels(userId)).withSelfRel());
         
    } 

    @GetMapping("/reels/{reelId}/user/{userId}")
    public EntityModel<Reels> getReelById(@PathVariable Long reelId, @PathVariable Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    Reels reell =reelsRepositry.findById(reelId)
            .filter(reel -> reel.getUser().equals(user))
            .orElseThrow(() -> new ReelsNotFoundException(reelId, userId));

            return assembler.toModel(reell);
}


  

    @PostMapping("reels/user/{userId}")
    public ResponseEntity<EntityModel<Reels>> createReel(@Valid @RequestBody Reels reel,@PathVariable Long userId) {

        Reels newReel = new Reels();
        newReel.setTitle(reel.getTitle());
        newReel.setVideo(reel.getVideo());
        newReel.setUser(reel.getUser());

        EntityModel<Reels> entityModel=assembler.toModel(reelsRepositry.save(newReel));
        
        
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }
    
    @PutMapping("reels/{reelId}/user/{userId}")
    public  ResponseEntity<EntityModel<Reels>> updateReel(@Valid @PathVariable Long reelId, @RequestBody Reels newreel,@PathVariable Long userId) {
        Reels updatedReel = reelsRepositry.findById(reelId)
        .map(reel->{
            reel.setTitle(newreel.getTitle());
            reel.setVideo(newreel.getVideo());
            reel.setLiked(newreel.getLiked());
            reel.setComments(newreel.getComments());
            return reelsRepositry.save(reel);

        })
        .orElseGet(()->{
            newreel.setId(reelId);
            return reelsRepositry.save(newreel);
        });
        EntityModel<Reels> entityModel =assembler.toModel(updatedReel);
        
        return ResponseEntity.ok(entityModel);

    }


    
    @DeleteMapping("reels/{reelId}/user/{userId}")
    public ResponseEntity<?> deleteReel(@PathVariable Long reelId,@PathVariable Long userId){

        Reels reel=reelsRepositry.findById(reelId).orElseThrow(()-> new ReelsNotFoundException(reelId, userId));
        User user=userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));

        if(reel.getUser().getId()!=user.getId())
        return  ResponseEntity.notFound().build();

        reelsRepositry.delete(reel);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("reels/like/{reelId}/user/{userId}")
    public ResponseEntity<EntityModel<Reels>> LikedReel(@PathVariable Long reelId, @PathVariable Long userId) {
       
        Reels reel=reelsRepositry.findById(reelId).orElseThrow(()-> new ReelsNotFoundException(reelId,userId));

        User user=userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));

        if(reel.getLiked().contains(user)){
            reel.getLiked().remove(user);
        }else{
            reel.getLiked().add(user);
        }
        reelsRepositry.save(reel);

           EntityModel<Reels> entityModel = assembler.toModel(reel);

        return new ResponseEntity<EntityModel<Reels>>(entityModel,HttpStatus.ACCEPTED);
    }


     

}

