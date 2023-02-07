package com.energyms.energyms.service;

import com.energyms.energyms.model.User;
import com.energyms.energyms.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email:" + emailId));
        return new org.springframework.security.core.userdetails.User(user.getEmailId(),
                user.getPassword(), new ArrayList<>());
                
    }
   
}
