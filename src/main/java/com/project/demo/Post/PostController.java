package com.project.demo.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.project.demo.ERsecurity.controllers.security.jwt.JwtUtils;
import com.project.demo.User.User;
import com.project.demo.User.UserNotFoundException;
import com.project.demo.User.UserRepository;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;




@RestController
public class PostController {
 
    
    private final PostRepositry repository;

    private final UserRepository userRepository;

    private final PostAssembler assembler;
      @Autowired
    private JwtUtils jwtUtils;

 

    public PostController(PostRepositry repository ,PostAssembler assembler,UserRepository userRepository ) {
        this.repository = repository;
        this.assembler=assembler;
        this.userRepository=userRepository;
    }

     @GetMapping("/posts")
    public CollectionModel<EntityModel<Post>> getPosts() {
        List<EntityModel<Post>> posts=repository.findAll().stream()
        .map(assembler::toModel)
        .collect(Collectors.toList());
      
        return CollectionModel.of(posts,linkTo(methodOn(PostController.class).getPosts()).withSelfRel());

    
    }

      @GetMapping("/posts/{id}")
    public EntityModel<Post> getPostById(@PathVariable Long id ) {
     Post post=repository.findById(id).orElseThrow(()-> new PostNotFoundException(id));
        return assembler.toModel(post);

    
    } 
  
      @GetMapping("/posts/user/{userId}")
    public CollectionModel<EntityModel<Post>> findUsersPosts(@PathVariable Long userId) {
    List<EntityModel<Post>> posts = repository.findByUser_Id(userId).stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

    return CollectionModel.of(posts, linkTo(methodOn(PostController.class).findUsersPosts(userId)).withSelfRel());


}
@PostMapping("/posts/user")
public ResponseEntity<?> createPost( @RequestBody Post post,@RequestHeader("Authorization") String jwt) {
    
    User userr; 


    jwt = jwt.substring(7);
    if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
      String username = jwtUtils.getUserNameFromJwtToken(jwt);
       userr=userRepository.findByUserName(username).orElseThrow();

    User user = userRepository.findById(userr.getId()).orElseThrow(() -> new UserNotFoundException(userr.getId()));

    if (post.getCaption() == null && post.getImage() == null && post.getVideo() == null) {
        return ResponseEntity.badRequest().body("At least one of caption, image, or video is required.");
    }

    Post newPost = new Post();
    newPost.setCaption(post.getCaption());
    newPost.setCreatedAt(LocalDateTime.now());
    newPost.setImage(post.getImage());
    newPost.setVideo(post.getVideo());
    newPost.setUser(user);


    Post createdPost = repository.save(newPost);

  
    EntityModel<Post> entityModel = assembler.toModel(createdPost);

    return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);}

    return null;
}


