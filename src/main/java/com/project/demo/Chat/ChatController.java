package com.project.demo.Chat;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
import org.springframework.web.bind.annotation.RestController;

import com.project.demo.ERsecurity.controllers.security.jwt.JwtUtils;
import com.project.demo.Message.Message;
import com.project.demo.Message.MessageRepository;
import com.project.demo.Post.Post;
import com.project.demo.Post.PostNotFoundException;
import com.project.demo.User.User;
import com.project.demo.User.UserNotFoundException;
import com.project.demo.User.UserRepository;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
public class ChatController {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private  ChatAssembler assembler;
    @Autowired
    private UserRepository userRepository;

     @Autowired
    private JwtUtils jwtUtils;


    @Autowired
    MessageRepository messageRepository;

     public ChatController(ChatAssembler chatAssembler, ChatRepository chatRepository2, UserRepository userRepository2,MessageRepository messageRepository) {
       this.chatRepository=chatRepository2;
       this.assembler=chatAssembler;
       this.userRepository=userRepository2;

       this.messageRepository=messageRepository;
    }

    @GetMapping("/chats")
    public CollectionModel<EntityModel<Chat>> getChats() {
        List<EntityModel<Chat>> chats= chatRepository.findAll().stream()
        .map(assembler::toModel)
        .collect(Collectors.toList());

        return CollectionModel.of(chats,linkTo(methodOn(ChatController.class).getChats()).withSelfRel());

    }
 
    @GetMapping("/chats/{chatId}")
    public EntityModel<Chat> getChatById(@PathVariable Long chatId) {

      //  User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
         Chat chatt=chatRepository.findById(chatId)
        // .filter(comment->comment.getUser().equals(user))
        // .filter(comment->comment.getPost().equals(post))
        
        .orElseThrow(()-> new ChatNotFoundException(chatId));
   

        return assembler.toModel(chatt);
    }

    @PostMapping("/chats/create")
    public ResponseEntity< EntityModel<Chat>> createChat( @RequestBody CreateChatRequest req,@RequestHeader("Authorization") String jwt ){

         jwt = jwt.substring(7);
        if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        User userr = userRepository.findByUserName(username).orElseThrow();
        User user = userRepository.findById(userr.getId()).orElseThrow(() -> new UserNotFoundException(userr.getId()));
    


        System.out.println(req);
    
        User reqUser=userRepository.findById(user.getId()).orElseThrow(()-> new UserNotFoundException(user.getId()));
        User user2=userRepository.findById(req.getUserId()).orElseThrow(()-> new UserNotFoundException(req.getUserId()));

        Chat isExist=chatRepository.findChatByUsersId(reqUser,user2);

        if(isExist!=null){
            
            return ResponseEntity.ok(assembler.toModel(isExist));
        }
        Chat chat=new Chat();
        chat.setChatName(user2.getUserName());
        chat.getUsers().add(user2);
        chat.getUsers().add(reqUser);
        chat.setTimestamp(LocalDateTime.now());
        chatRepository.save(chat);
        EntityModel<Chat> entityModel=assembler.toModel(chat);
     
        return  ResponseEntity 
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
        .body(entityModel);
    }
    
  
    @GetMapping("/chats/user")
    public ResponseEntity< CollectionModel<EntityModel<Chat>>> finsUsersChat(@RequestHeader("Authorization") String jwt){
        jwt = jwt.substring(7);
        if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
           
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
    
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        User userr = userRepository.findByUserName(username).orElseThrow();
        User user = userRepository.findById(userr.getId()).orElseThrow(() -> new UserNotFoundException(userr.getId()));
    

       
        List<EntityModel<Chat>> chats= chatRepository.findByUsersId(user.getId()).stream()
        .map(assembler::toModel)
        .collect(Collectors.toList());

        CollectionModel<EntityModel<Chat>> collectionModel = CollectionModel.of(chats, linkTo(methodOn(ChatController.class).getChats()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @DeleteMapping("/chats/{chatId}")
    public ResponseEntity<?> deleteChat(@PathVariable Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException(chatId));

        chatRepository.delete(chat);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/chats/{chatId}/clear-messages")
    public ResponseEntity<?> clearChatMessages(@PathVariable Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException(chatId));

        List<Message> messages = chat.getMessages();
        messages.forEach(message -> message.setChat(null));
        messageRepository.saveAll(messages);

        return ResponseEntity.noContent().build();
    }
    @PostMapping("/chats/{chatId}")
    public ResponseEntity<EntityModel<Chat>> updateChat(@Valid @RequestBody Chat updatedChat, @PathVariable Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
    
        if (chat == null) {
            throw new ChatNotFoundException(chatId);
        }
    
        chat.setChatName(updatedChat.getChatName());
        chat.setChatImage(updatedChat.getChatImage());
    
        Chat savedChat = chatRepository.save(chat);
    
        EntityModel<Chat> entityModel = assembler.toModel(savedChat);
        return ResponseEntity.ok(entityModel);
        
    }
    

    
}
