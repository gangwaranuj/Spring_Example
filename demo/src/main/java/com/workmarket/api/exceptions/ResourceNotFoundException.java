package com.workmarket.api.exceptions;

/**
 * Created by ianha on 4/1/15.
 */
public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String message) {
       super(message);
    }
}
