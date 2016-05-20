package com.dii.ids.application.api.response.error;

import java.util.List;

public class ErrorBodyResponse {
    private int code;
    private String message;
    private List<ExceptionResponse> exception;

    public int getCode() {
        return code;
    }

    public ErrorBodyResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ErrorBodyResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<ExceptionResponse> getException() {
        return exception;
    }

    public ErrorBodyResponse setException(List<ExceptionResponse> exception) {
        this.exception = exception;
        return this;
    }
}
