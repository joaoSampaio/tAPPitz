package com.tappitz.tappitz.rest.service;

/**
 * Created by sampaio on 08-10-2015.
 */
public interface CallbackFromService {
    public void success(Object response);

    public void failed(Object error);
}
