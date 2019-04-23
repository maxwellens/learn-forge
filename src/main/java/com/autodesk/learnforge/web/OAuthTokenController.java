package com.autodesk.learnforge.web;

import com.autodesk.client.auth.OAuth2TwoLegged;
import com.autodesk.learnforge.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * AccessToken
 * @author Lenovo
 */
@RestController
public class OAuthTokenController
{
    @Autowired
    private OAuthService oAuthService;

    @RequestMapping("/api/forge/oauth/token")
    public Object getOAuthToken() throws Exception
    {
        Map<String, Object> result = new HashMap<>();
        OAuth2TwoLegged forgeOAuth = oAuthService.getOAuthPublic();
        String token = forgeOAuth.getCredentials().getAccessToken();
        long expires_in = (forgeOAuth.getCredentials().getExpiresAt() - System.currentTimeMillis()) / 1000;
        result.put("access_token", token);
        result.put("expires_in", expires_in);
        return result;
    }
}