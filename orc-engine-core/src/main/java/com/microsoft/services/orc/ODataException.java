package com.microsoft.services.orc;

import com.microsoft.services.orc.interfaces.OrcResponse;

public class ODataException extends Exception {

    private OrcResponse response;

    public ODataException(OrcResponse response, String message) {
        super(message);
        this.response = response;
    }

    public ODataException(OrcResponse response, Throwable inner) {
        super(inner);
        this.response = response;
    }

    public OrcResponse getODataResponse() {
        return this.response;
    }
}
