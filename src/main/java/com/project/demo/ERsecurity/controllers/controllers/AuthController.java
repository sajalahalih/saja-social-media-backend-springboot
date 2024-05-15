package com.project.demo.ERsecurity.controllers.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.demo.ERsecurity.controllers.payload.request.LoginRequest;
import com.project.demo.ERsecurity.controllers.payload.request.SignupRequest;
import com.project.demo.ERsecurity.controllers.payload.response.JwtResponse;
import com.project.demo.ERsecurity.controllers.payload.response.MessageResponse;
import com.project.demo.ERsecurity.controllers.security.jwt.JwtUtils;
import com.project.demo.ERsecurity.controllers.security.services.UserDetailsImpl;
import com.project.demo.User.User;
import com.project.demo.User.UserRepository;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;



  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    
      Optional<User> userOptional = userRepository.findByUserName(loginRequest.getUsername());
      
      if (!userOptional.isPresent()) {
          userOptional = userRepository.findByEmail(loginRequest.getUsername());
      }
      
      if (userOptional.isPresent()) {
          User user = userOptional.get();
          Authentication authentication = authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(user.getUserName(), loginRequest.getPassword()));
  
          SecurityContextHolder.getContext().setAuthentication(authentication);
          String jwt = jwtUtils.generateJwtToken(authentication);
  
          UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
          List<String> roles = userDetails.getAuthorities().stream()
                  .map(item -> item.getAuthority())
                  .collect(Collectors.toList());
  
          return ResponseEntity.ok(new JwtResponse(jwt,
                  userDetails.getId(),
                  userDetails.getUsername(),
                  userDetails.getEmail(),
                  userDetails.getFirstName(),
                  userDetails.getLastName(),
                  roles));
      } else {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
      }
  }
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUserName(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    User user = new User(signUpRequest.getUsername(), 
               signUpRequest.getEmail(),
               encoder.encode(signUpRequest.getPassword()),
               signUpRequest.getFirstName(),
               signUpRequest.getLastName());

    Set<String> strRoles = signUpRequest.getRole();
   

   
    userRepository.save(user);

    //UsernamePasswordAuthenticationToken

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}
