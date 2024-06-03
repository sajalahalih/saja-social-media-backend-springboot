package com.project.demo.ERsecurity.controllers.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;


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


  private static final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

  @PostMapping("/google/signin")
  public ResponseEntity<?> googleSignIn(@RequestBody Map<String, String> payload) {
      String token = payload.get("token");

      try {
          GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory)
                  .setAudience(Collections.singletonList("YOUR_GOOGLE_CLIENT_ID"))
                  .build();

          GoogleIdToken idToken = verifier.verify(token);
          if (idToken != null) {
              GoogleIdToken.Payload googlePayload = idToken.getPayload();
              String email = googlePayload.getEmail();

              Optional<User> userOptional = userRepository.findByEmail(email);

              if (userOptional.isPresent()) {
                  User user = userOptional.get();
                  Authentication authentication = authenticationManager.authenticate(
                          new UsernamePasswordAuthenticationToken(user.getUserName(), "DEFAULT_PASSWORD"));

                  SecurityContextHolder.getContext().setAuthentication(authentication);
                  String jwt = jwtUtils.generateJwtToken(authentication);

                  UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                  return ResponseEntity.ok(new JwtResponse(jwt,
                          userDetails.getId(),
                          userDetails.getUsername(),
                          userDetails.getEmail(),
                          userDetails.getFirstName(),
                          userDetails.getLastName(),
                          userDetails.getAuthorities().stream()
                                  .map(item -> item.getAuthority())
                                  .collect(Collectors.toList())));
              } else {
                  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
              }
          } else {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
          }
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error validating token");
      }
  }

  // @PostMapping("/login/oauth/code/google")


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
