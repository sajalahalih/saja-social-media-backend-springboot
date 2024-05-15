package com.project.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.Chat.Chat;
import com.project.demo.Chat.ChatRepository;
import com.project.demo.Message.Message;
import com.project.demo.Message.MessageAssembler;
import com.project.demo.Message.MessageController;
import com.project.demo.Message.MessageRepository;
import com.project.demo.User.User;
import com.project.demo.User.UserAssembler;
import com.project.demo.User.UserController;
import com.project.demo.User.UserRepository;
import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/test")
@SpringBootTest
@AutoConfigureMockMvc
public class TestMessageController {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageAssembler messageAssembler;

    @Mock
    private ChatRepository chatRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    @Autowired
    private MessageController messageController;

    // @Autowired
    // WebApplicationContext webApplicationContext;
    @BeforeEach
    public void setUp() {
        messageAssembler=new MessageAssembler();

        messageController=new MessageController(messageRepository,userRepository,messageAssembler,chatRepository);

        UserAssembler userAssembler=new UserAssembler();
        UserController userController=new UserController(userRepository, userAssembler);

       // mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
    }

    @Test
    public void testGetMessages() throws Exception {
      
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("Test Content 1", "test1.jpg", new User(), new Chat()));
        messages.add(new Message("Test Content 2", "test2.jpg", new User(), new Chat()));

       
        when(messageRepository.findAll()).thenReturn(messages);

        mockMvc.perform(get("/chats/messages"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetMessageById() throws Exception {
        
        Long messageId = 1L;
        Long chatId = 1L;
        Message message = new Message("Test Content", "test.jpg", new User(), new Chat());
        message.setId(messageId);

      
        when(messageRepository.findByChatId(chatId)).thenReturn(List.of(message));

        mockMvc.perform(get("/chats/{chatId}/messages/{messageId}",  chatId,messageId))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteMessage() throws Exception {
       
                 Long userId = 1L;
                 User user = new User("testUser", "test@example.com", "password");
                user.setId(userId);

                Long userId2 = 2L;
                User user2 = new User("testUser2", "test2@example.com", "password");
                user.setId(userId2);

                Long chatId = 1L;
                List<User> users = new ArrayList<>();
                 users.add(user);
               users.add(user2);
                Chat chat = new Chat("chat", "image", users);
                chat.setId(chatId);

                Long messageId = 1L;
                Message message = new Message("Test Content", "test.jpg", user, chat);
                message.setId(messageId);





                when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));
                when(userRepository.findById(userId)).thenReturn(Optional.of(user));
                when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

                when(userRepository.save(any(User.class))).thenReturn(user);
                when(userRepository.save(any(User.class))).thenReturn(user2);
               
                when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
                when(messageRepository.save(any(Message.class))).thenReturn(message);
                when(messageRepository.findByChatId(chatId)).thenReturn(List.of(message));
               
        // Perform request and assert
        mockMvc.perform(delete("/chats/{chatId}/messages/{messageId}",  chatId,messageId))
                .andExpect(status().isNoContent());
    }


    @Test
    public void testCreateMessage() throws Exception {
       
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
       user.setId(userId);

       Long userId2 = 2L;
       User user2 = new User("testUser2", "test2@example.com", "password");
       user.setId(userId2);

       Long chatId = 1L;
       List<User> users = new ArrayList<>();
        users.add(user);
      users.add(user2);
       Chat chat = new Chat("chat", "image", users);
       chat.setId(chatId);

       Long messageId = 1L;
       Message message = new Message("Test Content", "test.jpg", user, chat);
       message.setId(messageId);





       when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));
       when(userRepository.findById(userId)).thenReturn(Optional.of(user));
       when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
       when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
       when(messageRepository.save(any(Message.class))).thenReturn(message);


        // Performing POST request
        mockMvc.perform(post("/chats/{chatId}/messages/user/{userId}", chatId, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"content\":\"Test Content\",\"image\":\"test.jpg\"}")
            )
            .andExpect(status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists()); 
    }

  
    


    @Test
    public void testCreateMessage_UnauthorizedUser() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
       user.setId(userId);

       Long userId2 = 2L;
       User user2 = new User("testUser2", "test2@example.com", "password");
       user.setId(userId2);

       Long chatId = 1L;
       List<User> users = new ArrayList<>();
        users.add(user);
    //   users.add(user2);
       Chat chat = new Chat("chat", "image", users);
       chat.setId(chatId);

       Long messageId = 1L;
       Message message = new Message("Test Content", "test.jpg", user, chat);
       message.setId(messageId);





       when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));
       when(userRepository.findById(userId)).thenReturn(Optional.of(user));
       when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
       when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
       when(messageRepository.save(any(Message.class))).thenReturn(message);


        // Performing POST request
        mockMvc.perform(post("/chats/{chatId}/messages/user/{userId}", chatId, userId2)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"content\":\"Test Content\",\"image\":\"test.jpg\"}")
            )
            .andExpect(status().isForbidden());
    }
}
