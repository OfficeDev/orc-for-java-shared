/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information.
 ******************************************************************************/
package com.microsoft.services.orc;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.services.orc.interfaces.DependencyResolver;
import com.microsoft.services.orc.interfaces.OrcResponse;
import com.microsoft.services.orc.interfaces.Request;

import static com.microsoft.services.orc.Helpers.addCustomParametersToODataRequest;

/**
 * The type OrcOperations.
 */
public abstract class OrcOperations extends OrcExecutable {
    private String urlComponent;
    private OrcExecutable parent;

	 /**
     * Instantiates a new ODataOperation.
     *
     * @param urlComponent the url component
     * @param parent the parent
     */
    public OrcOperations(String urlComponent, OrcExecutable parent) {
        this.urlComponent = urlComponent;
        this.parent = parent;
    }

    @Override
    protected ListenableFuture<OrcResponse> oDataExecute(Request request) {
        request.getUrl().prependPathComponent(urlComponent);
        addCustomParametersToODataRequest(request, getParameters(), getHeaders());
        return parent.oDataExecute(request);
    }

    @Override
    protected  DependencyResolver getResolver() {
        return parent.getResolver();
    }
}
