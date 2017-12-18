package com.example.rest.service;

import com.example.rest.dao.UserDao;
import com.example.rest.model.User;
import com.example.rest.model.UserToken;
import com.example.rest.security.TokenHandler;
import com.example.rest.utils.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@EnableAutoConfiguration
@RequestMapping("/user")
public class UserService {

    @Autowired
    UserDao userDao;
	
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenHandler tokenHandler;
    
    @Autowired
    UserDetailsService userDetailsService;

	public UserService() {
		super();
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserToken login(@RequestBody User user, Device device) {
		UserToken userToken = new UserToken();

        // Check user credentials
        Authentication authentication = this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword()
            )
        );

        // Authenticate user in spring security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate token
        User userDetails = userDao.getUser(user.getUsername());
        String tokenKey = this.tokenHandler.generateToken(userDetails, device);
        
        // Build response
        userToken.setUser(userDetails);
        userToken.setTokenKey(tokenKey);
        userToken.setStatus(Constants.UsetTokenStatus.ACTIVE);
        
        // Save token
        userDao.saveUserToken(userToken);

		return userToken;
	}
	
	@RequestMapping(value = "logout", method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	private boolean logout(){
		User currentUser = (User) userDetailsService.loadUserByUsername(null);
		userDao.deactivateUserToken(currentUser);
		return true;
	}
	
	@RequestMapping(value="testAuth", method=RequestMethod.GET)
	public String test(){
		return "It works!";
	}

}
