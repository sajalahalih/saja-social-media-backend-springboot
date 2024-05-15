package com.project.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Collections;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/test")
@SpringBootTest
@AutoConfigureMockMvc
public class TestUser {

  
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepository repository;

    @InjectMocks
    @Autowired
    private UserController userController;
    User user;
  

    @BeforeEach
public void setUp() {
    
    UserAssembler userAssembler = new UserAssembler();
    userController = new UserController(repository, userAssembler); // Adjust constructor accordingly
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
   
    user = new User("testUser", "test@example.com", "password");
    user.setId(100L);
    
}

 
    @Test
   public void d() throws Exception{
        this.mockMvc.perform(get("/users")).andExpect(status().isOk());

    }

    @Test
public void testGetUsers() throws Exception {
    when(repository.findAll()).thenReturn(Collections.singletonList(user));

    mockMvc.perform(get("/users"))
        .andExpect(status().isOk());
}




    @Test
    public void testGetUserById() throws Exception {
        User user = new User("testUser", "test@example.com", "password");
        user.setId(1L);

        when(repository.findById(1L)).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("testUser"));
    }

    @Test
    public void testAddUser() throws Exception {
        User user = new User("testUser", "test@example.com", "password");

        when(repository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("testUser"));
    }

    @Test
    public void testAddUser_ValidationFailure() throws Exception {
        // Sending a request with invalid data (empty or too long userName, email, or password)
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                        + "\"userName\":\"\","
                        + "\"gender\":\"MALE\","
                        + "\"email\":\"\","
                        + "\"password\":\"\","
                        + "\"followers\":[],"
                        + "\"following\":[],"
                        + "\"savedPosts\":[]"
                    + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = new User("testUser", "test@example.com", "password");
        user.setId(1L);

        when(repository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(repository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("testUser"));
    }

    @Test
    public void testUpdateUser_UsernameConflict() throws Exception {
        User user = new User("testUser1", "test@example.com", "password");
        user.setId(1L);
    
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenReturn(user);
    
        User user2 = new User("testUser", "test2@example.com", "password");
        user2.setId(2L);
    
        when(repository.findById(2L)).thenReturn(Optional.of(user2));
        when(repository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Username already exists"));

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                + "\"userName\":\"testUser\","
                + "\"gender\":\"M\","
                + "\"email\":\"test2@example.com\","
                + "\"password\":\"password\","
                + "\"followers\":[],"
                + "\"following\":[],"
                + "\"savedPosts\":[]"
            + "}"))
            .andExpect(status().isConflict()); 
    }
    


    @Test
    public void testSearchUser() throws Exception {
        when(repository.findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase("test", "test"))
                .thenReturn(Collections.singletonList(user));
    
        mockMvc.perform(get("/users/search?query=test"))
                .andExpect(status().isOk());
                
    }
    

    @Test
    public void testDeleteUser() throws Exception {
        User user = new User("testUser", "test@example.com", "password");
        user.setId(1L);

        when(repository.findById(1L)).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testFollowUserHandeler() throws Exception {
        User user1 = new User("user1", "user1@example.com", "password");
        user1.setId(1L);
        User user2 = new User("user2", "user2@example.com", "password");
        user2.setId(2L);

        when(repository.findById(1L)).thenReturn(java.util.Optional.of(user1));
        when(repository.findById(2L)).thenReturn(java.util.Optional.of(user2));

        mockMvc.perform(put("/users/follow/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("user1"));
    }
}