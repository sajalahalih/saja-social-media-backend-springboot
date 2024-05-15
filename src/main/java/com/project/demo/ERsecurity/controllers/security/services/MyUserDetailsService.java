package com.project.demo.ERsecurity.controllers.security.services;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface MyUserDetailsService extends UserDetailsService {


    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException ;
 
  
}
