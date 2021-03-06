/* 
 * polymap.org
 * Copyright (C) 2014, Falko Br�utigam. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.core.data.imex.kml;

import java.util.Collection;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.geotools.feature.DefaultFeatureCollections;
import org.geotools.feature.FeatureCollection;
import org.geotools.kml.KMLConfiguration;
import org.geotools.xml.Parser;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Throwables;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.eclipse.core.runtime.IProgressMonitor;

import org.polymap.core.data.DataPlugin;
import org.polymap.core.data.Messages;
import org.polymap.core.data.imex.FileImportWizard;
import org.polymap.core.runtime.IMessages;

/**
 * KML import.
 * 
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 */
public class KmlImportWizard 
        extends FileImportWizard
        implements INewWizard {

    private static Log log = LogFactory.getLog( KmlImportWizard.class );

    public static final String          ID = "org.polymap.core.data.KmlImportWizard";
    
    private static final IMessages      i18n = Messages.forPrefix( "KmlImportWizard" );
    

    public KmlImportWizard() {
        super();
    }

    
    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        super.init( workbench, selection );
        setWindowTitle( i18n.get( "windowTitle" ) );
        setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                DataPlugin.PLUGIN_ID, "icons/workset_wiz.png" ) );
        setNeedsProgressMonitor( true );
        
        page1 = new KmlUploadPage();
    }


    @Override
    public FeatureCollection parseKml( final InputStream in ) throws Exception {
        features = null;
        IRunnableWithProgress task = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                monitor.beginTask( i18n.get( "parseTaskTitle" ), 2 );
                try {
//                    log.info( "KML: " + kmlOut.toString() );
                    // parse KML
                    monitor.subTask( "Parsing KML..." );
                    KMLConfiguration config = new KMLConfiguration();

                    Parser parser = new Parser( config );
                    SimpleFeature f = (SimpleFeature)parser.parse( in );
                    Collection<Feature> placemarks = (Collection<Feature>)f.getAttribute( "Feature" );
                    //log.info( placemarks );

                    features = DefaultFeatureCollections.newCollection();
                    features.addAll( placemarks );
                    monitor.worked( 1 );

                    // adjust type
                    FeatureType schema = features.getSchema();
//                    SimpleFeatureTypeBuilder ftb = new SimpleFeatureTypeBuilder();
//                    ftb.init( (SimpleFeatureType)schema );
//                    ftb.remove( "visibility" );
//                    ftb.remove( "open" );
//                    ftb.remove( "Style" );
//                    ftb.remove( "LookAt" );
//                    ftb.setCRS( CRS.decode( "EPSG:4326" ) );
//                    ftb.remove( schema.getGeometryDescriptor().getLocalName() );
//                    ftb.add( schema.getGeometryDescriptor().getLocalName(), MultiLineString.class, CRS.decode( "EPSG:4326" ) );

//                    SimpleFeatureTypeBuilder ftb = new SimpleFeatureTypeBuilder();
//                    ftb.add( "name", String.class );
//                    ftb.setCRS( CRS.decode( "EPSG:4326" ) );
//                    ftb.add( schema.getGeometryDescriptor().getLocalName(), MultiLineString.class, CRS.decode( "EPSG:4326" ) );
//
//                    //features = new ReTypingFeatureCollection( features, ftb.buildFeatureType() );
//                    features = new RetypingFeatureCollection( features, ftb.buildFeatureType() ) {
//                        protected Feature retype( Feature feature ) {
//                            try {
//                                SimpleFeatureBuilder fb = new SimpleFeatureBuilder( (SimpleFeatureType)getSchema() );
//                                for (PropertyDescriptor prop : getSchema().getDescriptors()) {
//                                    fb.set( prop.getName(), feature.getProperty( prop.getName() ).getValue() );
//                                }
//                                return fb.buildFeature( feature.getIdentifier().getID() );
//                            }
//                            catch (Exception e) {
//                                throw new RuntimeException( e );
//                            }
//                        }
//                    };
                }
                catch (Exception e) {
                    new InvocationTargetException( e );
                }
                finally {
                    IOUtils.closeQuietly( in );
                }
            }            
        };

        try {
            getContainer().run( true, true, task );
        }
        catch (InvocationTargetException e) {
            Throwables.propagateIfPossible( e.getTargetException(), Exception.class );
        }
        catch (InterruptedException e) {
            //MessageDialog.openInformation( getShell(), "Info", i18n.get( "timeout" ) );
        }
        return features;
    }

}
