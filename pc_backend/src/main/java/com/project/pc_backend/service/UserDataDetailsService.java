package com.project.pc_backend.service;

import com.project.pc_backend.dto.UserDataDetails;
import com.project.pc_backend.model.User;
import com.project.pc_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserDataDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDataDetailsService.class);
    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);

        if(user == null){
            logger.warn("User not found for email={}", email);
            throw new UsernameNotFoundException("User Not Found !");
        }

        return new UserDataDetails(user);
    }
}
