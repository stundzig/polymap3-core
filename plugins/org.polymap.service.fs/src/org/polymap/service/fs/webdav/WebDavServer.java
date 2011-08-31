/* 
 * polymap.org
 * Copyright 2011, Polymap GmbH. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.service.fs.webdav;

import java.util.Locale;
import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bradmcevoy.http.HttpManager;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.Response;
import com.bradmcevoy.http.SecurityManager;

import org.polymap.core.runtime.Polymap;
import org.polymap.core.runtime.SessionContext;

import org.polymap.service.fs.ContentManager;
import org.polymap.service.fs.FsPlugin;
import org.polymap.service.http.WmsService;

/**
 * WebDAV server/servlet based on Milton.
 *
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 */
public class WebDavServer
        extends WmsService {

    private static Log log = LogFactory.getLog( WebDavServer.class );
    
    private static final String                     NULL_USER_NAME = "null";
    
    private static final ThreadLocal<Request>       threadRequest  = new ThreadLocal<Request>();

    private static final ThreadLocal<Response>      threadResponse = new ThreadLocal<Response>();

    private static final ThreadLocal<ServletConfig> threadServletConfig  = new ThreadLocal<ServletConfig>();


    public static Request request() {
        return WebDavServer.threadRequest.get();
    }

    public static Response response() {
        return WebDavServer.threadResponse.get();
    }

    /**
     * Make the servlet config available to any code on this thread.
     */
    public static ServletConfig servletConfig() {
        return threadServletConfig.get();
    }

    
    // instance *******************************************
    
    private ServletConfig               config;

    private HttpManager                 httpManager;
    
    private ResourceFactory             resourceFactory;
    
    private SecurityManager             securityManager;


    public void init( ServletConfig _config )
    throws ServletException {
        super.init( _config );
        log.info( "WebDAV Server: " + _config.getServletContext().getContextPath() );
        try {
            this.config = _config;
            
//            Map<String,String> users = new HashMap();
//            users.put( "admin", "admin" );
//            users.put( "falko", "." );
            securityManager = new SecurityManagerAdapter( "POLYMAP3 WebDAV" );
            
            this.resourceFactory = new WebDavResourceFactory( securityManager, "webdav" );
            
            this.httpManager = new HttpManager( resourceFactory );
        }
        catch (Throwable ex) {
            log.error( "Exception while starting", ex );
            throw new RuntimeException( ex );
        }
    }
    
    
    public void destroy() {
        httpManager = null;
        //resourceFactory.dispose();
        resourceFactory = null;
        securityManager = null;
    }


    public void service( ServletRequest servletRequest, ServletResponse servletResponse ) 
    throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse resp = (HttpServletResponse)servletResponse;

        try {
            Request request = new com.bradmcevoy.http.ServletRequest( req );
            Response response = new com.bradmcevoy.http.ServletResponse( resp );
            threadRequest.set( request );
            threadResponse.set( response );

            // map session context
            HttpSession session = req.getSession( true );
            FsPlugin.getDefault().sessionContextProvider.mapContext( session.getId(), true );

            // init new session
            if (session.isNew()) {
                session.setMaxInactiveInterval( 30 );
                final SessionContext sessionContext = SessionContext.current();
                
                // login
//                Auth auth = request.getAuthorization();
//                String user = auth != null ? auth.getUser() : NULL_USER_NAME;
                Principal user = Polymap.instance().getUser();

                // ContentManager
                Locale locale = req.getLocale();
                sessionContext.setAttribute( "contentManager", 
                        ContentManager.forUser( user.getName(), locale ) ); 
                        
                session.setAttribute( "sessionListener", new HttpSessionBindingListener() {
                    public void valueBound( HttpSessionBindingEvent ev ) {
                    }
                    public void valueUnbound( HttpSessionBindingEvent ev ) {
                        FsPlugin.getDefault().sessionContextProvider.destroyContext(
                                sessionContext.getSessionKey() );
                    }
                });
                log.info( "New session: " + session.getId() + ", user: " + user );
            }
            
//            log.info( "    useragent: " + request.getUserAgentHeader() );
//            Auth auth = request.getAuthorization();
//            log.info( "    user: " + (auth != null ? auth.getUser() : "null") );
            
            httpManager.process( request, response );
        }
        finally {
            FsPlugin.getDefault().sessionContextProvider.unmapContext();

            threadRequest.set( null );
            threadResponse.set( null );
            
            servletResponse.getOutputStream().flush();
            servletResponse.flushBuffer();
        }
    }
    
}
