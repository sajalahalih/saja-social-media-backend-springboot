package com.project.demo;


import com.project.demo.Chat.Chat;
import com.project.demo.Chat.ChatAssembler;
import com.project.demo.Chat.ChatController;
import com.project.demo.Chat.ChatNotFoundException;
import com.project.demo.Chat.ChatRepository;
import com.project.demo.Chat.CreateChatRequest;
import com.project.demo.Message.Message;
import com.project.demo.Message.MessageRepository;
import com.project.demo.User.User;
import com.project.demo.User.UserAssembler;
import com.project.demo.User.UserController;
import com.project.demo.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/test")
@SpringBootTest
@AutoConfigureMockMvc
public class TestChatController {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatAssembler chatAssembler;


    @InjectMocks
    @Autowired
    private ChatController chatController;

    // @Autowired
    // WebApplicationContext webApplicationContext;

    @Mock
    MessageRepository messageRepository;

    @BeforeEach
    public void setUp() {
        chatAssembler=new ChatAssembler();
        chatController=new ChatController(chatAssembler,chatRepository,userRepository,messageRepository);
        
        UserAssembler userAssembler=new UserAssembler();
        UserController userController=new UserController(userRepository, userAssembler);

        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }

    @Test
    public void testGetChats() throws Exception {
        when(chatRepository.findAll()).thenReturn(Collections.singletonList(createTestChat()));
        mockMvc.perform(get("/chats"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetChatById() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);

        Long userId2 = 2L;
        User user2 = new User("testUser2", "test@example.com", "password");
        user.setId(userId2);
        List list=new ArrayList<>();
        list.add(user2);
        list.add(user);


        Chat chat=new Chat("name", "image", list);
        Long chatId = 1L;
        chat.setId(chatId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(chatRepository.save(any(Chat.class))).thenReturn(chat);

       
        mockMvc.perform(get("/chats/{chatId}", chatId))
                .andExpect(status().isOk());
    }


    @Test
    public void testCreateChat() throws Exception {
        Long userId = 1L;
        User reqUser = new User("testUser1", "test1@example.com", "password");
        reqUser.setId(userId);
        User user2 = new User("testUser2", "test2@example.com", "password");
        user2.setId(userId + 1);

        CreateChatRequest request = new CreateChatRequest(user2.getId());

        Chat chat = new Chat();
        chat.setId(1L);
        chat.setUsers(List.of(reqUser, user2));
        chat.setTimestamp(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(reqUser));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(chatRepository.findChatByUsersId(reqUser, user2)).thenReturn(null);
        when(chatRepository.save(chat)).thenReturn(chat);
      //  when(chatAssembler.toModel(chat)).thenReturn(new EntityModel<>(chat));

        mockMvc.perform(post("/chats/create/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": " + user2.getId() + "}"))
                .andExpect(status().isCreated());
    }

    
    @Test
    public void testFindUsersChat() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);

        Long userId2 = 2L;
        User user2 = new User("testUser2", "test@example.com", "password");
        user.setId(userId2);
        List list=new ArrayList<>();
        list.add(user2);
        list.add(user);


        Chat chat=new Chat("name", "image", list);
        Long chatId = 1L;
        chat.setId(chatId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(chatRepository.save(any(Chat.class))).thenReturn(chat);
 mockMvc.perform(get("/chats/user/{userId}", userId))
                .andExpect(status().isOk());
    }

    




    @Test
    public void testDeleteChat() throws Exception {
        Long chatId = 1L;
        Chat chat = createTestChat();
        chat.setId(chatId);

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

        mockMvc.perform(delete("/chats/{chatId}", chatId))
                .andExpect(status().isNoContent());
    }

   

    @Test
    public void testClearChatMessages() throws Exception {
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
   

        mockMvc.perform(delete("/chats/{chatId}/clear-messages", chatId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateChat() throws Exception {
        Long chatId = 1L;
        Chat originalChat = new Chat("Original Chat Name", "original.jpg", new ArrayList<>());
        originalChat.setId(chatId);
        
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(originalChat));
        when(chatRepository.save(any(Chat.class))).thenReturn(originalChat);
        
        mockMvc.perform(post("/chats/{chatId}", chatId) // Change post to put
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"chatName\":\"Updated Chat Name\",\"chatImage\":\"updated.jpg\",\"users\":[]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatName").value("Updated Chat Name"))
                .andExpect(jsonPath("$.chatImage").value("updated.jpg"));
    }
  


   
    private Chat createTestChat() {
        Chat chat = new Chat();
        chat.setChatName("Test Chat");
        chat.setChatImage("test.jpg");
        chat.setUsers(new ArrayList<>());
        chat.setTimestamp(LocalDateTime.now());
        chat.setMessages(new ArrayList<>());
        return chat;
    }

    

    // private Chat createTestChat() {
    //     Chat chat = new Chat("Test Chat", "test.jpg", new ArrayList<>());
    //     chat.setId(1L);
    //     chat.setTimestamp(LocalDateTime.now());
    //     return chat;
    // }
}
