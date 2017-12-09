package com.example.rest;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories("com.example.rest.repositories")
@ComponentScan(basePackages = { 
		"com.example.rest.dao",
		"com.example.rest.model",
		"com.example.rest.repositories",
		"com.example.rest.service",
		"com.example.rest.security"
	})
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    	Locale locale = Locale.getDefault();
		System.out.println("Default Locale: " + locale);
		Locale.setDefault(new Locale("en", "US"));
		System.out.println("Default Locale: " + Locale.getDefault());
        SpringApplication.run(App.class, args);
    }
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
