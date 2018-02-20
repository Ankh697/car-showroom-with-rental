package com.rental.carshowroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataJpaTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:validationmessages.properties")
public class UserControllerIntegrationTest extends AbstractWebIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper mapper;

    @Value("${msg.validation.user.notfound}")
    private String userNotFound;
    private String notBlankMessage = "may not be empty";
    private String notNullMessage = "may not be null";
    @Value("${msg.validation.user.pesel.pattern}")
    private String wrongPeselPattern;
    @Value("${msg.validation.user.username.size}")
    private String usernameSize;
    @Value("${msg.validation.user.nameandsurname.size}")
    private String nameAndSurnameSize;
    @Value("${msg.validation.user.email.pattern}")
    private String emailPattern;
    @Value("${msg.validation.user.email.size}")
    private String emailSize;
    @Value("${msg.validation.json.syntax.wrong}")
    private String wrongJsonSyntaxError;

    private User user;
    private User admin;
    private User toUpdate;
    private final String password = RandomStringUtils.randomAlphabetic(10);
    private final String username = RandomStringUtils.randomAlphabetic(5);
    private final String email = "xxx@example.com";
    private final String PESEL = RandomStringUtils.randomNumeric(11);

    @Before
    public void setup() {
        initMockMvc();
        user = User.builder()
                .username(username)
                .nameAndSurname(RandomStringUtils.randomAlphabetic(5))
                .pesel(PESEL)
                .roles(Role.userRoles())
                .email(email)
                .status(UserStatus.ACTIVE)
                .password(password)
                .build();
        admin = User.builder()
                .username("admin")
                .roles(Role.adminRoles())
                .status(UserStatus.ACTIVE)
                .build();
        toUpdate = User.builder()
                .username(user.getUsername())
                .nameAndSurname(RandomStringUtils.randomAlphabetic(20))
                .pesel(PESEL)
                .roles(Role.userRoles())
                .email(email)
                .status(UserStatus.INACTIVE)
                .password(password)
                .build();
    }

    @Test
    public void addUser_Positive_Test() throws Exception {
        mockMvc.perform(authenticatedToken(post("/api/user"), admin)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/user")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(UserStatus.ACTIVE.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
                .andReturn();
    }

    @Test
    public void addUser_ValidationFailureAllNulls_Test() throws Exception {
        mockMvc.perform(authenticatedToken(post("/api/user"), admin)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content("{ }"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(notNullMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(notBlankMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nameAndSurname").value(notBlankMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pesel").value(notNullMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value(notBlankMessage))
                .andReturn();
    }

    @Test
    public void addUser_ValidationFailureWrongPatterns_Test() throws Exception {
        user.setEmail(RandomStringUtils.randomNumeric(19));
        user.setPesel(RandomStringUtils.randomAlphabetic(5));
        mockMvc.perform(authenticatedToken(post("/api/user"), admin)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(emailPattern))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pesel").value(wrongPeselPattern))
                .andReturn();
    }

    @Test
    public void addUser_ValidationFailureWrongSize_Test() throws Exception {
        user.setEmail(RandomStringUtils.randomAlphabetic(50) + "@example.com");
        user.setUsername(RandomStringUtils.randomAlphabetic(25));
        user.setNameAndSurname(RandomStringUtils.randomAlphabetic(110));
        mockMvc.perform(authenticatedToken(post("/api/user"), admin)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(emailSize))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(usernameSize))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nameAndSurname").value(nameAndSurnameSize))
                .andReturn();
    }

    @Test
    public void addUser_ValidationFailureAllBlank_Test() throws Exception {
        user.setUsername("   ");
        user.setNameAndSurname("   ");
        user.setPassword("   ");
        mockMvc.perform(authenticatedToken(post("/api/user"), admin)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value(notBlankMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(notBlankMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nameAndSurname").value(notBlankMessage))
                .andReturn();
    }

    @Test
    public void addUser_WrongJsonSyntax_Test() throws Exception {
        mockMvc.perform(authenticatedToken(post("/api/user"), admin)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content("{ x }"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(wrongJsonSyntaxError))
                .andReturn();
    }

    @Test
    public void addUser_NoAuthorize_Test() throws Exception {
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void addUser_NotAdmin_Test() throws Exception {
        user.setRoles(Role.userRoles());
        mockMvc.perform(authenticatedToken(post("/api/user"), user)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void listAllUsers_Positive_test() throws Exception {
        saveTestUsers();
        MockHttpServletResponse response = mockMvc.perform(authenticatedToken(get("/api/user"), admin))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        User[] users = mapper.readValue(response.getContentAsString(), User[].class);
        assertEquals(3, users.length);
    }

    @Test
    public void listAllUsers_NotAdmin_test() throws Exception {
        mockMvc.perform(authenticatedToken(get("/api/user"), user))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void listAllUsers_NoAuthorize_test() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void updateUser_Admin_Test() throws Exception {
        checkPositiveUpdate(admin);
    }

    @Test
    public void updateUser_OwnProfile_Test() throws Exception {
        checkPositiveUpdate(user);
    }

    private void checkPositiveUpdate(User principal) throws Exception {
        userRepository.save(user);
        mockMvc.perform(authenticatedToken(put("/api/user/" + user.getId()), principal)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(toUpdate)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nameAndSurname").value(toUpdate.getNameAndSurname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(toUpdate.getStatus().name()))
                .andReturn();
    }

    @Test
    public void updateUser_NotOwnProfile_Test() throws Exception {
        userRepository.save(user);
        User testUser = User.builder()
                .username(RandomStringUtils.randomAlphabetic(8))
                .roles(Role.userRoles())
                .status(UserStatus.ACTIVE)
                .build();
        mockMvc.perform(authenticatedToken(put("/api/user/" + user.getId()), testUser)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(toUpdate)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void updateUser_NotFound_Test() throws Exception {
        mockMvc.perform(authenticatedToken(put("/api/user/1"), admin)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(toUpdate)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(userNotFound))
                .andReturn();
    }

    @Test
    public void updateUser_NotFoundAndUnauthorized_Test() throws Exception {
        mockMvc.perform(put("/api/user/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(toUpdate)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void updateUser_Unauthorized_Test() throws Exception {
        userRepository.save(user);
        mockMvc.perform(put("/api/user/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(toUpdate)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void updateUser_ChangeUsername_Test() throws Exception {
        userRepository.save(user);
        toUpdate.setUsername(RandomStringUtils.randomAlphabetic(6));
        mockMvc.perform(authenticatedToken(put("/api/user/" + user.getId()), admin)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .with(csrf())
                .content(mapper.writeValueAsBytes(toUpdate)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nameAndSurname").value(toUpdate.getNameAndSurname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(toUpdate.getStatus().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
                .andReturn();
    }

    @Test
    public void deleteUser_Admin_Test() throws Exception {
        userRepository.save(user);
        mockMvc.perform(authenticatedToken(delete("/api/user/" + user.getId()), admin)
                .with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void deleteUser_OwnProfile_Test() throws Exception {
        userRepository.save(user);
        mockMvc.perform(authenticatedToken(delete("/api/user/" + user.getId()), user)
                .with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void deleteUser_NotFound_Test() throws Exception {
        mockMvc.perform(authenticatedToken(delete("/api/user/1"), admin)
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(userNotFound))
                .andReturn();
    }

    @Test
    public void deleteUser_NotFoundAndUnauthorized_Test() throws Exception {
        mockMvc.perform(delete("/api/user/1")
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void deleteUser_Unauthorized_Test() throws Exception {
        userRepository.save(user);
        mockMvc.perform(delete("/api/user/" + user.getId())
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void deleteUser_NotOwnProfile_Test() throws Exception {
        userRepository.save(user);
        User testUser = User.builder()
                .username(RandomStringUtils.randomAlphabetic(8))
                .roles(Role.userRoles())
                .status(UserStatus.ACTIVE)
                .build();
        mockMvc.perform(authenticatedToken(delete("/api/user/" + user.getId()), testUser)
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
        assertNotNull(userRepository.findOne(user.getId()));
    }

    @Test
    public void getUser_Admin_Test() throws Exception {
        userRepository.save(user);
        mockMvc.perform(authenticatedToken(get("/api/user/" + user.getId()), admin)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nameAndSurname").value(user.getNameAndSurname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(user.getStatus().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
                .andReturn();
    }

    @Test
    public void getUser_OwnProfile_Test() throws Exception {
        userRepository.save(user);
        mockMvc.perform(authenticatedToken(get("/api/user/" + user.getId()), user)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nameAndSurname").value(user.getNameAndSurname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(user.getStatus().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
                .andReturn();
    }

    @Test
    public void getUser_NotFoundAdmin_Test() throws Exception {
        mockMvc.perform(authenticatedToken(get("/api/user/1"), admin)
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(userNotFound))
                .andReturn();
    }

    @Test
    public void getUser_NotFoundAndUnauthorized_Test() throws Exception {
        mockMvc.perform(get("/api/user/1")
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void getUser_NotOwnProfile_Test() throws Exception {
        userRepository.save(user);
        User testUser = User.builder()
                .username(RandomStringUtils.randomAlphabetic(8))
                .roles(Role.userRoles())
                .status(UserStatus.ACTIVE)
                .build();
        mockMvc.perform(authenticatedToken(get("/api/user/" + user.getId()), testUser)
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void getUser_Unauthorized_Test() throws Exception {
        userRepository.save(user);
        mockMvc.perform(get("/api/user/" + user.getId())
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    private void saveTestUsers() {
        for (int i = 0; i < 3; i++) {
            saveTestUser();
        }
    }

    private void saveTestUser() {
        userRepository.save(User.builder()
                .username(RandomStringUtils.randomAlphabetic(6))
                .nameAndSurname(RandomStringUtils.randomAlphabetic(5))
                .pesel(PESEL)
                .roles(Role.userRoles())
                .email(email)
                .status(UserStatus.ACTIVE)
                .password(password)
                .build());
    }
}
