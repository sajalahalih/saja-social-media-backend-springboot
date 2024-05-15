package com.project.demo;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.Reel.Reels;
import com.project.demo.Reel.ReelsAssembler;
import com.project.demo.Reel.ReelsController;
import com.project.demo.Reel.ReelsRepositry;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/test")
@SpringBootTest
@AutoConfigureMockMvc
public class TestReelsController {

    @Autowired
    private MockMvc mockMvc;

 

    @Mock
    private ReelsRepositry reelsRepository;

    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    @Autowired
    private ReelsController reelsController;

    @BeforeEach
    public void setUp() {
        ReelsAssembler reelsAssembler=new ReelsAssembler();
        reelsController=new ReelsController(reelsRepository,reelsAssembler,userRepository);

        UserAssembler userAssembler=new UserAssembler();
        UserController userController=new UserController(userRepository, userAssembler);

        mockMvc = MockMvcBuilders.standaloneSetup(reelsController).build();
    }

    @Test
    public void testGetAllReels() throws Exception {
        when(reelsRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reels"))
               .andExpect(status().isOk());
             
    }

    @Test
    public void testGetUserReels() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);

        Reels reel=new Reels("reel", "video", user);
        Long reelId=1l;
        reel.setId(reelId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reelsRepository.findById(reelId)).thenReturn(Optional.of(reel));
        when(reelsRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reels/user/{userId}", userId))
               .andExpect(status().isOk());
    }

    @Test
    public void testGetReelById() throws Exception {
        Long userId = 1L;
        Long reelId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        Reels reel = new Reels("Test Reel", "test-video.mp4", user);
        reel.setId(reelId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reelsRepository.findById(reelId)).thenReturn(Optional.of(reel));

        mockMvc.perform(get("/reels/{reelId}/user/{userId}", reelId, userId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.title").value("Test Reel"))
               .andExpect(jsonPath("$.video").value("test-video.mp4"));
    }

    @Test
    public void testCreateReel_InvalidVideoUrl() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        Reels reel = new Reels("Test Reel", "invalid-url", user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reelsRepository.save(any(Reels.class))).thenReturn(reel);

        mockMvc.perform(post("/reels/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(reel)))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateReel() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        Reels reel = new Reels("Test Reel", "https://www.example.com/video.mp4", user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reelsRepository.save(any(Reels.class))).thenReturn(reel);

        mockMvc.perform(post("/reels/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(reel)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.title").value("Test Reel"))
               .andExpect(jsonPath("$.video").value("https://www.example.com/video.mp4"));
    }

    @Test
    public void testUpdateReel() throws Exception {
        Long userId = 1L;
        Long reelId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        Reels reel = new Reels("Test Reel", "test-video.mp4", user);
        reel.setId(reelId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reelsRepository.findById(reelId)).thenReturn(Optional.of(reel));
        when(reelsRepository.save(any(Reels.class))).thenReturn(reel);

        mockMvc.perform(put("/reels/{reelId}/user/{userId}", reelId, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(reel)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.title").value("Test Reel"))
               .andExpect(jsonPath("$.video").value("test-video.mp4"));
    }
    @Test
    public void testCreateReel_MissingUser() throws Exception {
        Reels reel = new Reels("Test Reel", "test-video.mp4", null);
        Long userId = 1L;

        mockMvc.perform(post("/reels/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(reel)))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteReel() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);

        Reels reel=new Reels("reel", "video", user);
        Long reelId=1l;
        reel.setId(reelId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reelsRepository.findById(reelId)).thenReturn(Optional.of(reel));
        when(reelsRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
     
        mockMvc.perform(delete("/reels/{reelId}/user/{userId}", reelId, userId))
               .andExpect(status().isNoContent());
    }

    @Test
    public void testLikeReel() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);

        Reels reel=new Reels("reel", "video", user);
        Long reelId=1l;
        reel.setId(reelId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reelsRepository.findById(reelId)).thenReturn(Optional.of(reel));
        when(reelsRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(put("/reels/like/{reelId}/user/{userId}", reelId, userId))
               .andExpect(status().isAccepted())
               .andExpect(jsonPath("$.title").value("reel"))
               .andExpect(jsonPath("$.video").value("video"));
    }
}
