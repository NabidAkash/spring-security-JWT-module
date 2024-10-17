package com.koios.main.service;

import com.koios.main.model.Authority;
import com.koios.main.model.User;
import com.koios.main.repository.AuthorityRepository;
import com.koios.main.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;


    public UserService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username.toLowerCase());
    }

    public void saveUser(User user) {
        Authority authority = authorityRepository.findByAuthority("ROLE_USER");
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);
    }

    public void saveAdmin(User user) {
        Authority authority = authorityRepository.findByAuthority("ROLE_ADMIN");
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);
    }

}
