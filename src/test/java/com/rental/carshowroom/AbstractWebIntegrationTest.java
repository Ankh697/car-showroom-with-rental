package com.rental.carshowroom;

import com.rental.carshowroom.model.User;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan
@EnableWebMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class AbstractWebIntegrationTest {
    @Value("${security.oauth2.client.client-id}")
    protected String oAuth2ClientId;

    @Autowired
    private WebApplicationContext context;
    @Qualifier("defaultAuthorizationServerTokenServices")
    @Autowired
    private AuthorizationServerTokenServices tokenService;

    protected MockMvc mockMvc;

    protected void initMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    public MockHttpServletRequestBuilder authenticatedToken(MockHttpServletRequestBuilder builder, User principal) {
        OAuth2AccessToken token = tokenService.createAccessToken(getOauthTestAuthentication(principal));
        builder.header("Authorization", "Bearer " + token.getValue());
        return builder;
    }

    public OAuth2Authentication getOauthTestAuthentication(User principal) {
        return new OAuth2Authentication(getOauth2Request(principal), getAuthentication(principal));
    }

    private OAuth2Request getOauth2Request(User principal) {
        return new OAuth2Request(Collections.emptyMap(),
                oAuth2ClientId,
                principal.getAuthorities(),
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                "http://example.com",
                Collections.emptySet(),
                Collections.emptyMap());
    }

    private Authentication getAuthentication(User principal) {
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }
}
