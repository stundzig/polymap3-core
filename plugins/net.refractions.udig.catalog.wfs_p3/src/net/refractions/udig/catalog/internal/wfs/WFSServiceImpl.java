/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.internal.wfs;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;
import net.refractions.udig.catalog.wfs.internal.Messages;
import net.refractions.udig.ui.ErrorManager;
import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.xml.wfs.WFSSchema;

/**
 * Handle for a WFS service.
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class WFSServiceImpl extends IService {

	private URL identifier = null;
	private Map<String, Serializable> params = null;
	protected Lock rLock = new UDIGDisplaySafeLock();

	public WFSServiceImpl(URL identifier, Map<String, Serializable> dsParams) {
		this.identifier = identifier;
		this.params = dsParams;
	}

	public String toString() {
        return new StringBuilder( 128 )
                .append( "WFS" /*getClass().getSimpleName()*/ )
                .append( " (" )
                .append( getIdentifier() )
                .append( ")" )
                .toString();
    }

	/*
	 * Required adaptions: <ul> <li>IServiceInfo.class <li>List.class
	 * <IGeoResource> </ul>
	 * 
	 * @see net.refractions.udig.catalog.IService#resolve(java.lang.Class,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor)
			throws IOException {
		if (adaptee == null)
			return null;
		if (adaptee.isAssignableFrom(WFSDataStore.class)) {
			return adaptee.cast(getDS(monitor));
		}
		return super.resolve(adaptee, monitor);
	}

	/*
	 * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
	 */
	public <T> boolean canResolve(Class<T> adaptee) {
		if (adaptee == null)
			return false;
		return adaptee.isAssignableFrom(WFSDataStore.class)
				|| super.canResolve(adaptee);
	}

	public void dispose(IProgressMonitor monitor) {
		if (members == null)
			return;

		int steps = (int) ((double) 99 / (double) members.size());
		for (IResolve resolve : members) {
			try {
				SubProgressMonitor subProgressMonitor = new SubProgressMonitor(
						monitor, steps);
				resolve.dispose(subProgressMonitor);
				subProgressMonitor.done();
			} catch (Throwable e) {
				ErrorManager
						.get()
						.displayException(
								e,
								"Error disposing members of service: " + getIdentifier(), CatalogPlugin.ID); //$NON-NLS-1$
			}
		}
	}

	/*
	 * @see net.refractions.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List<WFSGeoResourceImpl> resources(IProgressMonitor monitor)
			throws IOException {

		if (members == null) {
			rLock.lock();
			try {
				if (members == null) {
					getDS(monitor); // load ds
					members = new LinkedList<WFSGeoResourceImpl>();
					String[] typenames = ds.getTypeNames();
					if (typenames != null)
						for (int i = 0; i < typenames.length; i++) {
							try {
								members.add(new WFSGeoResourceImpl(this,
										typenames[i]));
							} catch (Exception e) {
								WfsPlugin.log("", e); //$NON-NLS-1$
							}
						}
				}
			} finally {
				rLock.unlock();
			}
		}
		return members;
	}

	private volatile List<WFSGeoResourceImpl> members = null;

	/*
	 * @see net.refractions.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
		getDS(monitor); // load ds
		if (info == null && ds != null) {
			rLock.lock();
			try {
				if (info == null) {
					info = new IServiceWFSInfo(ds);
					IResolveDelta delta = new ResolveDelta(this,
							IResolveDelta.Kind.CHANGED);
					
		            // XXX _p3: not a good idea of the programmer of this extension
		            // to directly cast to CatalogImpl
		            ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
		            if (localCatalog instanceof CatalogImpl) {
		                System.out.println( "No CatalogImpl -> no event fired." );
		                ((CatalogImpl)localCatalog).fire( new ResolveChangeEvent( this,
		                            IResolveChangeEvent.Type.POST_CHANGE, delta ) );
		            }
				}
			} finally {
				rLock.unlock();
			}
		}
		return info;
	}

	/*
	 * @see net.refractions.udig.catalog.IService#getConnectionParams()
	 */
	public Map<String, Serializable> getConnectionParams() {
		return params;
	}

	private Throwable msg = null;
	private volatile WFSDataStoreFactory dsf;
	private volatile WFSDataStore ds = null;
	private static final Lock dsLock = new UDIGDisplaySafeLock();

	WFSDataStore getDS(IProgressMonitor monitor) throws IOException {
		if (ds == null) {
			if (monitor == null)
				monitor = new NullProgressMonitor();
			monitor.beginTask(Messages.WFSServiceImpl_task_name, 3);
			dsLock.lock();
			monitor.worked(1);
			try {
				if (ds == null) {
					if (dsf == null) {
						dsf = new WFSDataStoreFactory();
					}
					monitor.worked(1);
					if (dsf.canProcess(params)) {
						monitor.worked(1);
						try {
							//HACK: explicitly ask for WFS 1.0
							URL url = (URL)params.get(WFSDataStoreFactory.URL.key);
							url = WFSDataStoreFactory.createGetCapabilitiesRequest(url);
							params = new HashMap<String, Serializable>(params);
                            params.put(WFSDataStoreFactory.URL.key, url);
                            // _p3: default 3s is to short
                            params.put(WFSDataStoreFactory.TIMEOUT.key, 10000);
							ds = dsf.createDataStore(params);
							monitor.worked(1);
						} catch (IOException e) {
							msg = e;
							throw e;
						}
					}
				}
			} finally {
				dsLock.unlock();
				monitor.done();
			}
			IResolveDelta delta = new ResolveDelta(this,
					IResolveDelta.Kind.CHANGED);
			
			// XXX _p3: not a good idea of the programmer of this extension
			// to directly cast to CatalogImpl
			ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
			if (localCatalog instanceof CatalogImpl) {
                System.out.println( "No CatalogImpl -> no event fired." );
			    ((CatalogImpl)localCatalog).fire( new ResolveChangeEvent( this,
							IResolveChangeEvent.Type.POST_CHANGE, delta ) );
			}
		}
		return ds;
	}

	/*
	 * @see net.refractions.udig.catalog.IResolve#getStatus()
	 */
	public Status getStatus() {
		return msg != null ? Status.BROKEN : ds == null ? Status.NOTCONNECTED
				: Status.CONNECTED;
	}

	/*
	 * @see net.refractions.udig.catalog.IResolve#getMessage()
	 */
	public Throwable getMessage() {
		return msg;
	}

	/*
	 * @see net.refractions.udig.catalog.IResolve#getIdentifier()
	 */
	public URL getIdentifier() {
		return identifier;
	}

	private class IServiceWFSInfo extends IServiceInfo {

		private WFSDataStore ds;

		IServiceWFSInfo(WFSDataStore resource) {
			super();
			this.ds = resource;
			icon = AbstractUIPlugin.imageDescriptorFromPlugin(WfsPlugin.ID,
            "icons/obj16/wfs_obj.16"); //$NON-NLS-1$
		}

		/*
		 * @see net.refractions.udig.catalog.IServiceInfo#getAbstract()
		 */
		public String getAbstract() {
			return ds.getInfo().getDescription();
		}

		/*
		 * @see net.refractions.udig.catalog.IServiceInfo#getKeywords()
		 */
		public Set<String> getKeywords() {
			return ds.getInfo().getKeywords();
		}

		/*
		 * @see net.refractions.udig.catalog.IServiceInfo#getSchema()
		 */
		public URI getSchema() {
			return WFSSchema.NAMESPACE;
		}

		public String getDescription() {
			return getIdentifier().toString();
		}

        public URI getSource() {
            try {
                return getIdentifier().toURI();
            } catch (URISyntaxException e) {
                // This would be bad 
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
        
		public String getTitle() {
			String title = ds.getInfo().getTitle();
			if (title == null) {
				title = getIdentifier() == null ? Messages.WFSServiceImpl_broken
						: getIdentifier().toString();
			}else{
				title += " (WFS " + ds.getInfo().getVersion() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			return title;
		}
	}
}