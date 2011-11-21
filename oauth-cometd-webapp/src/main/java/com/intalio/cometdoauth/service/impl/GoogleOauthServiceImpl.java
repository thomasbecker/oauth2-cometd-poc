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

package com.intalio.cometdoauth.service.impl;

import java.net.URI;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpStatus;

import com.intalio.cometdoauth.service.OauthService;

/* ------------------------------------------------------------ */
/**
 */
public class GoogleOauthServiceImpl implements OauthService
{
    private static final String VALIDATE_ACCESS_TOKEN_ENDPOINT = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=";

    private HttpClient httpClient = new HttpClient();

    public GoogleOauthServiceImpl()
    {
        try
        {
            httpClient.start();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
            if (contentExchange.getResponseStatus() == HttpStatus.OK_200)
                return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
