package com.project.demo.ERsecurity.controllers.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.demo.User.User;
import com.project.demo.User.UserRepository;


@Service
public class UserDetailsServiceImpl implements MyUserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUserName(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    return UserDetailsImpl.build(user);
  }

  @Override
  public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
    User user = userRepository.findByUserName(email)
    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

    throw new UnsupportedOperationException("Unimplemented method 'loadUserByEmail'");
  }

}
