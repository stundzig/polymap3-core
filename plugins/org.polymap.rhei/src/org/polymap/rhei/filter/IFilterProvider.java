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
package org.polymap.rhei.filter;

import java.util.List;

import org.polymap.core.project.ILayer;

/**
 * 
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 * @version ($Revision$)
 */
public interface IFilterProvider {

    /**
     * Returns a list of newly created filters for the given layer.
     * 
     * @param layer
     * @return The result list if the given layer is supported by this provider,
     *         or null otherwise.
     */
    public List<IFilter> addFilters( ILayer layer )
    throws Exception;
    
}