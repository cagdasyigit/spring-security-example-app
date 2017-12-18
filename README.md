# spring-security-example-app
A custom security implementation of  spring boot framework

#### Which includes:

 - Token control; generated based on user information, can be checked on per request on the fly or from db, just with a single configuration
 - Authentication of a basic UserDetails implementation object (class User implements UserDetails) by a ready-to-go "/user/login" and "/user/logout" rest service!.
 - User role permission check for each rest service path (also methods can be checked just calling some annotations on the top of the methods, see security configuration for details below)
 - Configurable cors header (application.yaml)

#### For The Future:
 - Method security example will be added
 - A ui form will be demonstrated
 - User password will be encrypted
 - Register service will be implemented
 

### **Get Started**

 1. Import application as maven project in your ide.
 2. Build project by command line or ide. From command line type "mvn install" in project directory. From ide: Right click on the project, choose Run As -> Maven build.
 3. Open the **application.yaml** file and write your own proper database information. Optionally change token configuration how it better suits your project.
 4. Click run on your ide while "App.java" class is opened, spring boot will install database tables and will start a tomcat instance.
 5. Rest services should be available at "http://localhost:8080" by default.
 6. See debugging section below for debugging.


### **Documentation**

#### **User object**
User object has implements the spring boot security's "UserDetails" object, therefore there are some implemented methods in this object. These methods are: getAuthorities, isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired, isEnabled. Feel free to change or implement functionalities of these methods on your own copy.

Currently; getAuthorities method has returns roles of user for checking roles on per request by filter.
Other methods returns just true or false. Change those methods if you have to implement on your project.

#### **Security Configuration**
Security configuration handled by "SecurityConfig" class. @EnableWebSecurity annotation enabling control over all web requests. In a rest api for example, configures service paths. @EnableGlobalMethodSecurity annotation enables control method security by @Secured annotation (this is optional). For example in a user rest service:

    @RestController
    @RequestMapping("/user")
    public class UserService {

        @Autowired
        UserDao userDao;

        .
        .

        @Secured("ADMIN")
        @RequestMapping(method = RequestMethod.POST, 
            produces = MediaType.APPLICATION_JSON_VALUE, 
            consumes = MediaType.APPLICATION_JSON_VALUE)
        public String addUser(@RequestBody User user) {
            userDao.addUser(user);
        }
    }

Paths and request methods can be configured under "configure" method in "SecurityConfig" class. 
For example "/user/login" and "/user/logout" paths are permitted for all requests but "/consumer" and "/corporate" paths are permitted only by a proper role. See below code:

    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public class SecurityConfig extends WebSecurityConfigurerAdapter {@Override

		.
		.
		
	    protected void configure(HttpSecurity http) throws Exception {
          http
          .csrf().disable()
          .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
          .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers("/user/login", "/user/logout").permitAll()
                  .antMatchers("/corporate").hasRole("CORPORATE")
                  .antMatchers("/consumer").hasRole("CONSUMER")
            .anyRequest().authenticated();

            http.addFilterBefore(new AuthorizationFilter(), 
                UsernamePasswordAuthenticationFilter.class);
	    }
    }

Secured paths controlled by a filter which named "AuthorizationFilter". This class checks "AuthenticationToken" request header. If the token is valid for user, then the filter is authenticating request, and resuming filter chain, request will be authenticated and continue to process. See "doFilter" method in "AuthorizationFilter" class for further information.

"AuthorizationFilter" class will check the token on the fly or from db per request. This can be configured in **application.yaml** file, **checkDbPerRequest** flag should be true for checking token from db.

### **Debugging**
I recommend use **Postman** or something equivalent application for sending requests. After adding a user in your database first call "/login" service and get a generated token. For authenticated rest service paths, set request header "AuthenticationToken". Token header must be current genereated token in "user_token" table. Also you need to set "Content-Type" header.  For example:

    Content-Type: application/json
    AuthenticationToken: 0a998HhF0m43Fv4JAsdWeD (This is not a real generated token)

Choose your request type which path you want to test, choose raw type in body section and write down a request body. If your service consumes json then write something like:

    {
    	"bookName": "My book",
    	"author": "Some novelist"
    }
Your debug points in the ide should be triggerred.
