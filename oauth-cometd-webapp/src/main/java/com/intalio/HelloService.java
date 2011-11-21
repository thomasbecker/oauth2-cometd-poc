/*
 * Copyright (c) 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intalio;

import java.util.HashMap;
import java.util.Map;

import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.server.Authorizer;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.AbstractService;
import org.cometd.server.DefaultSecurityPolicy;

import com.intalio.cometdoauth.service.OauthService;
import com.intalio.cometdoauth.service.impl.GoogleOauthServiceImpl;

public class HelloService extends AbstractService
{
    private OauthService oauthService = new GoogleOauthServiceImpl();

    public HelloService(BayeuxServer bayeux)
    {
        super(bayeux,"hello");
        addService("/service/hello","processHello");
        bayeux.setSecurityPolicy(new OAuthAuthenticator());
    }

    public void processHello(ServerSession remote, Message message)
    {
        Map<String, Object> input = message.getDataAsMap();
        String name = (String)input.get("name");

        Map<String, Object> output = new HashMap<String, Object>();
        output.put("greeting","Hello, " + name);
        remote.deliver(getServerSession(),"/hello",output,null);
    }

    private class OAuthAuthorizer implements Authorizer
    {
        public Result authorize(Operation arg0, ChannelId arg1, ServerSession arg2, ServerMessage arg3)
        {
            System.out.println("DENYING!!!");
            return Result.deny("NO WAY DUDE!");
        }
    }

    private class OAuthAuthenticator extends DefaultSecurityPolicy implements ServerSession.RemoveListener
    {
        @Override
        public boolean canHandshake(BayeuxServer server, ServerSession session, ServerMessage message)
        {
            if (session.isLocalSession())
                return true;

            Map<String, Object> ext = message.getExt();
            if (ext == null)
                return false;

            Map<String, Object> authentication = (Map<String, Object>)ext.get("authentication");
            if (authentication == null)
                return false;

            String token = (String)authentication.get("oauthAccessToken");

            // Authentication successful
            if (oauthService.validateAccessToken(token))
            {
                System.out.println("TOKEN VALID!!!");
                // Be notified when the session disappears
                session.addListener(this);
                return true;
            }

            // Link authentication data to the session

            return true;
        }

        public void removed(ServerSession session, boolean expired)
        {
            // Unlink authentication data from the remote client (8)
        }
    }
}
