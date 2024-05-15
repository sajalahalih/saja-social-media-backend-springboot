package com.project.demo.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.demo.ERsecurity.controllers.security.jwt.JwtUtils;

import jakarta.validation.Valid;

//import com.tryproject.demo.service.UserService;

import org.springframework.web.bind.annotation.PutMapping;


@RestController
public class UserController {
      @Autowired
       private  UserRepository repository;
       @Autowired
    private  UserAssembler assembler;
    @Autowired
    private JwtUtils jwtUtils;


  

       public UserController(UserRepository repository2, UserAssembler userAssembler) {
        repository=repository2;
        assembler=userAssembler;
    }

    @GetMapping("/users")
       public CollectionModel<EntityModel<User>> getUsers() {
        List<EntityModel<User>> users=repository.findAll().stream()
        .map(assembler::toModel)
        .collect(Collectors.toList());
      
        return CollectionModel.of(users,linkTo(methodOn(UserController.class).getUsers()).withSelfRel());
   
        
    }

    @GetMapping("/users/{id}")
    public EntityModel<User> getUserById(@PathVariable Long id ) {
     User user=repository.findById(id).orElseThrow();
        return assembler.toModel(user);
    }  


    @PostMapping("/users")
    public ResponseEntity<?> addUser(@Valid @RequestBody User newuser) {
       
    EntityModel<User> entityModel = assembler.toModel(repository.save(newuser));
  
    return ResponseEntity 
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
        .body(entityModel);
    }


      @Autowired
  PasswordEncoder encoder;

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody User newuser,@RequestHeader("Authorization") String jwt) {
        
      
        try{

            User userr; 


            jwt = jwt.substring(7);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
              String username = jwtUtils.getUserNameFromJwtToken(jwt);
               userr=repository.findByUserName(username).orElseThrow();
    
         
            User updatedUser = repository.findById(userr.getId())
            .map(user -> {
                user.setFirstName(newuser.getFirstName());
                user.setLastName(newuser.getLastName());
                user.setUserName(newuser.getUserName());
                user.setGender(newuser.getGender());
                user.setEmail(newuser.getEmail());
                user.setPassword(encoder.encode(newuser.getPassword()));
                user.setFollowers(newuser.getFollowers());
                user.setFollowing(newuser.getFollowing());
                user.setSavedPosts(newuser.getSavedPosts());
                user.setImage(newuser.getImage());
                return repository.save(user);
                
            })
       
            .orElseGet(() -> {
                System.out.println("llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll");
                newuser.setId(userr.getId());
                return repository.save(newuser);
            });
    
        EntityModel<User> entityModel = assembler.toModel(updatedUser);
    
        return  ResponseEntity.ok(entityModel);
    }
} catch (DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email already exists");
    }
        return null; 
    
    }

//     @PutMapping("users/{id}")
// public ResponseEntity<?> updateUser( @PathVariable Long id,@Valid @RequestBody User newuser) {
//     try{

//     User updatedUser = repository.findById(id)
//         .map(user -> {
//             user.setUserName(newuser.getUserName());
//             user.setGender(newuser.getGender());
//             user.setEmail(newuser.getEmail());
//             user.setPassword(newuser.getPassword());
//             user.setFollowers(newuser.getFollowers());
//             user.setFollowing(newuser.getFollowing());
//             user.setSavedPosts(newuser.getSavedPosts());
//             user.setImage(newuser.getImage());
//             return repository.save(user);
//         })
//         .orElseGet(() -> {
//             newuser.setId(id);
//             return repository.save(newuser);
//         });

//     EntityModel<User> entityModel = assembler.toModel(updatedUser);

//     return  ResponseEntity.ok(entityModel);
// } catch (DataIntegrityViolationException ex) {
//     return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email already exists");
// }

// }

 
    //  @GetMapping("/users/search")
    // public   CollectionModel<EntityModel<User>> searchUser(@RequestParam("query") String query){
    //     List<User> users =userService.searchUser(query);

    // List<EntityModel<User>> userslist=    users.stream().map(assembler::toModel)
    //     .collect(Collectors.toList());

    //     return CollectionModel.of(userslist,linkTo(methodOn(UserController.class).searchUser(query)).withSelfRel());
    // }

    @GetMapping("/users/search")
    public CollectionModel<EntityModel<User>> searchUser(@RequestParam("query") String query) {
        List<EntityModel<User>> users = repository.findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(users, linkTo(methodOn(UserController.class).searchUser(query)).withSelfRel());
    }

    
    @DeleteMapping("users/{id}")
    public  ResponseEntity<?> deleteUser(@PathVariable Long id){
        repository.delete(repository.findById(id).orElseThrow(()-> new UserNotFoundException(id)));
    return ResponseEntity.noContent().build();
      

    }

    @PutMapping("users/follow/{userid2}")
    public ResponseEntity< EntityModel<User>> followUserHandeler(@RequestHeader("Authorization") String jwt,@PathVariable Long userid2){
 
        jwt = jwt.substring(7);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
          String username = jwtUtils.getUserNameFromJwtToken(jwt);
          User user=repository.findByUserName(username).orElseThrow();
          


        User user1=repository.findById(user.getId()).orElseThrow(()->new UserNotFoundException(user.getId()));

        User user2=repository.findById(userid2).orElseThrow(()->new UserNotFoundException(userid2));

        user2.getFollowers().add(user1);

        user1.getFollowing().add(user2);

        repository.save(user1);
        repository.save(user2);

        EntityModel<User> entityModel=assembler.toModel(user1);

          return ResponseEntity.ok(entityModel);}
          return null;  
    }

    //users/profile 

    @GetMapping("/users/profile")
    public User getUserFromToken(@RequestHeader("Authorization") String jwt){
      
        jwt = jwt.substring(7);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
          String username = jwtUtils.getUserNameFromJwtToken(jwt);
          User user=repository.findByUserName(username).orElseThrow();
          
        
        System.out.println("dddddddddddddd"+username);
        return user;
        }

        System.out.println("faileddddddddddddddddddddd");

    //    String user=JwtUtils.getuserfrom(jwt);

      //  System.out.println(user+"lllllllllllllllllllllllllll");



      
       return null;

    }

    // @GetMapping("/users/profile")
    // public User getUserFromTokenm(@RequestHeader("Authorization") String jwt){

    //      jwt = jwt.substring(7);
    //     if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
    //       String username = jwtUtils.getUserNameFromJwtToken(jwt);
        
    //     System.out.println("dddddddddddddd"+username);
    //     }

    //     System.out.println("faileddddddddddddddddddddd");


  
       
      
    //    return null;

    // }

   
 
}
