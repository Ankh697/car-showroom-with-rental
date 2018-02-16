package com.rental.carshowroom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
class AuthServiceConfig extends AuthorizationServerConfigurerAdapter {
    @Value("${security.oauth2.client.client-id}")
    private String oAuth2ClientId;
    @Value("${security.oauth2.client.client-secret}")
    private String oAuth2ClientSecret;
    @Value("${security.oauth2.client.authorized-grant-types}")
    private String oAuth2ClientGrantType;
    @Value("${security.oauth2.client.scope}")
    private String oAuth2ClientScope;

    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceConfig(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, @Qualifier("dataSource") DataSource dataSource) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.dataSource = dataSource;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource)
                .withClient(oAuth2ClientId)
                .secret(oAuth2ClientSecret)
                .authorizedGrantTypes(oAuth2ClientGrantType)
                .scopes(oAuth2ClientScope);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore()).userDetailsService(userDetailsService)
                .accessTokenConverter(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        return new JwtAccessTokenConverter();
    }
}