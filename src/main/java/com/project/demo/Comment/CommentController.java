package com.project.demo.Comment;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.project.demo.ERsecurity.controllers.security.jwt.JwtUtils;
import com.project.demo.Post.Post;
import com.project.demo.Post.PostNotFoundException;
import com.project.demo.Post.PostRepositry;
import com.project.demo.User.User;
import com.project.demo.User.UserNotFoundException;
import com.project.demo.User.UserRepository;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;



@RestController
public class CommentController {
    
          @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PostRepositry postRepositry;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentAssembler assembler;

    

    public CommentController(PostRepositry postRepositry, UserRepository userRepository,
            CommentRepository commentRepository, CommentAssembler assembler) {
        this.postRepositry = postRepositry;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.assembler = assembler;
    }

    @GetMapping("/posts/comments")
    public CollectionModel<EntityModel<Comment>> getComments() {
        List<EntityModel<Comment>> comments= commentRepository.findAll().stream()
        .map(assembler::toModel)
        .collect(Collectors.toList());

        return CollectionModel.of(comments,linkTo(methodOn(CommentController.class).getComments()).withSelfRel());

    }
 
    @GetMapping("/posts/{postId}/comments/{commentId}/user/{userId}")
    public EntityModel<Comment> getCommentById(@PathVariable Long commentId,@PathVariable Long postId,@PathVariable Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Post post=postRepositry.findById(postId).orElseThrow(()->new PostNotFoundException(postId));
        Comment commentt=commentRepository.findById(commentId)
        .filter(comment->comment.getUser().equals(user))
        .filter(comment->comment.getPost().equals(post))
        
        .orElseThrow(()-> new CommentNotFoundException(commentId,userId));
   

        return assembler.toModel(commentt);
    }
    

    @PostMapping("/posts/{postId}/comments/user")
    public ResponseEntity<EntityModel<Comment>> createComment(@Valid @RequestBody Comment comment,@PathVariable("postId") Long postId,@RequestHeader("Authorization") String jwt){
        User userr; 


        jwt = jwt.substring(7);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
          String username = jwtUtils.getUserNameFromJwtToken(jwt);
           userr=userRepository.findByUserName(username).orElseThrow();
           long userId=userr.getId();

        
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
      
        Post post=postRepositry.findById(postId).orElseThrow(()-> new PostNotFoundException(postId));
        Comment createdComment=new Comment();
        createdComment.setContent(comment.getContent());
        createdComment.setCreatedAt(LocalDateTime.now());
        createdComment.setLiked(comment.getLiked());
        createdComment.setPost(post);
        createdComment.setUser(user);
       
        createdComment =commentRepository.save(createdComment);
        EntityModel<Comment> entityModel = assembler.toModel(createdComment);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);}
        return null;
  
    }
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11      1EDITE SECURITY EDIT LONG USERID
    @PutMapping("/posts/comments/like/{commentId}/user")
    public EntityModel<Comment> likeComment(@PathVariable Long commentId,@RequestHeader("Authorization") String jwt){
        User userr; 


        jwt = jwt.substring(7);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
          String username = jwtUtils.getUserNameFromJwtToken(jwt);
           userr=userRepository.findByUserName(username).orElseThrow();
           long userId=userr.getId();

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
      
        Comment comment=commentRepository.findById(commentId).orElseThrow(()->new CommentNotFoundException(commentId, userId));
        if(comment.getLiked().contains(user)){
            comment.getLiked().remove(user);
        }else{
            comment.getLiked().add(user);
        }
        commentRepository.save(comment);
       

        return assembler.toModel(comment);}
        return null;

    }

    @PutMapping("/posts/{postId}/comments/{commentId}/user")
    public ResponseEntity<EntityModel<Comment>> editComment(@PathVariable Long commentId,@PathVariable Long postId, @RequestBody Comment newComment ,@RequestHeader("Authorization") String jwt){
        User userr; 


        jwt = jwt.substring(7);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
          String username = jwtUtils.getUserNameFromJwtToken(jwt);
           userr=userRepository.findByUserName(username).orElseThrow();
           long userId=userr.getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Post post=postRepositry.findById(postId).orElseThrow(()->new PostNotFoundException(postId));
       
        Comment Updatedcomment=commentRepository.findById(commentId)
        .filter(comment->comment.getUser().equals(user))
        .filter(comment->comment.getPost().equals(post))
        .map(comment->{
            comment.setContent(newComment.getContent());
            comment.setCreatedAt(LocalDateTime.now());
            comment.setLiked(newComment.getLiked());
          
            return commentRepository.save(comment);
        })
        .orElseGet(()->{
            newComment.setId(commentId);
            return commentRepository.save(newComment);
        });
        EntityModel<Comment> entityModel=assembler.toModel(Updatedcomment);
        return ResponseEntity.ok(entityModel);}
        return null;

    }


    @DeleteMapping("/posts/{postId}/comments/{commentId}/user")
       public ResponseEntity<?> deleteComment(@PathVariable Long commentId,@PathVariable Long postId,@RequestHeader("Authorization") String jwt){
        User userr; 


        jwt = jwt.substring(7);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
          String username = jwtUtils.getUserNameFromJwtToken(jwt);
           userr=userRepository.findByUserName(username).orElseThrow();
           long userId=userr.getId();

        Comment comment=commentRepository.findById(commentId).orElseThrow(()-> new CommentNotFoundException(commentId, userId));
        User user=userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));

        if(comment.getUser().getId()!=user.getId())
        return  ResponseEntity.notFound().build();

       commentRepository.delete(comment);

        return ResponseEntity.noContent().build();}
        return null;
    }

    @PostMapping("/posts/{postId}/comments/{parentCommentId}/reply/user")
    public ResponseEntity<EntityModel<Comment>> replyToComment(@Valid @RequestBody Comment reply,
           
            @PathVariable("postId") Long postId,
            @PathVariable("parentCommentId") Long parentCommentId,
            @RequestHeader("Authorization") String jwt){
                User userr; 
        
        
                jwt = jwt.substring(7);
                if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                  String username = jwtUtils.getUserNameFromJwtToken(jwt);
                   userr=userRepository.findByUserName(username).orElseThrow();
                   long userId=userr.getId();

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Post post = postRepositry.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new CommentNotFoundException(parentCommentId, userId));

        Comment replyComment = new Comment();
        replyComment.setContent(reply.getContent());
        replyComment.setCreatedAt(LocalDateTime.now());
        replyComment.setLiked(reply.getLiked());
        replyComment.setPost(post);
        replyComment.setUser(user);
        replyComment.setParentComment(parentComment);  
        parentComment.getReplies().add(replyComment);

        replyComment = commentRepository.save(replyComment);
        return ResponseEntity.created(assembler.toModel(replyComment).getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(assembler.toModel(replyComment));}
                return null;
    }
}
