package com.dii.ids.application.api.response.error;


public class ErrorResponse {
    private ErrorBodyResponse error;

    public ErrorBodyResponse getError() {
        return error;
    }

    public ErrorResponse setError(ErrorBodyResponse error) {
        this.error = error;
        return this;
    }
}
