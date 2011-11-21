// ========================================================================
// Copyright (c) 2009-2009 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
// The Eclipse Public License is available at
// http://www.eclipse.org/legal/epl-v10.html
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
// You may elect to redistribute this code under either of these licenses.
// ========================================================================

package com.intalio.service.impl;

import java.net.URI;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.io.ByteArrayBuffer;

import com.google.gson.Gson;
import com.intalio.service.OAuthService;

/* ------------------------------------------------------------ */
/**
 */
public class GoogleOAuthServiceImpl implements OAuthService
{
    // raw ip addresses will be refused by google
    private static final String DOMAIN = "tbecker2.dyndns.org";
    // private static final String OAUTH_CONSUMER_KEY = "tbecker2.dyndns.org";
    // private static final String OAUTH_SECRET = "WsEnec3gmG0aS5ooXcf9Kaff";
    private static final String CLIENT_ID = "821447254243.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "I02lXX8B7kiva-uimD-K0V6I";
    private static final String SCOPE = "https://www.googleapis.com/auth/buzz";
    private static final String REDIRECT_URL = "http://" + DOMAIN + "/oauth_callback";
    private static final String AUTH_TOKEN_ENDPOINT = "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri="
            + REDIRECT_URL + "&scope=" + SCOPE;
    private static final String ACCESS_TOKEN_ENDPOINT = "https://accounts.google.com/o/oauth2/token";
    private static final String VALIDATE_ACCESS_TOKEN_ENDPOINT = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=";

    private HttpClient httpClient = new HttpClient();

    public GoogleOAuthServiceImpl()
    {
        try
        {
            httpClient.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /* ------------------------------------------------------------ */
    /**
     * @see com.intalio.service.OAuthService#sendAccessTokenRequest()
     */
    public ContentExchange sendAccessTokenRequest()
    {
        ContentExchange contentExchange = new ContentExchange();
        try
        {
            contentExchange.setURI(new URI(AUTH_TOKEN_ENDPOINT));
            httpClient.send(contentExchange);
            contentExchange.waitForDone();
            System.out.println("AUTH_CODE RESPONSE:");
            System.out.println(contentExchange.getResponseStatus());
            System.out.println(contentExchange.getResponseContent());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return contentExchange;
    }

    public String getAccessToken(String auth_code)
    {
        ContentExchange contentExchange = new ContentExchange();
        contentExchange.setRequestContentType("application/x-www-form-urlencoded");
        contentExchange.setMethod(HttpMethods.POST);
        contentExchange.setRequestContent(createAccessTokenRequestParameters(auth_code));

        AccessTokenResponse accessTokenResponse = null;
        try
        {
            contentExchange.setURI(new URI(ACCESS_TOKEN_ENDPOINT));
            httpClient.send(contentExchange);
            contentExchange.waitForDone();
            String response = contentExchange.getResponseContent();
            Gson gson = new Gson();
            accessTokenResponse = gson.fromJson(response,AccessTokenResponse.class);
            System.out.println("ACCESS_TOKEN RESPONSE:");
            System.out.println(accessTokenResponse);
            System.out.println(contentExchange.getResponseStatus());
            System.out.println(contentExchange.getResponseContent());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return accessTokenResponse.getAccessToken();
    }

    private ByteArrayBuffer createAccessTokenRequestParameters(String auth_code)
    {
        StringBuilder postContent = new StringBuilder();
        postContent.append("client_id=" + CLIENT_ID);
        postContent.append("&client_secret=" + CLIENT_SECRET);
        postContent.append("&redirect_uri=" + REDIRECT_URL);
        postContent.append("&grant_type=authorization_code");
        postContent.append("&code=" + auth_code);
        ByteArrayBuffer requestContent = new ByteArrayBuffer(postContent.toString());
        return requestContent;
    }

    private class AccessTokenResponse
    {
        private String access_token;
        private String token_type;
        private String expires_in;
        private String refresh_token;

        public String getAccessToken()
        {
            return access_token;
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("AccessTokenResponse [access_token=");
            builder.append(access_token);
            builder.append(", token_type=");
            builder.append(token_type);
            builder.append(", expires_in=");
            builder.append(expires_in);
            builder.append(", refresh_token=");
            builder.append(refresh_token);
            builder.append("]");
            return builder.toString();
        }

    }

    public boolean validateAccessToken(String accessToken)
    {
        ContentExchange contentExchange = new ContentExchange();
        try
        {
            contentExchange.setURI(new URI(VALIDATE_ACCESS_TOKEN_ENDPOINT + accessToken));
            httpClient.send(contentExchange);
            contentExchange.waitForDone();
            System.out.println("VALIDATE TOKEN RESPONSE:");
            System.out.println(contentExchange.getResponseStatus());
            System.out.println(contentExchange.getResponseContent());
            if(contentExchange.getResponseStatus()==HttpStatus.OK_200)
                return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

}
