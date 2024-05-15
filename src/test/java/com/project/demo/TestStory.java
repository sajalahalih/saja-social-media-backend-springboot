package com.project.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Collections;
import java.util.Optional;

import com.project.demo.Story.Story;
import com.project.demo.Story.StoryAssembler;
import com.project.demo.Story.StoryController;
import com.project.demo.Story.StoryRepository;
import com.project.demo.User.User;
import com.project.demo.User.UserAssembler;
import com.project.demo.User.UserController;
import com.project.demo.User.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/test")
@SpringBootTest
@AutoConfigureMockMvc
public class TestStory {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StoryRepository storyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    StoryAssembler storyAssembler;

    @BeforeEach
    public void setUp() {
        StoryAssembler storyAssembler=new StoryAssembler();
        StoryController storyController=new StoryController(storyRepository, storyAssembler,userRepository);

        UserAssembler userAssembler=new UserAssembler();
        UserController userController=new UserController(userRepository, userAssembler);

        mockMvc = MockMvcBuilders.standaloneSetup(storyController).build();
    }

    @Test
    public void testGetStories() throws Exception {
        mockMvc.perform(get("/stories"))
            .andExpect(status().isOk());
    }

    @Test
    public void testGetStoryById() throws Exception {
        User user = new User("testUser", "test@example.com", "password");
        user.setId(1L);
        Story story = new Story("caption", "image", "video", user,  Collections.emptyList());
        story.setId(1L);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(storyRepository.findById(1L)).thenReturn(java.util.Optional.of(story));

        mockMvc.perform(get("/stories/1/user/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.caption").value("caption"));
    }

    @Test
public void testGetStoryById_StoryNotFound() throws Exception {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    when(storyRepository.findById(1L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/stories/1/user/1"))
        .andExpect(status().isNotFound());
}

@Test
public void testGetStoryById_UserNotFound() throws Exception {
    User user = new User("testUser", "test@example.com", "password");
    user.setId(1L);

    when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
    when(storyRepository.findById(1L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/stories/1/user/1"))
        .andExpect(status().isNotFound());
}

    @Test
    public void testFindUsersStories() throws Exception {
        User user = new User("testUser", "test@example.com", "password");
        user.setId(1L);
        Story story = new Story("caption", "image", "video", user,  Collections.emptyList());
        story.setId(1L);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(storyRepository.findByUserId(1L)).thenReturn(Collections.singletonList(story));
        mockMvc.perform(get("/stories/user/1"))

            .andExpect(status().isOk());
    }

    @Test
     public void testFindUsersStories_UserNotFound() throws Exception {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/stories/user/1"))
        .andExpect(status().isNotFound());
}



    @Test
    public void testCreateStory() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);

        Story story = new Story("Test caption", "Test image", "Test video", user,  Collections.emptyList());

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(storyRepository.save(any(Story.class))).thenReturn(story);

        mockMvc.perform(post("/stories/user/{userId}",userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{"
        + "\"caption\":\"Test caption\","
        + "\"image\":\"Test image\","
        + "\"video\":\"Test video\","
        + "\"liked\":[],"
        + "\"user\":{"
            + "\"id\":" + userId + ","
            + "\"username\":\"testUser\","
            + "\"email\":\"test@example.com\","
            + "\"password\":\"password\""
        + "}"
    + "}"))
            .andExpect(status().isCreated());
    }

    
    @Test
    public void testCreateStory1() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);

        Story story = new Story("Test caption", "Test image", "Test video", user,  Collections.emptyList());
        Long storyId = 1L;
        story.setId(storyId);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(storyRepository.save(any(Story.class))).thenReturn(story);
 when(storyAssembler.toModel(story)).thenReturn(EntityModel.of(story));

        
        mockMvc.perform(post("/stories/user/{userId}",userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"caption\":\"Test caption\","
                    + "\"image\":\"Test image\","
                    + "\"video\":\"Test video\","
                    + "\"liked\":[],"
                    + "\"user\":{"
                        + "\"id\":" + userId + ","
                        + "\"username\":\"testUser\","
                        + "\"email\":\"test@example.com\","
                        + "\"password\":\"password\""
                    + "}"
                + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.caption").value("Test caption"))
                .andExpect(jsonPath("$.image").value("Test image"))
                .andExpect(jsonPath("$.video").value("Test video"))
                .andExpect(jsonPath("$.createdAt").exists()) // Check that createdAt exists
                .andExpect(jsonPath("$.liked").isArray()); // Check that liked is an array
    }

    @Test
    public void testCreateStory_ValidationFailure() throws Exception {
        // Sending a request with empty values for caption, image, and video fields
        mockMvc.perform(post("/stories/user/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                        + "\"caption\":\"\","
                        + "\"image\":\"\","
                        + "\"video\":\"\","
                        + "\"liked\":[],"
                        + "\"user\":{"
                            + "\"id\":1,"
                            + "\"username\":\"testUser\","
                            + "\"email\":\"test@example.com\","
                            + "\"password\":\"password\""
                        + "}"
                    + "}"))
                .andExpect(status().isBadRequest());
    }

    
    @Test
    public void testDeleteStory() throws Exception {
        User user = new User("testUser", "test@example.com", "password");
        user.setId(1L);
        Story story = new Story("caption", "image", "video", user,  Collections.emptyList());
        story.setId(1L);

        when(storyRepository.findById(1L)).thenReturn(java.util.Optional.of(story));
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(delete("/stories/1/user/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    public void testLikedStory() throws Exception {
        User user = new User("testUser", "test@example.com", "password");
        user.setId(1L);
        Story story = new Story("caption", "image", "video", user,  Collections.emptyList());
        story.setId(1L);

        when(storyRepository.findById(1L)).thenReturn(java.util.Optional.of(story));
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(post("/stories/like/1/user/1"))
            .andExpect(status().isAccepted());
    }
}
