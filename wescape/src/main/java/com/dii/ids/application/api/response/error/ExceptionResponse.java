package com.dii.ids.application.api.response.error;


public class ExceptionResponse {
    private String message;
    private String _class;

    public String getMessage() {
        return message;
    }

    public ExceptionResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public String get_class() {
        return _class;
    }

    public ExceptionResponse set_class(String _class) {
        this._class = _class;
        return this;
    }
}
