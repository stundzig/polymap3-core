package org.polymap.openlayers.rap.lib_provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator 
        extends Plugin {

    private static final Log log = LogFactory.getLog(Activator.class);

	// The plug-in ID
	public static final String PLUGIN_ID = "org.polymap.openlayers.rap.lib_provider";

	private static Activator       plugin;

	public boolean                 started = false;

	
	public Activator() {
	}


    public void start( final BundleContext context )
    throws Exception {
        super.start( context );

        log.info( "HttpService: " + HttpService.class );
        
        // start HttpServiceRegistry
        if (HttpService.class != null) {
            startService( context );
        }
        else {
            context.addBundleListener( new BundleListener() {
                public void bundleChanged( BundleEvent ev ) {
                    if (ev.getType() == BundleEvent.STOPPED
                            && !started && (HttpService.class != null)) {
                        startService( context );
                    }
                }
            });
        }

		plugin = this;
	}


    public void stop( BundleContext context )
    throws Exception {
        plugin = null;
        super.stop( context );
    }

    
    protected void startService( BundleContext context ) {
        HttpService httpService;
        ServiceReference[] httpReferences = null;
        try {
            httpReferences = context.getServiceReferences( HttpService.class.getName(), null );
        }
        catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }

        if (httpReferences != null) {
            String port = context.getProperty( "org.osgi.service.http.port" );
            String hostname = context.getProperty( "org.osgi.service.http.hostname" );

            log.info( "found http service on hostname:" + hostname + "/ port:" + port );

            httpService = (HttpService)context.getService( httpReferences[0] );

            try {
                httpService.registerResources( "/openlayers", "/openlayers", null );
                started = true;
            }
            catch (NamespaceException e) {
                e.printStackTrace();
            }
        }
        else {
            log.debug( "No http service yet available - waiting for next BundleEvent" );
        }
    }

    
	public static Activator getDefault() {
		return plugin;
	}
	
}
