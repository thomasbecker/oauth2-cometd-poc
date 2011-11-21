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

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.eclipse.jetty.client.ContentExchange;
import org.junit.Before;
import org.junit.Test;

import com.intalio.service.OAuthService;


/* ------------------------------------------------------------ */
/**
 */
public class GoogleOAuthServiceTest
{

    OAuthService authService = new GoogleOAuthServiceImpl();

    /* ------------------------------------------------------------ */
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
    }

    /**
     * Test method for {@link com.intalio.service.impl.GoogleOAuthServiceImpl#sendAccessTokenRequest()}.
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testSendAccessTokenRequest() throws UnsupportedEncodingException
    {
        ContentExchange contentExchange = authService.sendAccessTokenRequest();
        assertEquals(302,contentExchange.getResponseStatus());
    }

}
