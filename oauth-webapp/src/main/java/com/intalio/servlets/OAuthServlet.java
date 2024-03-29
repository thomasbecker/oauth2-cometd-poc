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

package com.intalio.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.ContentExchange;

import com.intalio.service.OAuthService;
import com.intalio.service.impl.GoogleOAuthServiceImpl;

public class OAuthServlet extends HttpServlet
{
    private static final long serialVersionUID = 8397028157245153094L;

    private OAuthService oAuthService = new GoogleOAuthServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        ContentExchange accessTokenExchange = oAuthService.sendAccessTokenRequest();
        res.setStatus(accessTokenExchange.getResponseStatus());
        ServletOutputStream out = res.getOutputStream();
        out.write(accessTokenExchange.getResponseContentBytes());
        out.close();
    }

}