//?????????????????
@PutMapping("/posts/{id}/user")
public ResponseEntity<?> updatePost(@PathVariable Long id,  @RequestBody Post newPost,@RequestHeader("Authorization") String jwt) {
    User userr; 


    jwt = jwt.substring(7);
    if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
      String username = jwtUtils.getUserNameFromJwtToken(jwt);
       userr=userRepository.findByUserName(username).orElseThrow();

    User user = userRepository.findById(userr.getId()).orElseThrow(() -> new UserNotFoundException(userr.getId()));

    if (newPost.getCaption() == null && newPost.getImage() == null && newPost.getVideo() == null) {
        return ResponseEntity.badRequest().body("At least one of caption, image, or video is required.");
    }

    Post updatedPost = repository.findById(id)
            .map(post -> {
                post.setCaption(newPost.getCaption());
                post.setCreatedAt(LocalDateTime.now());
                post.setImage(newPost.getImage());
                post.setVideo(newPost.getVideo());
                post.setUser(user); 
               
                post.setLiked(newPost.getLiked());
                post.setComments(newPost.getComments());

                return repository.save(post);
            })
            .orElseGet(() -> {
                newPost.setId(id);
                newPost.setUser(user); 
                return repository.save(newPost);
            });

    EntityModel<Post> entityModel = assembler.toModel(updatedPost);


    return ResponseEntity.ok().body(entityModel);}

    return null;
}


       @DeleteMapping("posts/{postid}/user")
    public  ResponseEntity<?> deletPost(@PathVariable Long postid , @RequestHeader("Authorization") String jwt) throws Exception{

        User userr; 


        jwt = jwt.substring(7);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
          String username = jwtUtils.getUserNameFromJwtToken(jwt);
           userr=userRepository.findByUserName(username).orElseThrow();
    

        Post post=repository.findById(postid).orElseThrow(()->new PostNotFoundException(postid));
        User user=userRepository.findById(userr.getId()).orElseThrow(()->new UserNotFoundException(userr.getId()));


        if(post.getUser().getId()!= user.getId()){
            throw new Exception("you cannot delete another user post");
        }
        repository.delete(post);

    
     return ResponseEntity.noContent().build();}
     return null;

  
      

    }


    @PutMapping("posts/save/{postid}/user")
    public ResponseEntity<EntityModel<Post>> savedPostHandler(@PathVariable Long postid, @RequestHeader("Authorization") String jwt) {

        User userr; 


        jwt = jwt.substring(7);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
          String username = jwtUtils.getUserNameFromJwtToken(jwt);
           userr=userRepository.findByUserName(username).orElseThrow();
    


        Post post = repository.findById(postid).orElseThrow(()-> new PostNotFoundException(postid));

        User user=userRepository.findById(userr.getId()).orElseThrow(()->new UserNotFoundException(userr.getId()));
        
        if(user.getSavedPosts().contains(post)){
            user.getSavedPosts().remove(post);
        }else{
            user.getSavedPosts().add(post);
        }
        userRepository.save(user);
   
        EntityModel<Post> entityModel = assembler.toModel(post);
    
        return new ResponseEntity<EntityModel<Post>>(entityModel,HttpStatus.ACCEPTED);}

        return null;
    }
    // @PutMapping("/posts/like/{postid}/user")
    // public ResponseEntity<EntityModel<Post>> unlikedPostHandlerr(@PathVariable Long postid, @RequestHeader("Authorization") String jwt) {
    //     try {
    //         jwt = jwt.substring(7);
    //         if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
    //             return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    //         }

    //         String username = jwtUtils.getUserNameFromJwtToken(jwt);
    //         User user = userRepository.findByUserName(username)
    //             .orElseThrow();
    //         Post post = repository.findById(postid)
    //             .orElseThrow(() -> new PostNotFoundException(postid));

    //         if (post.getLiked().contains(user)) {
    //             post.getLiked().remove(user);
    //         } else {
    //             post.getLiked().add(user);
    //         }

    //         repository.save(post);
    //         EntityModel<Post> entityModel = EntityModel.of(post);
    //         return new ResponseEntity<>(entityModel, HttpStatus.ACCEPTED);
    //     } catch (UserNotFoundException e) {
    //         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //     } catch (PostNotFoundException e) {
    //         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //     } catch (Exception e) {
    //         return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    //     }
    // }

    @PutMapping("posts/like/{postid}/user")
    public synchronized ResponseEntity<EntityModel<Post>> likedPostHandler(@PathVariable Long postid, @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        User userr = userRepository.findByUserName(username).orElseThrow();
        Post post = repository.findById(postid).orElseThrow(() -> new PostNotFoundException(postid));
        User user = userRepository.findById(userr.getId()).orElseThrow(() -> new UserNotFoundException(userr.getId()));
    
        boolean alreadyLiked = post.getLiked().contains(user);
        
        if (alreadyLiked) {
            post.getLiked().remove(user);
        } else {
            post.getLiked().add(user);
        }
    
        repository.save(post);
        EntityModel<Post> entityModel = assembler.toModel(post);
    
        return new ResponseEntity<>(entityModel, HttpStatus.ACCEPTED);
    }
    

    @GetMapping("/posts/user/following") //on security i authorize this link
    public CollectionModel<EntityModel<Post>> getFollowingPosts( @RequestHeader("Authorization") String jwt) {
        User userr; 


        jwt = jwt.substring(7);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
          String username = jwtUtils.getUserNameFromJwtToken(jwt);
           userr=userRepository.findByUserName(username).orElseThrow();
    
        User user = userRepository.findById(userr.getId()).orElseThrow(() -> new UserNotFoundException(userr.getId()));

       // List<Post> followings = user.getFollowing().stream().map(user->PostRepositry.findByUser_Id(user.getId())).collect(Collectors.toList());

        List<Post>followingPosts=repository.findByUserIn(user.getFollowing());

        List<EntityModel<Post>> posts = followingPosts.stream().map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(posts, linkTo(methodOn(PostController.class).getFollowingPosts(jwt)).withSelfRel());}
        return null;
    }




    @PostMapping("/posts/share/{postId}/user")
public ResponseEntity<?> sharePost(@PathVariable Long postId,  @RequestHeader("Authorization") String jwt) {
    User userr; 


    jwt = jwt.substring(7);
    if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
      String username = jwtUtils.getUserNameFromJwtToken(jwt);
       userr=userRepository.findByUserName(username).orElseThrow();

    
    Post originalPost = repository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

    User sharingUser = userRepository.findById(userr.getId()).orElseThrow(() -> new UserNotFoundException(userr.getId()));

    Post sharedPost = new Post();
    sharedPost.setCaption(originalPost.getCaption());
    sharedPost.setImage(originalPost.getImage());
    sharedPost.setVideo(originalPost.getVideo());
    sharedPost.setUser(sharingUser); 
    sharedPost.setCreatedAt(LocalDateTime.now());

    Post createdSharedPost = repository.save(sharedPost);

     EntityModel<Post> entityModel = assembler.toModel(createdSharedPost);

     return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);}
     return null;
}


 


    
}
