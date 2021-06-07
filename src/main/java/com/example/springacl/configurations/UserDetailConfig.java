package com.example.springacl.configurations;

import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.security.core.userdetails.User;
        import org.springframework.security.core.userdetails.UserDetailsService;
        import org.springframework.security.crypto.password.NoOpPasswordEncoder;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserDetailConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var uds = new InMemoryUserDetailsManager();
        var u1 = User.withUsername("pawan").password("12345").authorities("read", "write").build();
        var u2 = User.withUsername("rajdeep").password("12345").authorities("read", "write").build();
        var u3 = User.withUsername("raj").password("12345").authorities("read", "write").build();

        uds.createUser(u1);
        uds.createUser(u2);
        uds.createUser(u3);

        return uds;
    }

}