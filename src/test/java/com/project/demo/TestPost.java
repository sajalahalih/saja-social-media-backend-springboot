package com.project.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Collections;
import java.util.Optional;

import com.project.demo.Post.Post;
import com.project.demo.Post.PostAssembler;
import com.project.demo.Post.PostController;
import com.project.demo.Post.PostRepositry;
import com.project.demo.User.User;
import com.project.demo.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/test")
@SpringBootTest
@AutoConfigureMockMvc
public class TestPost {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PostRepositry repository;

    @Mock
    private UserRepository userRepository;

    @Autowired
    private PostAssembler assembler;

    @InjectMocks
    @Autowired
    private PostController postController;

    @BeforeEach
    public void setUp() {

        assembler=new PostAssembler();
        postController=new PostController(repository, assembler, userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }


    @Test
    public void testGetPosts() throws Exception {
        // Mocking repository behavior
        when(repository.findAll()).thenReturn(Collections.singletonList(createTestPost()));

        // Performing GET request
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk());
              
    }

    @Test
    public void testGetPostById() throws Exception {
        // Mocking repository behavior
        when(repository.findById(any())).thenReturn(java.util.Optional.of(createTestPost()));
    
        // Performing GET request
        MvcResult result = mockMvc.perform(get("/posts/{id}", 1L))
                .andExpect(status().isOk())
                .andReturn();
    
        String content = result.getResponse().getContentAsString();
        System.out.println(content); // Log the response content
    
        // Assert JSON path
        mockMvc.perform(get("/posts/{id}", 1L))
        
                .andExpect(jsonPath("$.caption").value("Test Caption"));
    }
    

    @Test
    public void testFindUsersPosts() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "https://www.example.com/video.mp4", "https://www.example.com/video.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        when(repository.save(any(Post.class))).thenReturn(post);

        when(repository.findById(postId)).thenReturn(java.util.Optional.of(post));

        when(repository.findByUser_Id(userId)).thenReturn(Collections.singletonList(post));

      
        mockMvc.perform(get("/posts/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());

    }

    @Test
    public void testCreatePost() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "https://www.example.com/video.mp4", "https://www.example.com/video.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        when(repository.save(any(Post.class))).thenReturn(post);

        when(repository.findById(postId)).thenReturn(java.util.Optional.of(post));

        when(repository.findByUser_Id(userId)).thenReturn(Collections.singletonList(post));

      
        mockMvc.perform(post("/posts/user/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"caption\":\"Test Caption\",\"image\":\"https://www.example.com/video.mp4\",\"video\":\"https://www.example.com/video.mp4\","
                + "\"user\": {\"id\": 1,\"userName\": \"testUser\",\"email\": \"test@example.com\",\"password\": \"password\"}}"
                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.caption").value("Test Caption"));
    }



    @Test
    public void testUpdatePost() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "https://www.example.com/video.mp4", "https://www.example.com/video.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        when(repository.save(any(Post.class))).thenReturn(post);

        when(repository.findById(postId)).thenReturn(java.util.Optional.of(post));

   
        mockMvc.perform(put("/posts/{id}/user/{userId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"caption\":\"Test Caption\",\"image\":\"https://www.example.com/video.mp4\",\"video\":\"https://www.example.com/video.mp4\","
                    + "\"user\": {\"id\": 1,\"userName\": \"testUser\",\"email\": \"test@example.com\",\"password\": \"password\"}}"
                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.caption").value("Test Caption")
                
                );
    }

    @Test
    public void testDeletePost() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "https://www.example.com/video.mp4", "https://www.example.com/video.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        when(repository.save(any(Post.class))).thenReturn(post);

        when(repository.findById(postId)).thenReturn(java.util.Optional.of(post));

        when(repository.findByUser_Id(userId)).thenReturn(Collections.singletonList(post));

      
      
        mockMvc.perform(delete("/posts/{postid}/user/{userid}", 1L, 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testSavedPostHandler() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "https://www.example.com/video.mp4", "https://www.example.com/video.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        when(repository.save(any(Post.class))).thenReturn(post);

        when(repository.findById(postId)).thenReturn(java.util.Optional.of(post));

        when(repository.findByUser_Id(userId)).thenReturn(Collections.singletonList(post));

      
        mockMvc.perform(put("/posts/save/{postid}/user/{userid}", 1L, 1L))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.caption").value("Test Caption"));
    }

    @Test
    public void testLikedPostHandler() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "https://www.example.com/video.mp4", "test.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        when(repository.save(any(Post.class))).thenReturn(post);

        when(repository.findById(postId)).thenReturn(java.util.Optional.of(post));

        when(repository.findByUser_Id(userId)).thenReturn(Collections.singletonList(post));

      
        mockMvc.perform(put("/posts/like/{postid}/user/{userid}", 1L, 1L))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.caption").value("Test Caption"));
    }

    @Test
    public void testGetFollowingPosts() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "test.jpg", "test.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        when(repository.save(any(Post.class))).thenReturn(post);

        when(repository.findById(postId)).thenReturn(java.util.Optional.of(post));

        when(repository.findByUser_Id(userId)).thenReturn(Collections.singletonList(post));

      
        mockMvc.perform(get("/posts/user/{userId}/following", 1L))
                .andExpect(status().isOk());
    }

    @Test
public void testGetFollowingPosts_NoFollowingUsers() throws Exception {
    Long userId = 1L;
    User user = new User("testUser", "test@example.com", "password");
    user.setId(userId);
    user.setFollowing(Collections.emptyList()); 
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    when(repository.findByUserIn(any())).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/posts/user/{userId}/following", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty()); 
}


    private Post createTestPost() {
        return new Post("Test Caption", "test.jpg", "test.mp4", createTestUser());
    }

    private User createTestUser() {
        User user = new User("testUser", "test@example.com", "password");
        user.setId(1L);
        return user;
    }


    @Test
    public void testCreatePost_InvalidMediaType() throws Exception {
        Long userId = 1L;
        User user = createTestUser();
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(post("/posts/user/{userId}", userId)
                .contentType(MediaType.TEXT_PLAIN) // Invalid content type
                .content("{\"caption\":\"Test Caption\",\"image\":\"test.jpg\",\"video\":\"test.mp4\","
                + "\"user\": {\"id\": 1,\"userName\": \"testUser\",\"email\": \"test@example.com\",\"password\": \"password\"}}"
                )
        )
        .andExpect(status().isUnsupportedMediaType());
    }

    @Test
public void testSharePost() throws Exception {
   
    Long userId = 1L;
    User user = new User("testUser", "test@example.com", "password");
    user.setId(userId);

    Long postId = 1L;
    Post originalPost = new Post("Test Caption", "test.jpg", "test.mp4", user);
    originalPost.setId(postId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(repository.findById(postId)).thenReturn(Optional.of(originalPost));
    when(repository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved post

    mockMvc.perform(post("/posts/share/{postId}/user/{userId}", postId, userId))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.caption").value("Test Caption")); // Expecting the response to contain the original post's caption
}

}
