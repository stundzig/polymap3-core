/*
 * polymap.org
 * Copyright 2011, Falko Br�utigam, and other contributors as
 * indicated by the @authors tag. All rights reserved.
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
package org.polymap.core.data.operations.feature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.swt.widgets.Event;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionDelegate;

import org.polymap.core.data.DataPlugin;
import org.polymap.core.data.PipelineFeatureSource;
import org.polymap.core.operation.OperationSupport;
import org.polymap.core.project.ILayer;
import org.polymap.core.workbench.PolymapWorkbench;

/**
 *
 * @deprecated As of {@link CopyFeaturesOperation2}
 * @see CopyFeaturesOperation
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 */
public class CopyLayerFeaturesAction
        extends ActionDelegate
        implements IObjectActionDelegate {

    private static Log log = LogFactory.getLog( CopyLayerFeaturesAction.class );

    private ILayer          layer;


    public void runWithEvent( IAction action, Event event ) {
        try {
            PipelineFeatureSource source = PipelineFeatureSource.forLayer( layer, false );
            CopyFeaturesOperation op = new CopyFeaturesOperation( source, null );
            OperationSupport.instance().execute( op, true, true );
        }
        catch (Exception e) {
            PolymapWorkbench.handleError( DataPlugin.PLUGIN_ID, this, "", e );
        }
    }


    public void selectionChanged( IAction action, ISelection sel ) {
        layer = null;
        action.setEnabled( false );

        if (sel instanceof IStructuredSelection) {
            Object elm = ((IStructuredSelection)sel).getFirstElement();
            if (elm != null && elm instanceof ILayer) {
                layer = (ILayer)elm;
                action.setEnabled( true );

                // check ACL permission
                //action.setEnabled( ACLUtils.checkPermission( acl, AclPermission.WRITE, false ) );
            }
        }
    }


    public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
    }

}
