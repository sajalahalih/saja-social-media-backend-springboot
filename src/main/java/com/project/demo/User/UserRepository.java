package com.project.demo.User;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String userName, String email);
    Optional<User> findByUserNameOrEmail(String username, String email);
    Optional<User>  findByUserName(String userName);
    Optional<User> findByEmail(String email);
    Boolean existsByUserName(String username);

  Boolean existsByEmail(String email);

  // public User findUserByJwt(String jwt);
  



 }
