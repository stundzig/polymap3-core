/* 
 * polymap.org
 * Copyright 2009-2013, Polymap GmbH. All rights reserved.
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
package org.polymap.core.project.ui.project;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import net.refractions.udig.ui.CRSChooser;
import net.refractions.udig.ui.Controller;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import org.polymap.core.operation.OperationSupport;
import org.polymap.core.project.IMap;
import org.polymap.core.project.Messages;
import org.polymap.core.project.ProjectPlugin;
import org.polymap.core.project.ProjectRepository;
import org.polymap.core.project.operations.NewMapOperation;
import org.polymap.core.project.ui.util.SimpleFormData;
import org.polymap.core.runtime.IMessages;
import org.polymap.core.workbench.PolymapWorkbench;

/**
 * Creates a new {@link IMap} by executing the {@link NewMapOperation}. 
 *
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 * @since 3.0
 */
public class NewMapWizard
        extends Wizard
        implements INewWizard {

    private static final Log log = LogFactory.getLog( NewMapWizard.class );

    public static final IMessages   i18n = Messages.forPrefix( "NewMapWizard" );
            
    private MapWizardPage           mapPage;
    
    
    public NewMapWizard() {
    }


    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        mapPage = new MapWizardPage();
        addPage( mapPage );
    }


    public boolean canFinish() {
        return mapPage.isPageComplete();
    }


    public boolean performFinish() {
        if (canFinish()) {
            try {
                //            String crsText = mapPage.crsText.getText();
                //            CoordinateReferenceSystem crs = CRS.decode( crsText );
                //            if (crs == null) {
                //                throw new RuntimeException( "Unable to locate EPSG authority for EPSG:4326" );
                //            }
                CoordinateReferenceSystem crs = mapPage.chooser.getCRS();
                if (crs == null) {
                    throw new RuntimeException( "No CRS set." );
                }

                String name = mapPage.nameText.getText();
                IMap parent = ProjectRepository.instance().getRootMap();

                NewMapOperation op = new NewMapOperation();
                op.init( parent, name, crs );
                OperationSupport.instance().execute( op, false, false );
                return true;
            }
            catch (Exception e) {
                PolymapWorkbench.handleError( ProjectPlugin.PLUGIN_ID, this, e.getLocalizedMessage(), e );
            }
        }
        return false;
    }

    
    /**
     * 
     */
    class MapWizardPage
            extends WizardPage
            implements IWizardPage {

        private Text            nameText; //, crsText;
        
        private CRSChooser      chooser;
        
        public MapWizardPage() {
            super( i18n.get( "pageName" ) );
            setMessage( i18n.get( "pageMsg" ) );
            setTitle( i18n.get( "pageTitle" ) );
            setDescription( i18n.get( "pageDescription" ) );
        }
        
        public void createControl( Composite parent ) {
            Composite composite = new Composite( parent, SWT.NONE );
            FormLayout layout = new FormLayout();
            layout.spacing = 2;
            layout.marginWidth = layout.marginHeight = 7;
            composite.setLayout( layout  );

            Label l1 = new Label( composite, SWT.NONE );
            l1.setLayoutData( SimpleFormData.filled().bottom( -1 ).create() );
            l1.setText( i18n.get( "fieldName" ) );
            
            nameText = new Text( composite, SWT.BORDER );
            nameText.setLayoutData( SimpleFormData.filled().top( l1 ).bottom( -1 ).create() );
            nameText.setText( "Map" );
            
//            new Label( composite, SWT.NONE ).setText( i18n.get( "fieldCRS" ) );
            
//            crsText = new Text( composite, SWT.BORDER );
//            crsText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
//            crsText.setText( "EPSG:4326" );
            
            // CRSChooser
            chooser = new CRSChooser( new Controller() {
                public void handleClose() {
                    performCancel();
                    dispose();
                }
                public void handleOk() {
                    getContainer().updateButtons();
//                    performFinish();
//                    dispose();
                }
                @Override
                public void handleSelect() {
                    getContainer().updateButtons();
                }
            });
            // XXX get a default value from preferences
            chooser.createControl( composite ).setLayoutData( SimpleFormData.filled().top( nameText, 20 ).create() );
            setControl( composite );
        }

//        public boolean canFlipToNextPage() {
//            return isPageComplete();
//        }

        public boolean isPageComplete() {
            log.info( "isPageComplete()..." );
            setErrorMessage( null );
            if (nameText.getCharCount() < 1) {
                setErrorMessage( "Geben Sie einen Projektnamen an." );
                return false;
            }
            if (chooser.getCRS() == null) {
                setErrorMessage( "Geben Sie ein Koordinatenreferenzsystem an." );
                return false;
            }
            return true;
        }
        
    }
    
}
