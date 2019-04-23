package com.autodesk.learnforge.service;

import com.autodesk.client.auth.OAuth2TwoLegged;
import com.autodesk.learnforge.config.ForgeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lenovo
 */
@Service
public class OAuthService
{
    @Autowired
    private ForgeProperties forgeProperties;

    private Map<String, OAuth2TwoLegged> cacheMap = new HashMap<>();

    public static List<String> INTERNAL_SCOPE = new ArrayList<String>()
    {
        {
            add("bucket:create");
            add("bucket:read");
            add("data:read");
            add("data:create");
            add("data:write");
        }
    };

    public static ArrayList<String> PUBLIC_SCOPE = new ArrayList<String>()
    {
        {
            add("viewables:read");
        }
    };

    public OAuth2TwoLegged getOAuthPublic() throws Exception
    {
        return buildOAuth2TwoLegged(PUBLIC_SCOPE, "public");
    }

    public OAuth2TwoLegged getOAuthInternal() throws Exception
    {
        return buildOAuth2TwoLegged(INTERNAL_SCOPE, "internal");
    }

    private OAuth2TwoLegged buildOAuth2TwoLegged(List<String> scopes, String cache) throws Exception
    {
        if (cacheMap.containsKey(cache))
        {
            return cacheMap.get(cache);
        } else
        {
            OAuth2TwoLegged forgeOAuth = OAuthClient(scopes);
            forgeOAuth.authenticate();
            cacheMap.put(cache, forgeOAuth);
            return forgeOAuth;
        }
    }

    public OAuth2TwoLegged OAuthClient(List<String> scopes) throws Exception
    {

        String clientId = forgeProperties.getClientId();
        String clientSecret = forgeProperties.getClientSecret();
        if (scopes == null)
        {
            scopes = INTERNAL_SCOPE;
        }
        return new OAuth2TwoLegged(clientId, clientSecret, scopes, true);
    }
}