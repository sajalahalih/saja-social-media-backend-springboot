package com.project.demo;

import com.project.demo.Comment.Comment;
import com.project.demo.Comment.CommentAssembler;
import com.project.demo.Comment.CommentController;
import com.project.demo.Comment.CommentRepository;
import com.project.demo.Post.Post;
import com.project.demo.Post.PostRepositry;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RequestMapping("/test")
@SpringBootTest
@AutoConfigureMockMvc
public class TestCommentController {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepositry postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentAssembler commentAssembler;

    @InjectMocks
    @Autowired
    private CommentController commentController;

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {

         commentAssembler=new CommentAssembler();
         commentController=new CommentController(postRepository, userRepository,commentRepository,commentAssembler);

        UserAssembler userAssembler=new UserAssembler();
        UserController userController=new UserController(userRepository, userAssembler);

     
       
         mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        
        
    }

    @Test
    public void testGetComments() throws Exception {
        when(commentRepository.findAll()).thenReturn(Collections.singletonList(createTestComment()));

       
        mockMvc.perform(get("/posts/comments"))
                .andExpect(status().isOk());
                
    }

    @Test
    public void testGetCommentById() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "test.jpg", "test.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        Comment comment=new Comment("comment", user, post, new ArrayList<>());
        when(postRepository.save(any(Post.class))).thenReturn(post);

        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));

        when(postRepository.findByUser_Id(userId)).thenReturn(Collections.singletonList(post));

      

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        mockMvc.perform(get("/posts/{postId}/comments/{commentId}/user/{userId}", 1, 1, 1))
                .andExpect(status().isOk());
    }
    @Test
    public void testCreateComment() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "test.jpg", "test.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        Comment comment=new Comment("comment", user, post, new ArrayList<>());
    
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));
        when(postRepository.findByUser_Id(userId)).thenReturn(Collections.singletonList(post));         
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);


        mockMvc.perform(post("/posts/{postId}/comments/user/{userId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"content\":\"comment\","
                    + "\"liked\":[],"
                    + "\"user\":{"
                        + "\"id\":" + userId
                    + "},"
                    + "\"post\":{"
                        + "\"id\":" + postId
                    + "}"
                + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("comment"));
    }

    @Test
    public void testCreateComment_InvalidInput() throws Exception {
        mockMvc.perform(post("/posts/{postId}/comments/user/{userId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"\",\"liked\":[]}") // Empty content
        )
                .andExpect(status().isBadRequest());
    }

    @Test
public void testEditComment_UserMismatch() throws Exception {
    Long userId = 1L;
        Long postId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        
         when(userRepository.findById(2L)).thenReturn(Optional.of(new User("otherUser", "other@example.com", "password")));
         when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
    
      
        Post post = new Post("Test Caption", "test.jpg", "test.mp4", user);
        post.setId(postId);
    
        Comment comment = new Comment("comment", user, post, new ArrayList<>());
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
    
    mockMvc.perform(put("/posts/{postId}/comments/{commentId}/user/{userId}", comment.getId(), postId, 2L)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"content\":\"Updated Comment Content\",\"liked\":[],\"user\":{\"id\":2}}")
  )
    .andExpect(status().isNotFound());
}


    @Test
    public void testLikeComment() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "test.jpg", "test.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        Comment comment=new Comment("comment", user, post, new ArrayList<>());
    
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));
        when(postRepository.findByUser_Id(userId)).thenReturn(Collections.singletonList(post));         
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        mockMvc.perform(put("/posts/comments/like/{commentId}/user/{userId}", 1L, 1L))
                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").value("comment"));
    }

    @Test
    public void testEditComment() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "test.jpg", "test.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        Comment comment=new Comment("comment", user, post, new ArrayList<>());
    
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));
        when(postRepository.findByUser_Id(userId)).thenReturn(Collections.singletonList(post));         
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        mockMvc.perform(put("/posts/{postId}/comments/{commentId}/user/{userId}", 1L, 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                + "\"content\":\"comment\","
                + "\"liked\":[],"
                + "\"user\":{"
                    + "\"id\":" + userId
                + "},"
                + "\"post\":{"
                    + "\"id\":" + postId
                + "}"
            + "}"))
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("comment"));
    }

    @Test
    public void testDeleteComment() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "test.jpg", "test.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        Comment comment=new Comment("comment", user, post, new ArrayList<>());
    
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));
        when(postRepository.findByUser_Id(userId)).thenReturn(Collections.singletonList(post));         
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}/user/{userId}", 1L, 1L, 1L))
                .andExpect(status().isNoContent());
    }
    @Test
    public void testDeleteComment_UserMismatch() throws Exception {
     
        Long userId = 1L;
        Long postId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        
         when(userRepository.findById(2L)).thenReturn(Optional.of(new User("otherUser", "other@example.com", "password")));
         when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
    
      
        Post post = new Post("Test Caption", "test.jpg", "test.mp4", user);
        post.setId(postId);
    
        Comment comment = new Comment("comment", user, post, new ArrayList<>());
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
    
        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}/user/{userId}", 1L, 1L, 2L))
            
                .andExpect(status().isNotFound());
    }
    

    @Test
    public void testReplyToComment() throws Exception {
        Long userId = 1L;
        User user = new User("testUser", "test@example.com", "password");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
       
        Post post =new Post("Test Caption", "test.jpg", "test.mp4", user);
        Long postId = 1L;
        post.setId(postId);

        Comment comment=new Comment("comment", user, post, new ArrayList<>());
    
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));
        when(postRepository.findByUser_Id(userId)).thenReturn(Collections.singletonList(post));         
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment reply = new Comment("reply comment", user, post, new ArrayList<>());
    
        reply.setUser(user);
        reply.setId(2l);
        reply.setParentComment(comment);

        when(commentRepository.findById(2L)).thenReturn(Optional.of(reply));
         when(commentRepository.save(any(Comment.class))).thenReturn(reply);
 

        mockMvc.perform(post("/posts/{postId}/comments/{parentCommentId}/reply/user/{userId}", 1L, 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                + "\"content\":\"comment\","
                + "\"liked\":[],"
                + "\"user\":{"
                    + "\"id\":" + userId
                + "},"
                + "\"post\":{"
                    + "\"id\":" + postId
                + "}"
            + "}"))
            .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("reply comment"));
    }

    private Comment createTestComment() {
        return new Comment("Test Content", createTestUser(), createTestPost(), Collections.emptyList());
    }

    private User createTestUser() {
        User user = new User("testUser", "test@example.com", "password");
        user.setId(1L);
        return user;
    }

    private Post createTestPost() {
        Post post= new Post("Test Caption", "test.jpg", "test.mp4", createTestUser());
    
    post.setId(1l);

    return post;
    }
}
