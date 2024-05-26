package com.project.demo.Message;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.project.demo.Chat.Chat;
import com.project.demo.Chat.ChatNotFoundException;
import com.project.demo.Chat.ChatRepository;
import com.project.demo.ERsecurity.controllers.security.jwt.JwtUtils;
import com.project.demo.User.User;
import com.project.demo.User.UserNotFoundException;
import com.project.demo.User.UserRepository;

@RestController
public class MessageController {
          @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageAssembler assembler;
    @Autowired
    private ChatRepository chatRepository;

    

    

    public MessageController(MessageRepository messageRepository, UserRepository userRepository,
            MessageAssembler assembler, ChatRepository chatRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.assembler = assembler;
        this.chatRepository = chatRepository;
    }

    @GetMapping("chats/messages")
    public CollectionModel<EntityModel<Message>> getMessages(){
        List<EntityModel<Message>> messages=messageRepository.findAll().stream()
        .map(assembler::toModel)
        .collect(Collectors.toList());
      
        return CollectionModel.of(messages,linkTo(methodOn(MessageController.class).getMessages()).withSelfRel());
    }
 
    @GetMapping("/chats/{chatId}/messages/{messageId}")
    public  EntityModel<Message> getMessageById(@PathVariable Long messageId,@PathVariable Long chatId){
        Message message=messageRepository.findByChatId(chatId).stream()
        .filter(m->m.getId().equals(messageId)).findFirst()
        .orElseThrow(()-> new MessageNotFoundException(messageId));

        return assembler.toModel(message);



    }

    @PostMapping("/chats/{chatId}/messages/user")
    public ResponseEntity< EntityModel<Message>> creatMessage(@RequestBody Message req,@PathVariable Long chatId,@RequestHeader("Authorization") String jwt){
        User userr; 


        jwt = jwt.substring(7);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
          String username = jwtUtils.getUserNameFromJwtToken(jwt);
           userr=userRepository.findByUserName(username).orElseThrow();
           long userId=userr.getId();
    

         User user=userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));

         Message message=new Message();
         Chat chat=chatRepository.findById(chatId).orElseThrow(()-> new ChatNotFoundException(chatId));
         
         if(!(chat.getUsers().contains(user))){
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
         }
         
        message.setChat(chat);
        message.setContent(req.getContent());
        message.setImage(req.getImage());
        message.setUser(user);
        message.setTimestamp(LocalDateTime.now());
        Message savedMessage=messageRepository.save(message);

        chat.getMessages().add(savedMessage);
        chatRepository.save(chat);
        
        EntityModel<Message> entityModel=assembler.toModel(savedMessage);
        return  ResponseEntity 
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
        .body(entityModel);}
        return null;
    }
    
     @DeleteMapping("/chats/{chatId}/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId,@PathVariable Long chatId){

        Message message=messageRepository.findByChatId(chatId).stream()
        .filter(m->m.getId().equals(messageId)).findFirst()
        .orElseThrow(()-> new MessageNotFoundException(messageId));

        Chat chat=chatRepository.findById(chatId).orElseThrow(()-> new ChatNotFoundException(chatId));

        if(message.getChat().getId()!=chat.getId())
        return  ResponseEntity.notFound().build();

        messageRepository.delete(message);

        return ResponseEntity.noContent().build();
    }


}
