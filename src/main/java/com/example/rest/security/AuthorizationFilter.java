package com.example.rest.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.example.rest.dao.UserDao;

public class AuthorizationFilter extends UsernamePasswordAuthenticationFilter {
	
	@Value("${app.token.tokenHeader}")
    private String tokenHeader;

	@Value("${app.filter.cors}")
    private String cors;
	
	@Autowired
	UserDao userDao;

	@Autowired
    TokenHandler tokenHandler;

	@Autowired
    UserDetailsService userDetailsService;
	    
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse res = (HttpServletResponse) servletResponse;
		
		// Set Headers & Allow All Origins
		res.addHeader("Access-Control-Allow-Headers",
                "Access-Control-Allow-Origin, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, " + tokenHeader);
        if (res.getHeader("Access-Control-Allow-Origin") == null)
            res.addHeader("Access-Control-Allow-Origin", cors);
        
        // Check Token From Request
		String token = req.getHeader(tokenHeader);

		if (token == null) {
			chain.doFilter(req, res);
			return;
		}
		
		// Read username from token
		String username = this.tokenHandler.getUsernameFromToken(token);
		
		// Check validity
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (this.tokenHandler.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
		
		chain.doFilter(req, res);
	}

}
