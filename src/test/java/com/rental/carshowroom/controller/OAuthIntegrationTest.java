package com.rental.carshowroom.controller;

import com.rental.carshowroom.AbstractWebIntegrationTest;
import com.rental.carshowroom.model.Role;
import com.rental.carshowroom.model.User;
import com.rental.carshowroom.model.enums.UserStatus;
import com.rental.carshowroom.repository.UserRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataJpaTest
@AutoConfigureMockMvc
public class OAuthIntegrationTest extends AbstractWebIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Value("${security.oauth2.client.client-secret}")
    private String oAuth2Secret;

    private User user;
    private String userLockedErrorMessage = "User account is locked";
    private String userDisabledErrorMessage = "User is disabled";

    @Before
    public void setup() {
        initMockMvc();
        user = User.builder()
                .username(RandomStringUtils.randomAlphabetic(5))
                .nameAndSurname(RandomStringUtils.randomAlphabetic(5))
                .pesel(RandomStringUtils.randomNumeric(11))
                .roles(Role.userRoles())
                .email("xxx@example.com")
                .status(UserStatus.ACTIVE)
                .password(RandomStringUtils.randomAlphanumeric(10))
                .build();
    }

    @Test
    public void authenticate_Positive_Test() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", user.getUsername());
        params.add("password", user.getPassword());
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params)
                .with(httpBasic(oAuth2ClientId, oAuth2Secret))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token_type").value("bearer"))
                .andReturn();
    }

    @Test
    public void authenticate_Disabled_Test() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", user.getUsername());
        params.add("password", user.getPassword());
        user.setPassword(encoder.encode(user.getPassword()));
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params)
                .with(httpBasic(oAuth2ClientId, oAuth2Secret))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_grant"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value(userDisabledErrorMessage))
                .andReturn();
    }

    @Test
    public void authenticate_Banned_Test() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", user.getUsername());
        params.add("password", user.getPassword());
        user.setPassword(encoder.encode(user.getPassword()));
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);
        mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params)
                .with(httpBasic(oAuth2ClientId, oAuth2Secret))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_grant"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value(userLockedErrorMessage))
                .andReturn();
    }

    @Test
    public void authenticate_BannedTemporary_Test() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", user.getUsername());
        params.add("password", user.getPassword());
        user.setPassword(encoder.encode(user.getPassword()));
        user.setStatus(UserStatus.BANNED_TEMPORARY);
        userRepository.save(user);
        mockMvc.perform(post("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params)
                .with(httpBasic(oAuth2ClientId, oAuth2Secret))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_grant"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value(userLockedErrorMessage))
                .andReturn();
    }
}
