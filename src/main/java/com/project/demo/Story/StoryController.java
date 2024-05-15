package com.project.demo.Story;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import com.project.demo.Post.Post;
import com.project.demo.Reel.Reels;
import com.project.demo.Reel.ReelsController;
import com.project.demo.Reel.ReelsNotFoundException;
import com.project.demo.User.User;
import com.project.demo.User.UserNotFoundException;
import com.project.demo.User.UserRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class StoryController {
    @Autowired
    private final StoryRepository storyRepository;
    @Autowired
    private final StoryAssembler assembler;
    @Autowired
    UserRepository userRepository;

    @Autowired
    public StoryController(StoryRepository storyRepository, StoryAssembler assembler, UserRepository userRepository) {
        this.storyRepository = storyRepository;
        this.assembler = assembler;
        this.userRepository = userRepository;
    }

    @GetMapping("/stories")
    public  CollectionModel<EntityModel<Story>> getStories() {

         List<EntityModel<Story>> stories=storyRepository.findAll()
        .stream()
        .map(assembler::toModel)
        .collect(Collectors.toList());
      
        return CollectionModel.of(stories,linkTo(methodOn(StoryController.class).getStories()).withSelfRel());

    }

    @GetMapping("/stories/{storyId}/user/{userId}")
public ResponseEntity<?> getStoryById(@PathVariable Long storyId, @PathVariable Long userId) {
    try {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Story story = storyRepository.findById(storyId)
                                      .filter(s -> s.getUser().equals(user))
                                      .orElseThrow(() -> new StoryNotFoundException(storyId));

        EntityModel<Story> entityModel = assembler.toModel(story);
        return ResponseEntity.ok(entityModel);
    } catch (UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
    } catch (StoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Story not found with ID: " + storyId);
    }
}
@GetMapping("/stories/user/{userId}")
public ResponseEntity<?> findUsersStories(@PathVariable Long userId){
    try {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<EntityModel<Story>> stories = storyRepository.findByUserId(userId)
                                                            .stream()
                                                            .map(assembler::toModel)
                                                            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(stories, linkTo(methodOn(ReelsController.class).findUsersReels(userId)).withSelfRel()));
    } catch (UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
    }
}


    @PostMapping("stories/user/{userId}")
    public ResponseEntity<?> createStory(@Validated @RequestBody Story story,@PathVariable Long userId) {

        if (story.getImage() == null || story.getImage().isBlank()) {
            if (story.getVideo() == null || story.getVideo().isBlank()) {
                return ResponseEntity.badRequest().body("At least one of image or video must not be blank");
            }
        }

        Story newStory = new Story();
        newStory.setCaption(story.getCaption());
        newStory.setVideo(story.getVideo());
        newStory.setUser(story.getUser());
        newStory.setCreatedAt(LocalDateTime.now());
        newStory.setImage(story.getImage());
        newStory.setLiked(story.getLiked());
        

        EntityModel<Story> entityModel=assembler.toModel(storyRepository.save(newStory));
        
        
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }
    

    @DeleteMapping("stories/{storyId}/user/{userId}")
    public ResponseEntity<?> deleteStory(@PathVariable Long storyId,@PathVariable Long userId){

        Story story=storyRepository.findById(storyId).orElseThrow(()-> new StoryNotFoundException( storyId));
        User user=userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));

        if(story.getUser().getId()!=user.getId())
        return  ResponseEntity.notFound().build();

        storyRepository.delete(story);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("stories/like/{storyId}/user/{userId}")
public ResponseEntity<EntityModel<Story>> LikedStory(@PathVariable Long storyId, @PathVariable Long userId) {
    Story story = storyRepository.findById(storyId).orElseThrow(() -> new StoryNotFoundException(storyId));
    User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    List<User> liked = new ArrayList<>(story.getLiked()); // Create a modifiable list

    if (liked.contains(user)) {
        liked.remove(user);
    } else {
        liked.add(user);
    }

    story.setLiked(liked); // Set the modifiable list back to the story

    storyRepository.save(story);

    EntityModel<Story> entityModel = assembler.toModel(story);

    return new ResponseEntity<>(entityModel, HttpStatus.ACCEPTED);
}


    
    
}
