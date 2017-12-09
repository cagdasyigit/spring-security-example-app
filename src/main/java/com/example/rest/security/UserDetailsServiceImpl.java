package com.example.rest.security;

import com.example.rest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.rest.dao.UserDao;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserDao userDao;

    private User userCache = null;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (this.userCache == null
            || (this.userCache != null
            && username != null
            && this.userCache.getUsername().equals(username) == false)) {
            this.userCache = this.userDao.getUser(username);
        }

        return userCache;
    }

}