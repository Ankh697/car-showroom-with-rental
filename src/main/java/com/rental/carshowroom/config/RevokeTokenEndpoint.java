package com.rental.carshowroom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@FrameworkEndpoint
public class RevokeTokenEndpoint {
    private final TokenStore tokenStore;
    private final AuthorizationServerTokenServices authorizationServerTokenServices;

    @Autowired
    public RevokeTokenEndpoint(TokenStore tokenStore, @Qualifier("defaultAuthorizationServerTokenServices") AuthorizationServerTokenServices authorizationServerTokenServices) {
        this.tokenStore = tokenStore;
        this.authorizationServerTokenServices = authorizationServerTokenServices;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/oauth/token-revoke")
    @ResponseBody
    public void revokeToken(Principal principal) {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
        OAuth2AccessToken accessToken = authorizationServerTokenServices.getAccessToken(oAuth2Authentication);
        tokenStore.removeAccessToken(accessToken);
    }
}
