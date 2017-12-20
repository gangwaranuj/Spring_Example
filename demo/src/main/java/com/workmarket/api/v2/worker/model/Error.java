package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ianha on 4/1/15.
 */
public class Error {
    // API TODO - normalize into API v2 response?
    /**
     * Human readable error message string
     */
    @JsonProperty("message")
    String message = "";

    /**
     * Name of the resource that prompted the error. The name of the resource
     * is not the Java class name but the resource name as defined in the API
     * documentation.
     */
    @JsonProperty("resource")
    String resource = "";

    /**
     * The field of the resource that prompted the error.
     */
    @JsonProperty("field")
    String field = "";

    public Error() {}

    public Error(String message) {
        this.message = message;
    }

    public Error(String message, String field) {
        this.message = message;
        this.field = field;
    }

    public Error(@JsonProperty("message") String message,
                 @JsonProperty("field") String field,
                 @JsonProperty("resource") String resource) {
        this.message = message;
        this.field = field;
        this.resource = resource;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
