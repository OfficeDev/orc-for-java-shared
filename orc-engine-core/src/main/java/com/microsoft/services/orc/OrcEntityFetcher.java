/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information.
 ******************************************************************************/
package com.microsoft.services.orc;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.services.orc.interfaces.HttpVerb;
import com.microsoft.services.orc.interfaces.OrcResponse;
import com.microsoft.services.orc.interfaces.OrcURL;
import com.microsoft.services.orc.interfaces.Request;

import static com.microsoft.services.orc.Helpers.addCustomParametersToRequest;
import static com.microsoft.services.orc.Helpers.transformToEntityListenableFuture;
import static com.microsoft.services.orc.Helpers.transformToStringListenableFuture;
import static com.microsoft.services.orc.Helpers.transformToVoidListenableFuture;

/**
 * The type OrcEntityFetcher.
 *
 * @param <TEntity>     the type parameter
 * @param <TOperations> the type parameter
 */
public abstract class OrcEntityFetcher<TEntity, TOperations extends OrcOperations>
        extends OrcFetcher<TEntity>
        implements Readable<TEntity> {
    private TOperations operations;
    private String select;
    private String expand;

    /**
     * Instantiates a new OrcEntityFetcher.
     *
     * @param urlComponent   the url component
     * @param parent         the parent
     * @param clazz          the clazz
     * @param operationClazz the operation clazz
     */
    public OrcEntityFetcher(String urlComponent, OrcExecutable parent, Class<TEntity> clazz, Class<TOperations> operationClazz) {
        super(urlComponent, parent, clazz);

        try {
            this.operations = operationClazz.getConstructor(String.class,
                    OrcExecutable.class).newInstance("", this);
        } catch (Throwable ignored) {
        }
    }

    @Override
    protected ListenableFuture<OrcResponse> oDataExecute(Request request) {

        OrcURL orcURL = request.getUrl();

        if (select != null) {
            orcURL.addQueryStringParameter("$select", select);
        }

        if (expand != null) {
            orcURL.addQueryStringParameter("$expand", expand);
        }

        orcURL.prependPathComponent(urlComponent);

        addCustomParametersToRequest(request, getParameters(), getHeaders());
        return parent.oDataExecute(request);
    }


    /**
     * Updates the given entity.
     *
     * @param updatedEntity the updated entity
     * @return the listenable future
     */
    public ListenableFuture<TEntity> update(TEntity updatedEntity) {
        ListenableFuture<String> future = updateRaw(getResolver().getJsonSerializer().serialize(updatedEntity));
        return transformToEntityListenableFuture(future, this.clazz, getResolver());
    }

    /**
     * Updates the given entity.
     *
     * @param payload the updated entity
     * @return the listenable future
     */
    public ListenableFuture<String> updateRaw(String payload) {
        byte[] payloadBytes = payload.getBytes(Constants.UTF8);

        Request request = getResolver().createRequest();
        request.setContent(payloadBytes);
        request.setVerb(HttpVerb.PATCH);

        ListenableFuture<OrcResponse> future = oDataExecute(request);

        return transformToStringListenableFuture(future);
    }

    /**
     * Deletes
     *
     * @return the listenable future
     */
    public ListenableFuture delete() {
        Request request = getResolver().createRequest();
        request.setVerb(HttpVerb.DELETE);

        ListenableFuture<OrcResponse> future = oDataExecute(request);
        return transformToVoidListenableFuture(future);
    }

    /**
     * Reads
     *
     * @return the listenable future
     */
    public ListenableFuture<TEntity> read() {
        return transformToEntityListenableFuture(readRaw(), this.clazz, getResolver());
    }

    /**
     * Reads raw
     *
     * @return the listenable future
     */
    public ListenableFuture<String> readRaw() {
        return super.readRaw();
    }


    /**
     * Select OrcCollectionFetcher.
     *
     * @param select the select
     * @return the o data collection fetcher
     */
    public OrcEntityFetcher<TEntity, TOperations> select(String select) {
        this.select = select;
        return this;
    }

    /**
     * Expand OrcCollectionFetcher.
     *
     * @param expand the expand
     * @return the o data collection fetcher
     */
    public OrcEntityFetcher<TEntity, TOperations> expand(String expand) {
        this.expand = expand;
        return this;
    }

    /**
     * Gets operations.
     *
     * @return the operations
     */
    public TOperations getOperations() {
        return this.operations;
    }
}