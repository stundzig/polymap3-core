/* 
 * polymap.org
 * Copyright 2010, Falko Br�utigam, and other contributors as indicated
 * by the @authors tag.
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
 *
 * $Id: $
 */
package org.polymap.rhei.form;

import org.eclipse.core.runtime.IProgressMonitor;

import org.polymap.rhei.field.IFormField;

/**
 * This interface provides a way to explicitly handle load/store of the page. So
 * custom controls can be loaded/stored. If the page registeres
 * {@link IFormField}s then these fields are automatically loaded/stored.
 * 
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 * @version ($Revision$)
 */
public interface IFormEditorPage2
        extends IFormEditorPage {

    /**
     * Check if this page has pending changes. If any page of the form editor
     * has changes then the submit button of the form is enabled.
     * <p/>
     * The method might get called <b>before</b>
     * {@link #createFormContent(IFormEditorPageSite)} was called. So the
     * implementation must not depend on UI elements.
     * 
     * @return True if the page has pending changes.
     */
    boolean isDirty();
    
    boolean isValid();
    
    void doLoad( IProgressMonitor monitor ) 
    throws Exception;


    /**
     * Submits changes of UI elements of this page to the underlying feature.
     * <p/>
     * This method may also be used to calculate fields depending on current
     * input. In order to signal changes to all form fields
     * {@link IFormEditorPageSite#reloadEditor()} inside an <b>async</b> display
     * action.
     * 
     * @param monitor
     * @throws Exception
     */
    void doSubmit( IProgressMonitor monitor )
    throws Exception;
    

    /**
     * Dispose any resource this page may have aquired in
     * {@link #createFormContent(IFormEditorPageSite)}. Form fields that were
     * created via
     * {@link IFormEditorPageSite#newFormField(org.eclipse.swt.widgets.Composite, org.opengis.feature.Property, org.polymap.rhei.field.IFormField, org.polymap.rhei.field.IFormFieldValidator)}
     * are automatically disposed and must not be disposed in this method.
     */
    void dispose();

}