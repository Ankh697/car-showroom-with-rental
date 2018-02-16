package com.rental.carshowroom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**").authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/user").permitAll()
                .antMatchers(HttpMethod.GET, "/api/car/rent").permitAll()
                .antMatchers(HttpMethod.GET, "/api/car/buy").permitAll()
                .antMatchers(HttpMethod.GET, "/api/car/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/user/registration").permitAll()
                .antMatchers(HttpMethod.GET, "/api/user/registration/confirm").permitAll()
                .anyRequest()
                .authenticated();
    }
}
